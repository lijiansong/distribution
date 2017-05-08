// lock server tester

#include "lock_protocol.h"
#include "lock_client.h"
#include "rpc.h"
#include <arpa/inet.h>
#include <vector>

// must be >= 2
int nt = 10;
std::string dst;
lock_client **lc = new lock_client *[nt];
lock_protocol::lockid_t a = 1;
lock_protocol::lockid_t b = 2;
lock_protocol::lockid_t c = 3;

// check_grant() and check_release() check that the lock server
// doesn't grant the same lock to both clients.
// it assumes that lock names are distinct in the first byte.
int ct[256];
pthread_mutex_t count_mutex;

void check_grant (lock_protocol::lockid_t lock_id)
{
	pthread_mutex_lock (&count_mutex);
	int x = lock_id & 0xff;
	if (ct[x] != 0)
	{
		fprintf (stderr, "error: server granted %016llx twice\n", lock_id);
		fprintf (stdout, "error: server granted %016llx twice\n", lock_id);
		exit (1);
	}
	ct[x] += 1;
	pthread_mutex_unlock (&count_mutex);
}

void check_release (lock_protocol::lockid_t lock_id)
{
	pthread_mutex_lock (&count_mutex);
	int x = lock_id & 0xff;
	if (ct[x] != 1)
	{
		fprintf (stderr, "error: client released un-held lock %016llx\n", lock_id);
		exit (1);
	}
	ct[x] -= 1;
	pthread_mutex_unlock (&count_mutex);
}

void test1 (void)
{
	printf ("acquire a release a acquire a release a\n");
	lc[0]->acquire (a);
	check_grant (a);
	lc[0]->release (a);
	check_release (a);
	lc[0]->acquire (a);
	check_grant (a);
	lc[0]->release (a);
	check_release (a);

	printf ("acquire a acquire b release b release a \n");
	lc[0]->acquire (a);
	check_grant (a);
	lc[0]->acquire (b);
	check_grant (b);
	lc[0]->release (b);
	check_release (b);
	lc[0]->release (a);
	check_release (a);
}

void *test2 (void *x)
{
	int i = *(int *) x;

	printf ("test2: client %d acquire a release a\n", i);
	lc[i]->acquire (a);
	printf ("test2: client %d acquire done\n", i);
	check_grant (a);
	sleep (1);
	printf ("test2: client %d release\n", i);
	check_release (a);
	lc[i]->release (a);
	printf ("test2: client %d release done\n", i);
	return 0;
}

void *test3 (void *x)
{
	int i = *(int *) x;

	printf ("test3: client %d acquire a release a concurrent\n", i);
	for (int j = 0; j < 10; j++)
	{
		lc[i]->acquire (a);
		check_grant (a);
		printf ("test3: client %d got lock\n", i);
		check_release (a);
		lc[i]->release (a);
	}
	return 0;
}

void *test4 (void *x)
{
	int i = *(int *) x;

	printf ("test4: client %d acquire a release a concurrent; same client\n", i);
	for (int j = 0; j < 10; j++)
	{
		lc[0]->acquire (a);
		check_grant (a);
		printf ("test4: client %d got lock\n", i);
		check_release (a);
		lc[0]->release (a);
	}
	return 0;
}

void *test5 (void *x)
{
	int i = *(int *) x;

	printf ("test5: client %d acquire a release a concurrent; same and diff client\n", i);
	for (int j = 0; j < 10; j++)
	{
		if (i < 5)
			lc[0]->acquire (a);
		else
			lc[1]->acquire (a);
		check_grant (a);
		printf ("test5: client %d got lock\n", i);
		check_release (a);
		if (i < 5)
			lc[0]->release (a);
		else
			lc[1]->release (a);
	}
	return 0;
}

int main (int argc, char *argv[])
{
	int r;
	pthread_t th[nt];
	int test = 0;

	setvbuf (stdout, NULL, _IONBF, 0);
	setvbuf (stderr, NULL, _IONBF, 0);
	srandom (getpid ());

	if (argc < 2)
	{
		fprintf (stderr, "Usage: %s [host:]port [test]\n", argv[0]);
		exit (1);
	}

	dst = argv[1];

	if (argc > 2)
	{
		test = atoi (argv[2]);
		//printf("--------test: %d\n",test);
		if (test < 1 || test > 5)
		{
			printf ("Test number must be between 1 and 5\n");
			exit (1);
		}
	}

	assert (pthread_mutex_init (&count_mutex, NULL) == 0);

	printf ("simple lock client\n");
	for (int i = 0; i < nt; i++)
		lc[i] = new lock_client (dst);

	if (!test || test == 1)
	{
		test1 ();
	}

	if (!test || test == 2)
	{
		// test2
		for (int i = 0; i < nt; i++)
		{
			int *a = new int (i);
			r = pthread_create (&th[i], NULL, test2, (void *) a);
			assert (r == 0);
		}
		for (int i = 0; i < nt; i++)
		{
			pthread_join (th[i], NULL);
		}
	}

	if (!test || test == 3)
	{
		printf ("test 3\n");

		// test3
		for (int i = 0; i < nt; i++)
		{
			int *a = new int (i);
			r = pthread_create (&th[i], NULL, test3, (void *) a);
			assert (r == 0);
		}
		for (int i = 0; i < nt; i++)
		{
			pthread_join (th[i], NULL);
		}
	}

	if (!test || test == 4)
	{
		printf ("test 4\n");

		// test 4
		for (int i = 0; i < 2; i++)
		{
			int *a = new int (i);
			r = pthread_create (&th[i], NULL, test4, (void *) a);
			assert (r == 0);
		}
		for (int i = 0; i < 2; i++)
		{
			pthread_join (th[i], NULL);
		}
	}

	if (!test || test == 5)
	{
		printf ("test 5\n");

		// test 5
		for (int i = 0; i < 10; i++)
		{
			int *a = new int (i);
			r = pthread_create (&th[i], NULL, test5, (void *) a);
			assert (r == 0);
		}
		for (int i = 0; i < 10; i++)
		{
			pthread_join (th[i], NULL);
		}
	}

	printf ("%s: passed all tests!\n", argv[0]);

}
