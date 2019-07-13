package de.vectordata.skynet.jobengine;


import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import de.vectordata.skynet.event.SyncFinishedEvent;
import de.vectordata.skynet.jobengine.annotations.Retry;
import de.vectordata.skynet.jobengine.api.Job;
import de.vectordata.skynet.jobengine.api.JobState;

public class JobEngine {

    private static final String TAG = "JobEngine";

    private Queue<Job> pendingJobs = new ConcurrentLinkedQueue<>();

    private List<Job> runningJobs = new CopyOnWriteArrayList<>();

    public JobEngine() {
        EventBus.getDefault().register(this);

        RetryController retryController = new RetryController();
        EventBus.getDefault().register(retryController);
    }

    public <T> Job<T> schedule(Job<T> job) {
        pendingJobs.offer(job);
        tryExecuteNext();
        return job;
    }

    public void onJobUpdated(Job job) {
        if (job.getState() == JobState.SUCCESSFUL) {
            if (job.hasEvents())
                EventBus.getDefault().unregister(job);
            runningJobs.remove(job);
        } else if (job.getState() == JobState.FAILED) {
            Log.i(TAG, "Job FAILED: " + job.toString());
            if (job.hasEvents())
                EventBus.getDefault().unregister(job);
            if (job.getRetryMode() == Retry.Mode.INSTANTLY) execute(job);
            else if (job.getRetryMode() == Retry.Mode.NEVER) runningJobs.remove(job);
        }
        tryExecuteNext();
    }

    @Subscribe
    public void onReconnect(SyncFinishedEvent event) {
        // Only one non-sleeping job can be in the running list at the same time.
        // Here, we check if there is a failed one and if it needs retry
        // on reconnect. If so, we execute it.
        for (Job job : runningJobs)
            if (job.getState() == JobState.FAILED && job.getRetryMode() == Retry.Mode.RECONNECT) {
                Log.i(TAG, "Retrying job " + job.toString());
                execute(job);
            }
    }

    boolean isEmpty() {
        return runningJobs.isEmpty() && pendingJobs.isEmpty();
    }

    private void tryExecuteNext() {
        if (hasFreeExecutionSlot()) {
            Job nextJob = pendingJobs.poll();
            if (nextJob != null)
                execute(nextJob);
        }
    }

    private void execute(Job job) {
        if (job.hasEvents())
            EventBus.getDefault().register(job);
        job.initialize(this);
        job.onExecute();
        if (!runningJobs.contains(job))
            runningJobs.add(job);
    }

    private boolean hasFreeExecutionSlot() {
        if (runningJobs.size() == 0) return true;
        for (Job job : runningJobs)
            if (job.getState() != JobState.SLEEPING)
                return false;
        return true;
    }

}
