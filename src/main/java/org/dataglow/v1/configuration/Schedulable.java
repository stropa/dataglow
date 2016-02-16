package org.dataglow.v1.configuration;

import java.time.LocalDateTime;

public interface Schedulable {

    LocalDateTime getLastRun();

    void setLastRun(LocalDateTime lastRun);

    long getPeriodSeconds();

    void setPeriodSeconds(long periodSeconds);

    void setJob(Runnable job);

    void runJob();
}
