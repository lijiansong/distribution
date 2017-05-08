// the lock server implementation

#include "lock_server.h"
#include <sstream>
#include <map>
#include <stdio.h>
#include <unistd.h>
#include <arpa/inet.h>

lock_server::lock_server ():
nacquire (0)
{
	pthread_mutex_init (&_lock_mutex, NULL);
	pthread_cond_init (&_lock_cond_var, NULL);
}

lock_protocol::status lock_server::stat (int client, lock_protocol::lockid_t lid, int &r)
{
	lock_protocol::status ret = lock_protocol::OK;
	printf ("stat request from client %d\n", client);
	r = nacquire;
	return ret;
}

//acquire
lock_protocol::status lock_server::acquire (lock_protocol::lockid_t lock_id, int &r)
{
	lock_protocol::status ret = lock_protocol::OK;
	pthread_mutex_lock (&_lock_mutex);
	while (_lock_map.count (lock_id) != 0 && _lock_map[lock_id])
	{
		//wait until other clients release the lock
		pthread_cond_wait (&_lock_cond_var, &_lock_mutex);
	}
	//if the lock is released, set the lock being locked,
	//and grant it to the waiting client
	if (!_lock_map[lock_id])
	{
		_lock_map[lock_id] = true;
	}
	else
	{
		ret = lock_protocol::RPCERR;
	}

	pthread_mutex_unlock (&_lock_mutex);

	return ret;
}

//release
lock_protocol::status lock_server::release (lock_protocol::lockid_t lock_id, int &r)
{
	lock_protocol::status ret = lock_protocol::OK;
	pthread_mutex_lock (&_lock_mutex);
	//set the lock being free
	//and notify the threads that are waiting for the lock
	if (_lock_map.count (lock_id) != 0 && _lock_map[lock_id])
	{
		_lock_map[lock_id] = false;
		pthread_cond_broadcast (&_lock_cond_var);
	}
	else
	{
		ret = lock_protocol::RPCERR;
	}
	pthread_mutex_unlock (&_lock_mutex);

	return ret;
}
