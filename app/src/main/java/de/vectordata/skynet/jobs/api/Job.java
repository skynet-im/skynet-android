package de.vectordata.skynet.jobs.api;


import java.util.Random;

import de.vectordata.skynet.jobs.JobEngine;
import de.vectordata.skynet.jobs.annotations.Retry;
import de.vectordata.skynet.util.Callback;

public abstract class Job<R> {

    private static final Random idRandom = new Random();

    private long id;

    private R result;

    private Retry.Mode retryMode;

    private JobState state;

    private JobProgress progress;

    private JobEngine engine;

    public Job() {
        id = idRandom.nextLong();
        Retry annotation = getClass().getAnnotation(Retry.class);
        if (annotation != null)
            retryMode = annotation.value();
    }

    public void initialize(JobEngine jobEngine) {
        this.engine = jobEngine;
        this.progress = JobProgress.indeterminate();
        reportState(JobState.RUNNING);
    }

    public abstract void onExecute();

    protected final void reportState(JobState state) {
        if (state != this.state) {
            this.state = state;
            this.engine.onJobUpdated(this);
        }
    }

    protected final void reportProgress(JobProgress progress) {
        if (!progress.equals(this.progress)) {
            this.progress = progress;
            this.engine.onJobUpdated(this);
        }
    }

    public long getId() {
        return id;
    }

    public R getResult() {
        return result;
    }

    public Retry.Mode getRetryMode() {
        return retryMode;
    }

    public JobState getState() {
        return state;
    }

    public JobProgress getProgress() {
        return progress;
    }

    public void awaitResult(Callback<R> result) {

    }

    public void awaitUpdate(Callback<Job<R>> jobCallback) {

    }

}
