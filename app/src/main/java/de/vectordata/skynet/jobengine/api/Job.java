package de.vectordata.skynet.jobengine.api;


import java.util.Random;

import de.vectordata.skynet.jobengine.JobEngine;
import de.vectordata.skynet.jobengine.annotations.Retry;
import de.vectordata.skynet.jobengine.await.JobAwaiter;
import de.vectordata.skynet.jobengine.await.ResultAwaiter;
import de.vectordata.skynet.jobengine.await.UpdateAwaiter;
import de.vectordata.skynet.util.Callback;

public abstract class Job<R> {

    private static final Random idRandom = new Random();

    private long id;

    private R result;

    private Retry.Mode retryMode;

    private JobState state;

    private JobProgress progress;

    private JobEngine engine;

    private JobAwaiter<Job<R>> awaiter;

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

    protected final void setResult(R result) {
        this.result = result;
    }

    protected final void reportState(JobState state) {
        if (state != this.state) {
            this.state = state;
            this.engine.onJobUpdated(this);
            if (awaiter != null)
                awaiter.onJobUpdated(this);
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

    protected JobEngine getEngine() {
        return engine;
    }

    public void awaitResult(Callback<Job<R>> resultCallback) {
        awaiter = new ResultAwaiter<>(resultCallback);
    }

    public void awaitUpdate(Callback<Job<R>> jobCallback) {
        awaiter = new UpdateAwaiter<>(jobCallback);
    }

}
