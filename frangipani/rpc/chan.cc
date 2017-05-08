// GC combined w. exceptions might make threads+locks+delete much easier.

// To Do:
// timeout recommendation
// find out why freebsd runs out of threads, even in wanrpc server
//   maybe -lc_r instead of -lpthread?
// why does it burn up so much CPU? in wanrpc.
// writev (for length + data)
// tcp back-pressure to avoid huge inq

#include <sys/types.h>
#include <arpa/inet.h>
#include <signal.h>
#include <strings.h>
#include <netinet/tcp.h>
#include <unistd.h>
#include "method_thread.h"
#include "chan.h"

static int get_lossy_env ()
{
	char *loss_env = getenv ("RPC_LOSSY");
	if (loss_env != NULL)
	{
		int lp = atoi (loss_env);
		if (lp > 0)
			return lp;
	}
	return 0;
}

cchan::cchan (sockaddr_in _dst, bool _debug):debug (_debug), lossy_percent (0), dst (_dst), ch (0), waiting (false)
{
	assert (pthread_mutex_init (&connect_m, 0) == 0);
	assert (pthread_mutex_init (&ch_m, 0) == 0);
	assert (pthread_cond_init (&ch_c, 0) == 0);

	lossy_percent = get_lossy_env ();
}

cchan::~cchan ()
{
	if (debug)
		printf ("~cchan start\n");

	if (ch)
		assert (pthread_cancel (th) == 0);

	assert (pthread_mutex_lock (&ch_m) == 0);
	if (ch)
	{
		ch->die ();
		waiting = true;
		//    if (debug) printf("~cchan waiting\n");
		assert (pthread_cond_wait (&ch_c, &ch_m) == 0);
	}
	assert (pthread_mutex_unlock (&ch_m) == 0);

	assert (pthread_mutex_destroy (&connect_m) == 0);
	assert (pthread_mutex_destroy (&ch_m) == 0);
	assert (pthread_cond_destroy (&ch_c) == 0);
	if (debug)
		printf ("~cchan done\n");
}

bool cchan::setup (sockaddr_in dst)
{
	int r = false;
	int yes = 1;
	int s;

	assert (pthread_mutex_lock (&connect_m) == 0);

	assert (pthread_mutex_lock (&ch_m) == 0);
	if (ch != 0)
	{
		assert (pthread_mutex_unlock (&ch_m) == 0);
		r = true;
		goto exit;
	}
	assert (pthread_mutex_unlock (&ch_m) == 0);

	s = socket (AF_INET, SOCK_STREAM, 0);
	if (s < 0)
	{
		perror ("tcpchan socket");
		goto exit;
	}
	if (connect (s, (sockaddr *) & dst, sizeof (dst)) < 0)
	{
		fprintf (stderr, "tcpchan connect problem to %s:%d\n", inet_ntoa (dst.sin_addr), (int) ntohs (dst.sin_port));
		perror ("tcpchan connect");
		close (s);
		goto exit;
	}
	setsockopt (s, IPPROTO_TCP, TCP_NODELAY, &yes, sizeof (yes));
	assert (pthread_mutex_lock (&ch_m) == 0);
	ch = new tcpchan (s, debug);
	th = method_thread (this, false, &cchan::tcp_loop);
	assert (pthread_mutex_unlock (&ch_m) == 0);
	r = true;
  exit:
	assert (pthread_mutex_unlock (&connect_m) == 0);
	return r;
}

void cchan::done ()
{
	assert (pthread_mutex_lock (&ch_m) == 0);
	assert (ch);
	delete ch;
	ch = 0;
	if (waiting)
	{
		assert (pthread_cond_signal (&ch_c) == 0);
	}
	assert (pthread_mutex_unlock (&ch_m) == 0);
}

void cchan::send (std::string pdu)
{
	if (setup (dst))
	{
		if (lossy_percent)
		{
			if ((random () % 100) < lossy_percent)
			{
				return;
			}
			if ((random () % 100) < 10)
			{
				sleep (random () % 10);	// delay request
			}
			if ((random () % 100) < lossy_percent)
			{
				ch->send (pdu);
			}
		}
		ch->send (pdu);
	}
}

// copy PDUs from a tcpchan to inq
void cchan::tcp_loop ()
{
	int oldstate;
	assert (pthread_setcancelstate (PTHREAD_CANCEL_ENABLE, &oldstate) == 0);

	pthread_cleanup_push (&cchan::cleanup_tcp_loop, (void *) this);
	while (1)
	{
		std::string pdu = ch->recv ();
		if (ch->dead ())
		{
			break;
		}
		else
		{
			inq.enq (pdu);
		}
	}
	pthread_cleanup_pop (1);
}

std::string cchan::recv ()
{
	return inq.deq ();
}

schan::schan (int port, bool _debug):debug (_debug), lossy_percent (0), waiting (false)
{
	assert (pthread_mutex_init (&tcpchans_m, 0) == 0);
	assert (pthread_cond_init (&tcpchans_c, 0) == 0);

	lossy_percent = get_lossy_env ();

	struct sockaddr_in sin;
	bzero (&sin, sizeof (sin));
	sin.sin_family = AF_INET;
	sin.sin_port = port;

	tcp = socket (AF_INET, SOCK_STREAM, 0);
	if (tcp < 0)
	{
		perror ("socket");
		exit (1);
	}
	int yes = 1;
	setsockopt (tcp, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof (yes));
	setsockopt (tcp, IPPROTO_TCP, TCP_NODELAY, &yes, sizeof (yes));
	if (bind (tcp, (sockaddr *) & sin, sizeof (sin)) < 0)
	{
		perror ("schan tcp bind");
		exit (1);
	}
	if (listen (tcp, 1000) < 0)
	{
		perror ("schan tcp listen");
		exit (1);
	}
	th_tcp_loop = method_thread (this, false, &schan::tcp_loop);
}

schan::~schan ()
{
	if (debug)
		printf ("~schan start\n");
	close (tcp);
	assert (pthread_mutex_lock (&tcpchans_m) == 0);
	std::map < int, channel >::iterator iter;
	for (iter = tcpchans.begin (); iter != tcpchans.end (); iter++)
	{
		tcpchan *ch = iter->second.ch;
		pthread_t th = iter->second.th;
		if (ch)
			ch->die ();
		pthread_cancel (th);
	}
	assert (pthread_mutex_unlock (&tcpchans_m) == 0);
	assert (pthread_cancel (th_tcp_loop) == 0);
	pthread_join (th_tcp_loop, NULL);

	assert (pthread_mutex_lock (&tcpchans_m) == 0);
	while (tcpchans.size () > 0)
	{
		waiting = true;
		if (debug)
			printf ("~schan waiting for tcpchan disappear %u\n", tcpchans.size ());
		assert (pthread_cond_wait (&tcpchans_c, &tcpchans_m) == 0);
	}
	if (debug)
		printf ("~schan after wait\n");
	assert (pthread_mutex_unlock (&tcpchans_m) == 0);

	assert (pthread_mutex_destroy (&tcpchans_m) == 0);
	assert (pthread_cond_destroy (&tcpchans_c) == 0);
	if (debug)
		printf ("~schan done\n");
}

// wait for each new tcp connection, turn it into a tcpchan,
// start a thread to read from that tcpchan.
void schan::tcp_loop ()
{
	int oldstate;
	assert (pthread_setcancelstate (PTHREAD_CANCEL_ENABLE, &oldstate) == 0);

	while (1)
	{
		sockaddr_in sin;
		socklen_t slen = sizeof (sin);
		int s1 = accept (tcp, (sockaddr *) & sin, &slen);
		if (s1 < 0)
		{
			perror ("schan accept");
			break;
		}
		method_thread (this, true, &schan::tcp_loop1, s1);
	}
}

void schan::done (pthread_t myth)
{
	tcpchan *ch = NULL;

	assert (pthread_mutex_lock (&tcpchans_m) == 0);
	std::map < int, channel >::iterator iter;
	for (iter = tcpchans.begin (); iter != tcpchans.end (); iter++)
	{
		pthread_t th = iter->second.th;
		if (th == myth)
		{
			ch = iter->second.ch;
			break;
		}
	}
	assert (ch);
	tcpchans.erase (iter);
	if (waiting)
		assert (pthread_cond_signal (&tcpchans_c) == 0);
	assert (pthread_mutex_unlock (&tcpchans_m) == 0);
	delete ch;
}

// just accept()ed a connection to an schan.
// wait for input on a single tcpchan, add to inq.
// blocks inside tcpchan::recv().
void schan::tcp_loop1 (int s)
{
	pthread_t th = pthread_self ();
	int oldstate;
	assert (pthread_setcancelstate (PTHREAD_CANCEL_ENABLE, &oldstate) == 0);

	int yes = 1;
	setsockopt (s, IPPROTO_TCP, TCP_NODELAY, &yes, sizeof (yes));

	tcpchan *ch = new tcpchan (s, debug);

	assert (pthread_mutex_lock (&tcpchans_m) == 0);
	assert (tcpchans.count (s) == 0);
	tcpchans[s].ch = ch;
	tcpchans[s].th = th;
	assert (pthread_mutex_unlock (&tcpchans_m) == 0);

	pthread_cleanup_push (&schan::cleanup_tcp_loop1, (void *) this);

	while (1)
	{
		std::string pdu = ch->recv ();
		if (ch->dead ())
			break;
		inq.enq (inbuf (pdu, s));
	}
	pthread_cleanup_pop (1);
}

// chan should be passed again to send(), to ensure that
// RPC reply goes back to where the request came from.
void schan::recv (std::string & pdu, int &channo)
{
	inbuf b = inq.deq ();
	pdu = b.s;
	channo = b.channo;
}

void schan::send (std::string pdu, int channo)
{
	tcpchan *ch = 0;
	assert (pthread_mutex_lock (&tcpchans_m) == 0);
	if (tcpchans.count (channo) > 0)
		ch = tcpchans[channo].ch;
	assert (pthread_mutex_unlock (&tcpchans_m) == 0);

	if (!ch)
		return;

	if (lossy_percent)
	{
		if ((random () % 100) < lossy_percent)
		{
			return;
		}
		if ((random () % 100) < 10)
		{
			sleep (random () % 10);	// delay request
		}
		if ((random () % 100) < lossy_percent)
		{
			ch->send (pdu);
		}
	}

	ch->send (pdu);
}

tcpchan::tcpchan (int xs, bool _debug):debug (_debug), s (xs), th (0), isdead (false)
{
	signal (SIGPIPE, SIG_IGN);
	th = method_thread (this, false, &tcpchan::output_loop);
}

tcpchan::~tcpchan ()
{
	close (s);
	assert (pthread_cancel (th) == 0);
	assert (pthread_join (th, NULL) == 0);
}

// output thread
void tcpchan::output_loop ()
{
	// tcpchan::tcpchan() also sets th but we might execute
	// first, and th must be set for die().
	th = pthread_self ();
	int oldstate;
	assert (pthread_setcancelstate (PTHREAD_CANCEL_ENABLE, &oldstate) == 0);

	while (1)
	{
		std::string pdu = outq.deq ();
		send1 (pdu);
	}
}

// output thread
void tcpchan::send1 (std::string pdu)
{
	unsigned long len = htonl (pdu.size ());
	if (write (s, &len, sizeof (len)) != sizeof (len))
	{
		perror ("tcpchan write");
		die ();
	}
	if (write (s, pdu.data (), pdu.size ()) != (ssize_t) pdu.size ())
	{
		perror ("tcpchan write");
		die ();
	}
}

// for debugging -- do something bad to the connection.
void tcpchan::lose ()
{
	int x = (random () % 3);
	if (x == 0)
	{
		shutdown (s, SHUT_RD);
	}
	else if (x == 1)
	{
		shutdown (s, SHUT_WR);
	}
	else
	{
		close (s);
		s = -1;
	}
}

// non-blocking (except for connect)
void tcpchan::send (std::string pdu)
{
	outq.enq (pdu);
}

// read exactly n bytes.
int tcpchan::xread (void *xp, unsigned int n)
{
	char *p = (char *) xp;
	unsigned int i = 0;
	while (i < n)
	{
		int cc = read (s, p + i, n - i);
		if (cc < 0)
		{
			perror ("tcpchan read");
			die ();
			return -1;
		}
		if (cc == 0)
		{
			die ();
			return -1;
		}
		i += cc;
	}
	return 0;
}

// blocks and reads from the tcp socket.
// called inside cchan or schan mutex
std::string tcpchan::recv ()
{
	unsigned long len;

	while (1)
	{
		if (xread (&len, sizeof (len)) != 0)
			return "";
		len = ntohl (len);
		if (len > 10 * 1024 * 1024)
		{
			fprintf (stderr, "tcpchan:recv len %lu too big\n", len);
			die ();
			return "";
		}
		char *p = (char *) malloc (len);
		if (xread (p, len) != 0)
		{
			free (p);
			return "";
		}
		std::string pdu (p, len);
		free (p);
		return pdu;
	}
}

// set isdead, so that this tcpchan and its associated output thread
// will go away.  the thread that reads from this tcpchan (owned by
// schan/cchan) will see a failure because s has been closed, and then
// it will clean up this tcpchan.  when the thread calls the
// destructor, the destructor will terminate the output thread
// associated with this tcpchan.
void tcpchan::die ()
{
	if (debug)
		printf ("%d: die\n", s);
	close (s);
	isdead = true;
}
