// RPC stubs for clients to connect & talk to lock server

#include "lock_client.h"
#include "rpc.h"
#include <arpa/inet.h>

#include <sstream>
#include <iostream>
#include <stdio.h>

lock_client::lock_client (std::string dst)
{
	sockaddr_in dstsock;
	make_sockaddr (dst.c_str (), &dstsock);
	cl = new rpcc (dstsock);
	if (cl->bind () < 0)
	{
		printf ("lock_client: call bind\n");
	}
}

lock_client::~lock_client ()
{
	delete cl;
}

int lock_client::stat (lock_protocol::lockid_t lock_id)
{
	int r;
	int ret = cl->call (lock_protocol::stat, cl->id (), lock_id, r);
	assert (ret == lock_protocol::OK);
	return r;
}

//acquire
int lock_client::acquire (lock_protocol::lockid_t lock_id)
{
	int r;
	int ret = cl->call (lock_protocol::acquire, lock_id, r);
	assert (ret == lock_protocol::OK);
	return r;
}

//release
int lock_client::release (lock_protocol::lockid_t lock_id)
{
	int r;
	int ret = cl->call (lock_protocol::release, lock_id, r);
	assert (ret == lock_protocol::OK);
	return r;
}
