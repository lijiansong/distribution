COMM_SRC = gen-cpp//myfirst_constants.cpp \
			gen-cpp//myfirst_types.cpp \
			gen-cpp//MyFirstService.cpp

COMM_FLAGS = -I./gen-cpp -lglog -lthrift -lboost_thread -lboost_system \
			-std=c++11 -pthread -g -O3
COMM_FLAGS_NB = -I./gen-cpp -lglog -lthriftnb -lthrift -levent -lboost_thread -lboost_system \
			-std=c++11 -pthread -g -O3

all: server client

server:
	c++ -o $@.bin MyFirstServer.cpp $(COMM_SRC) $(COMM_FLAGS)
server_nb:
	c++ -o $@.bin MyFirstServer.cpp $(COMM_SRC) $(COMM_FLAGS_NB)
client:
	c++ -o $@.bin MyFirstClient.cpp $(COMM_SRC) $(COMM_FLAGS)

clean:
	rm -rf *.bin *.bin.*
