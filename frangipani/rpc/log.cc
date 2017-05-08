#include "log.h"
#include <cstdarg>
#include <ctime>

Logger::Logger(const char *logfile, bool concurrent) : concurrent(concurrent)
{
    fp = fopen(logfile, "w");
    if (concurrent) {
        pthread_mutex_init(&mutex, NULL);
    }
}

Logger::~Logger()
{
    fclose(fp);
}

void Logger::log(const char *fmt, ...)
{
    if (concurrent)
        pthread_mutex_lock(&mutex);
    va_list vl;
    fprintf(fp, "[%ld]: ", time(NULL));
    va_start(vl, fmt);
    vfprintf(fp, fmt, vl);
    va_end(vl);
    fflush(fp);
    if (concurrent)
        pthread_mutex_unlock(&mutex);
}

