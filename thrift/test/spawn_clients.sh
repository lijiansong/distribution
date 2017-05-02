#! /bin/bash

for i in `seq 5`; do
    GLOG_logtostderr=1 ./client.bin &
done

