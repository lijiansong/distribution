LAB=1
SOL=1

# compile flags
CXXFLAGS =  -g -MD -Wall -DLAB=$(LAB) -DSOL=$(SOL) -D_FILE_OFFSET_BITS=64
FUSEFLAGS= -D_FILE_OFFSET_BITS=64 -DFUSE_USE_VERSION=25 -I/usr/local/include/fuse -I/usr/include/fuse
MACFLAGS= -D__FreeBSD__=10
LDFLAGS = -L. -L/usr/local/lib
LDLIBS = -lrpc -lfuse -lpthread
CC = g++
CXX = g++

lab1: librpc.a rpctest lock_server lock_tester lock_demo

# header files
hfiles1=fifo.h chan.h host.h rpc.h marshall.h method_thread.h lock_protocol.h\
	 lock_server.h lock_client.h

rpclib=rpc.cc host.cc chan.cc log.cc
librpc.a: $(patsubst %.cc,%.o,$(rpclib))
	rm -f $@
	ar cq $@ $^
	ranlib librpc.a

rpctest=rpctest.cc
rpctest: $(patsubst %.cc,%.o,$(rpctest))

# lock demo client
lock_demo=lock_demo.cc lock_client.cc
lock_demo : $(patsubst %.cc,%.o,$(lock_demo))

# lock service test program
lock_tester=lock_tester.cc lock_client.cc
lock_tester : $(patsubst %.cc,%.o,$(lock_tester))
# lock server
lock_server=lock_server.cc lock_smain.cc
lock_server : $(patsubst %.cc,%.o,$(lock_server))


fuse.o: fuse.cc
	$(CXX) -c $(CXXFLAGS) $(FUSEFLAGS) $(MACFLAGS) $<

-include *.d

.PHONY : clean
clean : 
	rm -rf *.o *.d librpc.a lock_server lock_tester lock_demo rpctest 
