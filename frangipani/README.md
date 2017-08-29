## Lock Service
This archieve mainly focuses on the task of Parallel and Distributed Computing @ UCAS-SCCE.
The source code is the implement of a simple lock service. The original RPC library is based on [Mit 6.824 distributed system course lab lib-- frangipani.](https://pdos.csail.mit.edu/archive/6.824)

### Description
Firstly, you are supposed to implement a `lock service`.
The lock service consists of two modules, the lock client and the lock server, which communicate via RPCs.A client requests a specific lock from the lock server by sending an `acquire` request.
The lock server grants the requested lock to one client at a time. When the client is done with the granted lock, it sends a `release` request to the server so the server can grant the lock to another client (if any) waiting to acquire the lock.
Secondly, you'll also augment the provided RPC library to ensure `at-most-once` execution by eliminating duplicate RPC requests. 

### Details
Before implement, you need to make out some terms, such as [remote procedure call（RPC）](https://en.wikipedia.org/wiki/Remote_procedure_call), [rpc semantics...](http://stackoverflow.com/questions/13330067/rpc-semantics-what-exactly-is-the-purpose)

It is easy to implement lock service, more details see the src code [lock_client.h](.lock_client.h), [lock_client.cc](./lock_client.cc), [lock_server.h](./lock_server.h) & [lock_server.cc](./lock_server.cc).

However, to ensure `at-most-once` execution, you need to modify the source code of the `server stub` by remembering the unique request ever received which is uniqued by client id & request id. 
Besides, the server also has to remember the original return value for each RPC request so that the server can re-send it in response to a duplicate request without really executing the RPC handler. 
It guarantees at-most-once, but it is memory-consuming since the memory holding the RPC ids and replies grows indefinitely. 
A better alternative is to use a sliding window of remembered RPCs at the server. 
Such an approach requires the client to generate id in a strict sequence so that the server can safely forget about the some old received RPC and its response.
More details about `at-most-once` execution, see [rpc.h](./rpc.h) & [rpc.cc](./rpc.cc).

### Build
Compile and start up the lock server by giving it a port number on which to listen to RPC requests. You have to choose a port number that other programs aren't using.
```
$ cd src-dir
$ make
$ ./lock_server 3009
```
Open a second terminal and run lock_tester by giving it the port number on which the lock server is listening:
```
$ ./lock_tester 3009
```
The second job is to augment the RPC library to guarantee at-most-once execution. 
You can tell the RPC library to simulate a lossy network by setting the environment variable RPC_LOSSY:
```
$ export RPC_LOSSY=0
$ ./rpctest
```

