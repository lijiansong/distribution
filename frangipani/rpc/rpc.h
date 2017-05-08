#ifndef rpc_h
#define rpc_h

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <map>
#include <list>
#include <cstring>
#include <cstdlib>
#include <stdio.h>
#include <unistd.h>
#include "marshall.h"
#include "chan.h"

class rpc_const
{
  public:
	static const unsigned int bind = 1;	// handler number reserved for bind
	static const int timeout_failure = -1;
	static const int atmostonce_failure = -2;
	static const int unmarshal_failure = -3;
	static const int bind_failure = -4;
	static const int cancel_failure = -5;
};
// rpc client endpoint.
// manages a socket and an xid space.
// threaded: multiple threads can be sending RPCs,
// and this code gives each reply to the right thread.
class rpcc
{
  private:
	sockaddr_in dst;			// address of server
	bool debug;
	unsigned int xid;			// xid for next request/call
	// xids of replies that clnt hasn't acknowledged
	  std::list < unsigned int >xid_rep_window;

	int svr_nonce;
	int clt_nonce;
	bool bind_done;
	cchan chan;
	class vivaldi *_vivaldi;

	// clock loop data for tight timeouts
	pthread_mutex_t _timeout_lock;
	pthread_cond_t _timeout_cond;
	struct timespec _next_timeout;
	static void cleanup_timeout_lock (void *arg)
	{
		pthread_mutex_t *l = (pthread_mutex_t *) arg;
		  pthread_mutex_unlock (l);
	};

	// map xid of awaited reply to waiting thread in call()
	//manages per rpc info
	struct caller
	{
		caller (int xxid, unmarshall * un, uint32_t ip, uint16_t port);
		 ~caller ();
		int xid;
		unmarshall *un;
		int intret;
		bool done;
		pthread_cond_t c;
		pthread_mutex_t m;
		uint32_t other_ip;
		uint16_t other_port;
	};
	std::map < int, caller * >calls;
	pthread_mutex_t m;			// protect insert/delete to calls[]
	pthread_cond_t destroy_wait_c;
	bool destroy_wait;

	pthread_t th_chan_loop;
	pthread_t th_clock_loop;
	void chan_loop ();
	void clock_loop ();
	void got_reply (unmarshall & rep);
	void update_xid_rep (unsigned int xid);

  public:
	rpcc (sockaddr_in _dst, bool _debug = false);
	~rpcc ();

	void setlossy (bool x);
	void set_vivaldi (vivaldi * v)
	{
		_vivaldi = v;
	}

	// hack to allow optional timeout argument to call().
	struct TO
	{
		int to;
	};
	static const TO to_inf;
	static TO to (int x)
	{
		TO t;
		t.to = x;
		return t;
	}

	int bind (TO to = to_inf);
	int id ()
	{
		return clt_nonce;
	}
	void cancel ();
	int call1 (unsigned int proc, const marshall & req, unmarshall & rep, TO to);

	template < class R > int call (unsigned int proc, R & r, TO to = to_inf);
	template < class R, class A1 > int call (unsigned int proc, const A1 & a1, R & r, TO to = to_inf);
	template < class R, class A1, class A2 > int call (unsigned int proc, const A1 & a1, const A2 & a2, R & r, TO to = to_inf);
	template < class R, class A1, class A2, class A3 > int call (unsigned int proc, const A1 & a1, const A2 & a2, const A3 & a3, R & r, TO to = to_inf);
	template < class R, class A1, class A2, class A3, class A4 > int call (unsigned int proc, const A1 & a1, const A2 & a2, const A3 & a3, const A4 & a4, R & r, TO to = to_inf);
	template < class R, class A1, class A2, class A3, class A4, class A5 >
		int call (unsigned int proc, const A1 & a1, const A2 & a2, const A3 & a3, const A4 & a4, const A5 & a5, R & r, TO to = to_inf);
};

template < class R > int rpcc::call (unsigned int proc, R & r, TO to)
{
	marshall m;
	unmarshall u;
	int intret = call1 (proc, m, u, to);
	if (intret >= 0)
	{
		u >> r;
		if (u.okdone () != true)
			return rpc_const::unmarshal_failure;
	}
	return intret;
}

template < class R, class A1 > int rpcc::call (unsigned int proc, const A1 & a1, R & r, TO to)
{
	marshall m;
	unmarshall u;
	m << a1;
	int intret = call1 (proc, m, u, to);
	if (intret >= 0)
	{
		u >> r;
		if (u.okdone () != true)
			return rpc_const::unmarshal_failure;
	}
	return intret;
}

template < class R, class A1, class A2 > int rpcc::call (unsigned int proc, const A1 & a1, const A2 & a2, R & r, TO to)
{
	marshall m;
	unmarshall u;
	m << a1;
	m << a2;
	int intret = call1 (proc, m, u, to);
	if (intret >= 0)
	{
		u >> r;
		if (u.okdone () != true)
			return rpc_const::unmarshal_failure;
	}
	return intret;
}

template < class R, class A1, class A2, class A3 > int rpcc::call (unsigned int proc, const A1 & a1, const A2 & a2, const A3 & a3, R & r, TO to)
{
	marshall m;
	unmarshall u;
	m << a1;
	m << a2;
	m << a3;
	int intret = call1 (proc, m, u, to);
	if (intret >= 0)
	{
		u >> r;
		if (u.okdone () != true)
			return rpc_const::unmarshal_failure;
	}
	return intret;
}

template < class R, class A1, class A2, class A3, class A4 > int rpcc::call (unsigned int proc, const A1 & a1, const A2 & a2, const A3 & a3, const A4 & a4, R & r, TO to)
{
	marshall m;
	unmarshall u;
	m << a1;
	m << a2;
	m << a3;
	m << a4;
	int intret = call1 (proc, m, u, to);
	if (intret >= 0)
	{
		u >> r;
		if (u.okdone () != true)
			return rpc_const::unmarshal_failure;
	}
	return intret;
}

template < class R, class A1, class A2, class A3, class A4, class A5 > int rpcc::call (unsigned int proc, const A1 & a1, const A2 & a2, const A3 & a3, const A4 & a4, const A5 & a5, R & r, TO to)
{
	marshall m;
	unmarshall u;
	m << a1;
	m << a2;
	m << a3;
	m << a4;
	m << a5;
	int intret = call1 (proc, m, u, to);
	if (intret >= 0)
	{
		u >> r;
		if (u.okdone () != true)
			return rpc_const::unmarshal_failure;
	}
	return intret;
}

class handler
{
  public:
	handler ();
	virtual ~ handler ()
	{
	}
	virtual int fn (unmarshall &, marshall &) = 0;
};

// rpc server endpoint.
class rpcs
{
  private:
	bool debug;
	schan chan;
	bool lossy;					// debug: drop some requests and replies
	int lossy_percent;			// percentage of packets to drop if lossy is true
	class vivaldi *_vivaldi;

	// deleting rpcs:
	pthread_t th_loop;
	int nthread;
	bool deleting;
	pthread_mutex_t delete_m;
	pthread_cond_t delete_c;

	// provide at most once semantics by maintaining a window of replies
	// per client that that client hasn't ackwnowledged receiving yet.
	pthread_mutex_t reply_window_m;	// protect reply window et al
	int nonce;
	// state about an in-progress or completed RPC, for at-most-once.
    // if rep_present is true, then the RPC is complete and a reply
    // has been sent; in that case buf points to a copy of the reply,
    // and sz holds the size of the reply.
	struct reply_t
	{
		reply_t (unsigned int _xid)
		{
			xid = _xid;
			rep_present = false;
			// buf=NULL;
			// sz=0;
		}
		bool rep_present;
		marshall rep;
		unsigned int xid;
		//char *buf;      // the reply buffer
		//int sz;         // the size of reply buffer
	};

	// provide at most once semantics by maintaining a window of replies
	// per client that that client hasn't acknowledged receiving yet.
    // indexed by client nonce.
	std::map < unsigned int, std::list < reply_t * > >reply_window;
	void free_reply_window (void);
	void add_reply (unsigned int clt_nonce, unsigned int xid, marshall & rep);
	typedef enum
	{
		NEW,					// new RPC, not a duplicate
		INPROGRESS,				// duplicate of an RPC we're still processing
		DONE,					// duplicate of an RPC we already replied to (have reply)
		FORGOTTEN,				// duplicate of an old RPC whose reply we've forgotten
	} rpcstate_t;
	rpcstate_t checkduplicate_and_update (unsigned int clnt_nonce, unsigned int xid, unsigned int rep_xid, marshall & rep);

	// counting
	int counting;
	std::map < int, int >counts;

	// map proc # to function
	std::map < int, handler * >procs;
	pthread_mutex_t procs_m;	// protect insert/delete to procs[]
	void updatestat (unsigned int proc);
	void loop ();
	void dec_nthread ();
	void inc_nthread ();
  protected:
	//void dispatch(std::string, sockaddr_in); // -> compiler error
	// struct djob_t 
	// {
	// 	djob_t (connection *c, char *b, int bsz):buf(b),sz(bsz),conn(c) {}
	// 	char *buf;
	// 	int sz;
	// 	connection *conn;
	// };
	struct junk
	{
		junk (std::string, int);
		std::string s;
		int chan;
	};
	void dispatch (junk *);

  public:
	rpcs (unsigned int port, bool _debug = false);
	~rpcs ();

	void set_vivaldi (vivaldi * v)
	{
		_vivaldi = v;
	}

	int bind (int a, int &r);

	// internal handler registration
	void reg1 (unsigned int proc, handler *);

	// register a handler
	template < class S, class A1, class R > void reg (unsigned int proc, S *, int (S::*meth) (const A1 a1, R & r));
	template < class S, class A1, class A2, class R > void reg (unsigned int proc, S *, int (S::*meth) (const A1 a1, const A2, R & r));
	template < class S, class A1, class A2, class A3, class R > void reg (unsigned int proc, S *, int (S::*meth) (const A1, const A2, const A3, R & r));
	template < class S, class A1, class A2, class A3, class A4, class R > void reg (unsigned int proc, S *, int (S::*meth) (const A1, const A2, const A3, const A4, R & r));
	template < class S, class A1, class A2, class A3, class A4, class A5, class R > void reg (unsigned int proc, S *, int (S::*meth) (const A1, const A2, const A3, const A4, const A5, R & r));

	void setlossy (bool);
};

template < class S, class A1, class R > void rpcs::reg (unsigned int proc, S * sob, int (S::*meth) (const A1 a1, R & r))
{
	class h1:public handler
	{
	  private:
		S * sob;
		int (S::*meth) (const A1 a1, R & r);
	  public:
		h1 (S * xsob, int (S::*xmeth) (const A1 a1, R & r)):sob (xsob), meth (xmeth)
		{
		}
		int fn (unmarshall & args, marshall & ret)
		{
			A1 a1;
			R r;
			args >> a1;
			if (!args.okdone ())
				return rpc_const::unmarshal_failure;
			int b = (sob->*meth) (a1, r);
			ret << r;
			return b;
		}
	};
	reg1 (proc, new h1 (sob, meth));
}

template < class S, class A1, class A2, class R > void rpcs::reg (unsigned int proc, S * sob, int (S::*meth) (const A1 a1, const A2 a2, R & r))
{
	class h1:public handler
	{
	  private:
		S * sob;
		int (S::*meth) (const A1 a1, const A2 a2, R & r);
	  public:
		h1 (S * xsob, int (S::*xmeth) (const A1 a1, const A2 a2, R & r)):sob (xsob), meth (xmeth)
		{
		}
		int fn (unmarshall & args, marshall & ret)
		{
			A1 a1;
			A2 a2;
			R r;
			args >> a1;
			args >> a2;
			if (!args.okdone ())
				return rpc_const::unmarshal_failure;
			int b = (sob->*meth) (a1, a2, r);
			ret << r;
			return b;
		}
	};
	reg1 (proc, new h1 (sob, meth));
}

template < class S, class A1, class A2, class A3, class R > void rpcs::reg (unsigned int proc, S * sob, int (S::*meth) (const A1 a1, const A2 a2, const A3 a3, R & r))
{
	class h1:public handler
	{
	  private:
		S * sob;
		int (S::*meth) (const A1 a1, const A2 a2, const A3 a3, R & r);
	  public:
		h1 (S * xsob, int (S::*xmeth) (const A1 a1, const A2 a2, const A3 a3, R & r)):sob (xsob), meth (xmeth)
		{
		}
		int fn (unmarshall & args, marshall & ret)
		{
			A1 a1;
			A2 a2;
			A3 a3;
			R r;
			args >> a1;
			args >> a2;
			args >> a3;
			if (!args.okdone ())
				return rpc_const::unmarshal_failure;
			int b = (sob->*meth) (a1, a2, a3, r);
			ret << r;
			return b;
		}
	};
	reg1 (proc, new h1 (sob, meth));
}

template < class S, class A1, class A2, class A3, class A4, class R > void rpcs::reg (unsigned int proc, S * sob, int (S::*meth) (const A1 a1, const A2 a2, const A3 a3, const A4 a4, R & r))
{
	class h1:public handler
	{
	  private:
		S * sob;
		int (S::*meth) (const A1 a1, const A2 a2, const A3 a3, const A4 a4, R & r);
	  public:
		h1 (S * xsob, int (S::*xmeth) (const A1 a1, const A2 a2, const A3 a3, const A4 a4, R & r)):sob (xsob), meth (xmeth)
		{
		}
		int fn (unmarshall & args, marshall & ret)
		{
			A1 a1;
			A2 a2;
			A3 a3;
			A4 a4;
			R r;
			args >> a1;
			args >> a2;
			args >> a3;
			args >> a4;
			if (!args.okdone ())
				return rpc_const::unmarshal_failure;
			int b = (sob->*meth) (a1, a2, a3, a4, r);
			ret << r;
			return b;
		}
	};
	reg1 (proc, new h1 (sob, meth));
}

template < class S, class A1, class A2, class A3, class A4, class A5, class R > void
	rpcs::reg (unsigned int proc, S * sob, int (S::*meth) (const A1 a1, const A2 a2, const A3 a3, const A4 a4, const A5 a5, R & r))
{
	class h1:public handler
	{
	  private:
		S * sob;
		int (S::*meth) (const A1 a1, const A2 a2, const A3 a3, const A4 a4, const A5 a5, R & r);
	  public:
		  h1 (S * xsob, int (S::*xmeth) (const A1 a1, const A2 a2, const A3 a3, const A4 a4, const A5 a5, R & r)):sob (xsob), meth (xmeth)
		{
		}
		int fn (unmarshall & args, marshall & ret)
		{
			A1 a1;
			A2 a2;
			A3 a3;
			A4 a4;
			A5 a5;
			R r;
			args >> a1;
			args >> a2;
			args >> a3;
			args >> a4;
			args >> a5;
			if (!args.okdone ())
				return rpc_const::unmarshal_failure;
			int b = (sob->*meth) (a1, a2, a3, a4, a5, r);
			ret << r;
			return b;
		}
	};
	reg1 (proc, new h1 (sob, meth));
}

void make_sockaddr (const char *hostandport, struct sockaddr_in *dst);
void make_sockaddr (const char *host, const char *port, struct sockaddr_in *dst);

#endif
