#ifndef fifo_h
#define fifo_h

// fifo template
// synchronized with mutex and cond

#include <assert.h>
#include <errno.h>
#include <list>
#include <sys/time.h>
#include <time.h>

template < class T > class fifo
{
  public:
	fifo ();
	~fifo ();
	void enq (T);
	T deq ();
  private:
	std::list < T > q;
	pthread_mutex_t m;
	pthread_cond_t c;			// q went non-empty
	static void cleanup_lock (void *arg)
	{
		pthread_mutex_t *l = (pthread_mutex_t *) arg;
		pthread_mutex_unlock (l);
	};
};

template < class T > fifo < T >::fifo ()
{
	assert (pthread_mutex_init (&m, 0) == 0);
	assert (pthread_cond_init (&c, 0) == 0);
}

template < class T > fifo < T >::~fifo ()
{
	assert (pthread_mutex_destroy (&m) == 0);
	assert (pthread_cond_destroy (&c) == 0);
}

template < class T > void fifo < T >::enq (T e)
{
	assert (pthread_mutex_lock (&m) == 0);
	q.push_back (e);
	assert (pthread_cond_broadcast (&c) == 0);
	assert (pthread_mutex_unlock (&m) == 0);
}

template < class T > T fifo < T >::deq ()
{
	while (1)
	{
		bool gotone = false;
		T e;

		assert (pthread_mutex_lock (&m) == 0);
		if (q.empty ())
		{
			pthread_cleanup_push (&fifo::cleanup_lock, (void *) &m);
			assert (pthread_cond_wait (&c, &m) == 0);
			pthread_cleanup_pop (0);
		}
		else
		{
			e = q.front ();
			q.pop_front ();
			gotone = true;
		}
		assert (pthread_mutex_unlock (&m) == 0);

		if (gotone)
			return e;
	}
}

#endif
