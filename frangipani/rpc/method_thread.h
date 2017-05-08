#ifndef method_thread_h
#define method_thread_h

// method_thread(): start a thread that runs an object method.
// returns a pthread_t on success, and zero on error.

#include <assert.h>
#include <pthread.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

static pthread_t method_thread_parent (void *(*fn) (void *), void *arg, bool detach)
{
	pthread_t th;
	int err = pthread_create (&th, NULL, fn, arg);
	if (err != 0)
	{
		fprintf (stderr, "pthread_create ret %d %s\n", err, strerror (err));
		exit (1);
	}

	if (detach)
	{
		// don't keep thread state around after exit, to avoid
		// running out of threads. set detach==false if you plan
		// to pthread_join.
		assert (pthread_detach (th) == 0);
	}

	return th;
}

static void method_thread_child ()
{
	// defer pthread_cancel() by default. check explicitly by
	// enabling then pthread_testcancel().
	int oldstate, oldtype;
	assert (pthread_setcancelstate (PTHREAD_CANCEL_DISABLE, &oldstate) == 0);
	assert (pthread_setcanceltype (PTHREAD_CANCEL_DEFERRED, &oldtype) == 0);
}

template < class C > pthread_t method_thread (C * o, bool detach, void (C::*m) ())
{
	class XXX
	{
	  public:
		C * o;
		void (C::*m) ();
		static void *yyy (void *vvv)
		{
			XXX *x = (XXX *) vvv;
			C *o = x->o;
			void (C::*m) () = x->m;
			delete x;
			  method_thread_child ();
			  (o->*m) ();
			  return 0;
		}
	};
	XXX *x = new XXX;
	x->o = o;
	x->m = m;
	return method_thread_parent (&XXX::yyy, (void *) x, detach);
}

template < class C, class A > pthread_t method_thread (C * o, bool detach, void (C::*m) (A), A a)
{
	class XXX
	{
	  public:
		C * o;
		void (C::*m) (A a);
		A a;
		static void *yyy (void *vvv)
		{
			XXX *x = (XXX *) vvv;
			C *o = x->o;
			void (C::*m) (A) = x->m;
			A a = x->a;
			delete x;
			  method_thread_child ();
			  (o->*m) (a);
			  return 0;
		}
	};
	XXX *x = new XXX;
	x->o = o;
	x->m = m;
	x->a = a;
	return method_thread_parent (&XXX::yyy, (void *) x, detach);
}

template < class C, class A1, class A2 > pthread_t method_thread (C * o, bool detach, void (C::*m) (A1, A2), A1 a1, A2 a2)
{
	class XXX
	{
	  public:
		C * o;
		void (C::*m) (A1 a1, A2 a2);
		A1 a1;
		A2 a2;
		static void *yyy (void *vvv)
		{
			XXX *x = (XXX *) vvv;
			C *o = x->o;
			void (C::*m) (A1, A2) = x->m;
			A1 a1 = x->a1;
			A2 a2 = x->a2;
			delete x;
			  method_thread_child ();
			  (o->*m) (a1, a2);
			  return 0;
		}
	};
	XXX *x = new XXX;
	x->o = o;
	x->m = m;
	x->a1 = a1;
	x->a2 = a2;
	return method_thread_parent (&XXX::yyy, (void *) x, detach);
}

template < class C, class A1, class A2, class A3 > pthread_t method_thread (C * o, bool detach, void (C::*m) (A1, A2, A3), A1 a1, A2 a2, A3 a3)
{
	class XXX
	{
	  public:
		C * o;
		void (C::*m) (A1 a1, A2 a2, A3 a3);
		A1 a1;
		A2 a2;
		A3 a3;
		static void *yyy (void *vvv)
		{
			XXX *x = (XXX *) vvv;
			C *o = x->o;
			void (C::*m) (A1, A2, A3) = x->m;
			A1 a1 = x->a1;
			A2 a2 = x->a2;
			A3 a3 = x->a3;
			delete x;
			  method_thread_child ();
			  (o->*m) (a1, a2, a3);
			  return 0;
		}
	};
	XXX *x = new XXX;
	x->o = o;
	x->m = m;
	x->a1 = a1;
	x->a2 = a2;
	x->a3 = a3;
	return method_thread_parent (&XXX::yyy, (void *) x, detach);
}

#endif
