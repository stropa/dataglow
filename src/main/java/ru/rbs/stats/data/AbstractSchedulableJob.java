package ru.rbs.stats.data;

import ru.rbs.stats.configuration.Schedulable;

import java.time.LocalDateTime;

public abstract class AbstractSchedulableJob implements Schedulable {
    protected Long periodSeconds;
    protected Runnable job;
    private LocalDateTime lastRun;

    public long getPeriodSeconds() {
        return periodSeconds;
    }

    public void setPeriodSeconds(long periodSeconds) {
        this.periodSeconds = periodSeconds;
    }

    @Override
    public void setJob(Runnable job) {
        this.job = job;
    }

    @Override
    public LocalDateTime getLastRun() {
        return lastRun;
    }

    @Override
    public void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }

    @Override
    public void runJob() {
        job.run();
    }
}
