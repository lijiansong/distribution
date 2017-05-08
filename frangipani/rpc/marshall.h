#ifndef marshall_h
#define marshall_h

#include <sstream>
#include <string>
#include <vector>

class marshall
{
  private:
	std::ostringstream s;
  public:
	marshall ()
	{
	}

	marshall & operator= (marshall & r)
	{
		std::stringbuf * b;
		b = r.s.rdbuf ();
		s << b->str ();
		return *this;
	};

	const std::string str () const
	{
		return s.str ();
	}
	int size ()
	{
		return s.str ().size ();
	}
	void rawbyte (unsigned);
	void rawbytes (const char *, int);
};

marshall & operator<< (marshall &, unsigned int);
marshall & operator<< (marshall &, unsigned long);
marshall & operator<< (marshall &, int);
marshall & operator<< (marshall &, unsigned char);
marshall & operator<< (marshall &, char);
marshall & operator<< (marshall &, unsigned short);
marshall & operator<< (marshall &, short);
marshall & operator<< (marshall &, unsigned long long);
marshall & operator<< (marshall &, const std::string &);

// i32() &c do not directly signal an error.
// call ok() to check that all calls succeeded.
class unmarshall
{
  private:
	std::istringstream s;
	bool _ok;
  public:
	  unmarshall (const std::string & xs):s (xs), _ok (true)
	{
	}
	unmarshall ():_ok (true)
	{
	}
	void str (std::string xs)
	{
		s.str (xs);
	}
	bool ok ()
	{
		return _ok;
	}
	bool okdone ();
	unsigned int rawbyte ();
	std::string rawbytes (unsigned int n);
	unsigned int i32 ();
	unsigned long long i64 ();
	std::string istr ();
};

unmarshall & operator>> (unmarshall &, unsigned char &);
unmarshall & operator>> (unmarshall &, char &);
unmarshall & operator>> (unmarshall &, unsigned short &);
unmarshall & operator>> (unmarshall &, short &);
unmarshall & operator>> (unmarshall &, unsigned int &);
unmarshall & operator>> (unmarshall &, unsigned long &);
unmarshall & operator>> (unmarshall &, int &);
unmarshall & operator>> (unmarshall &, unsigned long long &);
unmarshall & operator>> (unmarshall &, std::string &);

template < class C > marshall & operator<< (marshall & m, std::vector < C > v)
{
	m << (unsigned int) v.size ();
	for (unsigned int i = 0; i < v.size (); i++)
		m << v[i];
	return m;
}

template < class C > unmarshall & operator>> (unmarshall & u, std::vector < C > &v)
{
	int n;
	u >> n;
	for (int i = 0; i < n; i++)
	{
		C z;
		u >> z;
		v.push_back (z);
	}
	return u;
}

#endif
