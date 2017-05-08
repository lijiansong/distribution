// lock server
// lock client has a similar interface

#ifndef lock_server_h
#define lock_server_h

#include <string>
#include "lock_protocol.h"
#include "lock_client.h"
#include "rpc.h"

class lock_server
{

  private:
	pthread_mutex_t _lock_mutex;//mutex
	pthread_cond_t _lock_cond_var;//condition variable

  protected:
	int nacquire;

	  std::map < lock_protocol::lockid_t, bool > _lock_map;//lock map, whether the lock is owned by some client


  public:
	  lock_server ();
	 ~lock_server ()
	{
	};
	lock_protocol::status stat (int client, lock_protocol::lockid_t, int &);
	lock_protocol::status acquire (lock_protocol::lockid_t, int &);
	lock_protocol::status release (lock_protocol::lockid_t, int &);
};

#endif
