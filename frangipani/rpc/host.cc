#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <netdb.h>
#include <arpa/inet.h>
#include "host.h"
#include "rpc.h"

host::host (char *hname, char *pname)
{
	struct sockaddr_in sock;
	make_sockaddr (hname, pname, &sock);
	addr = sock.sin_addr.s_addr;
	port = sock.sin_port;
}

std::ostream & operator<< (std::ostream & os, const host h)
{
	struct in_addr in;
	in.s_addr = h.addr;
	os << inet_ntoa (in) << ":" << ntohs (h.port);
	return os;
}

std::ostream & operator<< (std::ostream & os, const std::vector < host > v)
{
	for (int i = 0; i < (int) v.size (); i++)
		os << v[i] << " ";
	return os;
}

bool same_hosts (std::vector < host > a, std::vector < host > b)
{
	if (a.size () != b.size ())
		return false;
	for (int i = 0; i < (int) a.size (); i++)
	{
		int j;
		for (j = 0; j < (int) b.size (); j++)
			if (a[i] == b[j])
				break;
		if (j >= (int) b.size ())
			return false;
	}
	return true;
}

bool in_hosts (host h, std::vector < host > v)
{
	for (int i = 0; i < (int) v.size (); i++)
		if (v[i] == h)
			return true;
	return false;
}

std::vector < host > host_union (std::vector < host > a, std::vector < host > b)
{
	std::vector < host > c;
	for (unsigned i = 0; i < a.size (); i++)
		if (!in_hosts (a[i], c))
			c.push_back (a[i]);
	for (unsigned i = 0; i < b.size (); i++)
		if (!in_hosts (b[i], c))
			c.push_back (b[i]);
	return c;
}
