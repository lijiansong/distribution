#ifndef chan_h
#define chan_h 1

// channel abstraction that is used by rpcc and rpcs to communicate.
// this layer understands about connections and multiplexing many send()s
// simultaneously over a connection.
// it uses TCP connections to get good wide-area performance, etc.

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string>
#include <map>
#include "fifo.h"

class tcpchan;

// for rpcs.  one rpcs may have may have many tcpchans, one per rpcc
// that is communitcating with this rpcs.
class schan
{
  private:
	bool debug;
	int lossy_percent;
	struct inbuf;
	int channo;
	bool waiting;

  public:
	  schan (int port, bool _debug = false);
	 ~schan ();

	void recv (std::string & pdu, int &channo);
	void send (std::string pdu, int channo);
	void done (pthread_t myth);
	void setlossy (int p = 5)
	{
		lossy_percent = p;
	}

  private:
	int tcp;

	// map channo to tcpchan (and its associated polling thread)
	struct channel
	{
		pthread_t th;
		tcpchan *ch;
	};
	std::map < int, channel > tcpchans;
	pthread_mutex_t tcpchans_m;
	pthread_cond_t tcpchans_c;

	// input queue. fed by a loop1() per tcpchan.
	// read & waited for by rpc's calls to recv().
	struct inbuf
	{
		inbuf (std::string xs, int xchan):s (xs), channo (xchan)
		{
		}
		inbuf ()
		{
		}
		std::string s;
		int channo;
	};
	fifo < inbuf > inq;

	pthread_t th_tcp_loop;
	void tcp_loop ();
	void tcp_loop1 (int s);
	static void cleanup_tcp_loop1 (void *arg)
	{
		schan *sch = (schan *) arg;
		sch->done (pthread_self ());
	}
	void send_tcp (int channo, std::string pdu);
};

// for rpcc
class cchan
{
  private:
	bool debug;
	int lossy_percent;
	pthread_mutex_t connect_m;
	pthread_mutex_t ch_m;
	pthread_cond_t ch_c;

	pthread_t th;
	sockaddr_in dst;
	tcpchan *ch;
	bool waiting;
	  fifo < std::string > inq;
	bool setup (sockaddr_in dst);
	void tcp_loop ();
	static void cleanup_tcp_loop (void *arg)
	{
		cchan *cch = (cchan *) arg;
		  cch->done ();
	}
  public:
	  cchan (sockaddr_in _dst, bool _debug = false);
	~cchan ();
	void send (std::string pdu);
	std::string recv ();
	void done ();
	void setlossy (int p = 5)
	{
		lossy_percent = p;
	}
};

// internal. one tcp connection (client or server end).
// multiplexes send()s, un-multiplexes on recv().
class tcpchan
{
  private:
	bool debug;
  public:
	tcpchan (int sock, bool _debug = false);
	 ~tcpchan ();

	int setup (sockaddr_in dst);
	void send (std::string);
	  std::string recv ();
	bool dead ()
	{
		return isdead;
	}
	void lose ();
	void die ();

  private:
	int s;
	pthread_t th;
	bool isdead;				// tell owning schan or cchan to stop using this tcpchan

	fifo < std::string > outq;

	int xread (void *p, unsigned int n);
	void do_connect (sockaddr_in dst, short my_port);
	void output_loop ();
	void send1 (std::string);
};

#endif
