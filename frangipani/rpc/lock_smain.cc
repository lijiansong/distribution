// unmarshall RPCs from lock_smain and hand them to lock_server

#include "rpc.h"
#include <arpa/inet.h>
#include <signal.h>
#include "lock_server.h"

static void force_exit(int) 
{
  exit(0);
}

int main (int argc, char *argv[])
{
	int count = 0;
	// force the lock_server to exit after 20 minutes
	signal(SIGALRM, force_exit);
	alarm(20 * 60);

	setvbuf (stdout, NULL, _IONBF, 0);
	setvbuf (stderr, NULL, _IONBF, 0);

	srandom (getpid ());

	if (argc != 2)
	{
		fprintf (stderr, "Usage: %s port\n", argv[0]);
		exit (1);
	}

	char *count_env = getenv("RPC_COUNT");
	if(count_env != NULL)
	{
		count = atoi(count_env);
	}

	lock_server _lock_server;//lock server
	rpcs server (htons (atoi (argv[1])));
	//register different proceduces at the lock server
	server.reg (lock_protocol::stat, &_lock_server, &lock_server::stat);
	server.reg (lock_protocol::acquire, &_lock_server, &lock_server::acquire);
	server.reg (lock_protocol::release, &_lock_server, &lock_server::release);

	while (1) sleep (1000);
}
