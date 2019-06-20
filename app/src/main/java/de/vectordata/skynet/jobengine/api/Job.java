package de.vectordata.skynet.jobengine.api;


import java.util.List;
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

    private List<Job> childJobs;

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

    public abstract void onCancel();

    protected final void setResult(R result) {
        this.result = result;
    }

    public final void cancel() {
        onCancel();
        for (Job job : childJobs)
            if (job.getState() == JobState.RUNNING || job.getState() == JobState.SCHEDULED || job.getState() == JobState.SLEEPING)
                job.cancel();
        reportState(JobState.FAILED);
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

    protected final <T> Job<T> startChildJob(Job<T> job) {
        childJobs.add(job);
        return getEngine().schedule(job);
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
