// RPC test and pseudo-documentation.
// generates print statements on failures, but eventually says "rpctest OK"

#include "rpc.h"
#include <arpa/inet.h>

rpcs *server;					// server rpc object
rpcc *client;					// client rpc object
int port;
int debug = false;
pthread_attr_t attr;

// server-side handlers. they must be methods of some class
// to simplify rpcs::reg(). a server process can have handlers
// from multiple classes.
class srv
{
  public:
	int handle_22 (const std::string a, const std::string b, std::string & r);
	int handle_fast (const int a, int &r);
	int handle_slow (const int a, int &r);
	int handle_bigrep (const int a, std::string & r);
};

// a handler. a and b are arguments, r is the result.
// there can be multiple arguments but only one result.
// the caller also gets to see the int return value
// as the return value from rpcc::call().
// rpcs::reg() decides how to unmarshall by looking
// at these argument types, so this function definition
// does what a .x file does in SunRPC.
int srv::handle_22 (const std::string a, std::string b, std::string & r)
{
	r = a + b;
	return 0;
}

int srv::handle_fast (const int a, int &r)
{
	r = a + 1;
	return 0;
}

int srv::handle_slow (const int a, int &r)
{
	sleep (1);
	r = a + 2;
	return 0;
}

int srv::handle_bigrep (const int len, std::string & r)
{
	r = std::string ((size_t) len, 'x');
	return 0;
}

void startserver (int port)
{
	// start the server. automatically starts a thread
	// to listen on the UDP port and dispatch calls,
	// each in its own thread. you probably only need
	// one rpcs per process, though it doesn't hurt
	// to have more.
	server = new rpcs (port, debug);

	// register a few server RPC handlers.
	srv service;
	server->reg (22, &service, &srv::handle_22);
	server->reg (23, &service, &srv::handle_fast);
	server->reg (24, &service, &srv::handle_slow);
	server->reg (25, &service, &srv::handle_bigrep);
}

void *client1 (void *xx)
{
	rpcc *c = (rpcc *) xx;

	// test concurrency.
	for (int i = 0; i < 100; i++)
	{
		int arg = (random () % 2000);
		std::string rep;
		c->call (25, arg, rep);
		assert (rep.size () == (size_t) arg);
	}

	// test rpc replies coming back not in the order of
	// the original calls -- i.e. does xid reply dispatch work.
	for (int i = 0; i < 10; i++)
	{
		int which = (random () % 2);
		int arg = (random () % 1000);
		int rep;
		c->call (which ? 23 : 24, arg, rep);
		assert (rep == (which ? arg + 1 : arg + 2));
	}

	return 0;
}

void *client2 (void *xx)
{
	rpcc *c = (rpcc *) xx;

	time_t t1;
	time (&t1);

	while (time (0) - t1 < 10)
	{
		int arg = (random () % 2000);
		std::string rep;
		c->call (25, arg, rep);
		assert (rep.size () == (size_t) arg);
	}
	return 0;
}

void *client3 (void *xx)
{
	rpcc *c = (rpcc *) xx;

	for (int i = 0; i < 4; i++)
	{
		int rep;
		int ret = c->call (24, i, rep, rpcc::to (3000));
		assert (ret == rpc_const::timeout_failure || ret == rpc_const::cancel_failure || rep == i + 2);
	}
	return 0;
}

void simple_tests (rpcc * c)
{
	printf ("simple_tests\n");
	// an RPC call to procedure #22.
	// rpcc::call() looks at the argument types to decide how
	// to marshall the RPC call packet, and how to unmarshall
	// the reply packet.
	std::string rep;
	int intret = c->call (22, "hello", " goodbye", rep);
	assert (intret == 0);		// this is what handle_22 returns
	assert (rep == "hello goodbye");

	// small request, big reply (perhaps req via UDP, reply via TCP)
	intret = c->call (25, 70000, rep, rpcc::to (200000));
	assert (intret == 0);
	assert (rep.size () == 70000);

	// too few arguments
	intret = c->call (22, "just one", rep);
	assert (intret < 0);

	// too many arguments; proc #23 expects just one.
	intret = c->call (23, 1001, 1002, rep);
	assert (intret < 0);

	// wrong return value size
	int wrongrep;
	intret = c->call (23, "hello", " goodbye", wrongrep);
	assert (intret < 0);

	// specify a timeout value to an RPC that should succeed (udp)
	int xx = 0;
	intret = c->call (23, 77, xx, rpcc::to (3000));
	assert (intret == 0 && xx == 78);

	// specify a timeout value to an RPC that should succeed (tcp)
	{
		std::string arg ((size_t) 1000, 'x');
		std::string rep;
		c->call (22, arg, "x", rep, rpcc::to (3000));
		assert (rep.size () == 1001);
	}

	// huge RPC
	std::string big ((size_t) 1000000, 'x');
	intret = c->call (22, big, "z", rep);
	assert (rep.size () == 1000001);

	// specify a timeout value to an RPC that should timeout (udp)
	struct sockaddr_in dst1;
	bzero (&dst1, sizeof (dst1));
	dst1.sin_family = AF_INET;
	dst1.sin_addr.s_addr = inet_addr ("127.0.0.1");
	dst1.sin_port = 7661;
	rpcc *c1 = new rpcc (dst1);
	time_t t0 = time (0);
	intret = c1->bind (rpcc::to (3000));
	time_t t1 = time (0);
	assert (intret < 0 && (t1 - t0) <= 4);
	printf ("simple_tests OK\n");
}

void concurrent_test ()
{
	// create threads that make lots of calls in parallel,
	// to test thread synchronization for concurrent calls
	// and dispatches.
	int nt = 100;
	int ret;

	printf ("start concurrent test (%d threads) ...\n", nt);

	pthread_t th[nt];
	for (int i = 0; i < nt; i++)
	{
		ret = pthread_create (&th[i], &attr, client1, (void *) client);
		assert (ret == 0);
	}

	for (int i = 0; i < nt; i++)
	{
		assert (pthread_join (th[i], NULL) == 0);
	}
	printf ("pass concurrent test!\n");
}

void lossy_test ()
{
	int ret;

	printf ("start lossy test ...\n");
	// test loss, timeout, retransmission.
	client->setlossy (true);
	server->setlossy (true);
	int nt = 10;
	pthread_t th[nt];
	for (int i = 0; i < nt; i++)
	{
		ret = pthread_create (&th[i], &attr, client2, (void *) client);
		assert (ret == 0);
	}
	for (int i = 0; i < nt; i++)
	{
		assert (pthread_join (th[i], NULL) == 0);
	}
	server->setlossy (false);
	printf ("pass lossy test!\n");
}

void failure_test ()
{
	rpcc *client1;

	printf ("failure_test\n");

	delete server;

	// server's ip address.
	struct sockaddr_in dst;
	bzero (&dst, sizeof (dst));
	dst.sin_family = AF_INET;
	dst.sin_addr.s_addr = inet_addr ("127.0.0.1");
	dst.sin_port = port;

	printf ("create new client and try to bind to server\n");
	client1 = new rpcc (dst);
	assert (client1->bind (rpcc::to (3000)) < 0);
	printf("   -- create new client and try to bind to failed server .. failed ok\n");

	delete client1;

	startserver (port);

	std::string rep;
	int intret = client->call (22, (std::string)"hello", (std::string)" goodbye", rep);
	assert (intret == rpc_const::atmostonce_failure);
	printf("   -- call recovered server with old client .. failed ok\n");

	delete client;

	client = new rpcc (dst);
	assert (client->bind () >= 0);
	assert (client->bind () < 0);

	intret = client->call (22, "hello", " goodbye", rep);
	assert (intret == 0);
	assert (rep == "hello goodbye");
	printf("   -- delete existing rpc client, create replacement rpc client .. ok\n");

	int nt = 10;
	int ret;
	printf("   -- concurrent test on new rpc client w/ %d threads ..\n", nt);

	pthread_t th[nt];
	for (int i = 0; i < nt; i++)
	{
		ret = pthread_create (&th[i], &attr, client3, (void *) client);
		assert (ret == 0);
	}

	sleep (1);

	delete server;

	for (int i = 0; i < nt; i++)
	{
		assert (pthread_join (th[i], NULL) == 0);
	}
	printf("ok\n");

	delete client;
	startserver (port);
	client = new rpcc (dst);
	assert (client->bind () >= 0);
	printf("   -- delete existing rpc client and server, create replacements.. ok\n");

	printf("   -- concurrent test on new client and server w/ %d threads ..\n", nt);
	for (int i = 0; i < nt; i++)
	{
		ret = pthread_create (&th[i], &attr, client3, (void *) client);
		assert (ret == 0);
	}

	client->cancel ();			// make all clients of rpcc fail

	for (int i = 0; i < nt; i++)
	{
		assert (pthread_join (th[i], NULL) == 0);
	}

	delete client;				// no other thread is using client; safe to delete

	delete server;
	printf("ok\n");
	printf ("pass failure test!\n");
}

int main (int argc, char *argv[])
{
	setvbuf (stdout, NULL, _IONBF, 0);
	setvbuf (stderr, NULL, _IONBF, 0);

	pthread_attr_init (&attr);
	// set stack size to 32K, so we don't run out of memory
	pthread_attr_setstacksize (&attr, 32 * 1024);

	srandom (getpid ());
	port = htons (20000 + (getpid () % 10000));

	startserver (port);

	// server's address.
	struct sockaddr_in dst;
	bzero (&dst, sizeof (dst));
	dst.sin_family = AF_INET;
	dst.sin_addr.s_addr = inet_addr ("127.0.0.1");
	dst.sin_port = port;

	// start the client.  bind it to the server.
	// starts a thread to listen for replies and hand them to
	// the correct waiting caller thread. there should probably
	// be only one rpcc per process. you probably need one
	// rpcc per server.
	client = new rpcc (dst, debug);
	assert (client->bind () == 0);

	simple_tests (client);
	concurrent_test ();
	lossy_test ();
	failure_test ();

	printf ("pass rpctest!\n");

	exit (0);
}
