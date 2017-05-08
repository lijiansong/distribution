#ifndef host_h
#define host_h

/* used by inet_addr, not defined on solaris anywhere!? */
#ifndef INADDR_NONE
#define INADDR_NONE ((unsigned long) -1)
#include <strings.h>
#endif

#include <vector>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <ostream>

struct host
{
	host (unsigned int a, unsigned int p)
	{
		addr = a;
		port = p;
	}
	host (char *hname, char *pname);
	host ()
	{
		addr = 0;
		port = 0;
	}
	sockaddr_in sin ()
	{
		sockaddr_in s;
		bzero (&s, sizeof (s));
		s.sin_family = AF_INET;
		s.sin_addr.s_addr = addr;
		s.sin_port = port;
		return s;
	};
	unsigned int addr;
	unsigned int port;
};

inline bool operator== (const host & a, const host & b)
{
	return (a.addr == b.addr) && (a.port == b.port);
}

inline bool operator!= (const host & a, const host & b)
{
	return (a.addr != b.addr) || (a.port != b.port);
}

inline bool operator> (const host & a, const host & b)
{
	return (ntohl (a.addr) > ntohl (b.addr)) || (a.addr == b.addr && ntohs (a.port) > ntohs (b.port));
}

inline bool operator< (const host & a, const host & b)
{
	return (ntohl (a.addr) < ntohl (b.addr)) || (a.addr == b.addr && ntohs (a.port) < ntohs (b.port));
}

std::ostream & operator<< (std::ostream & os, const host h);
std::ostream & operator<< (std::ostream & os, const std::vector < host > v);
bool same_hosts (std::vector < host > a, std::vector < host > b);
bool in_hosts (host h, std::vector < host > v);
std::vector < host > host_union (std::vector < host > a, std::vector < host > b);

#endif
