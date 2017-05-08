#ifndef _LOG_H
#define _LOG_H

#include <cstdio>
#include <string>
#include <pthread.h>

class Logger
{
private:
    FILE *fp;
    bool concurrent;
    pthread_mutex_t mutex;

public:
    Logger(const char *logfile, bool concurrent=false);
    ~Logger();

    void log(const char *fmt, ...);
};

#endif
