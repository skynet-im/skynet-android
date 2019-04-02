package de.vectordata.skynet.jobengine.await;

import de.vectordata.skynet.jobengine.api.Job;
import de.vectordata.skynet.jobengine.api.JobState;
import de.vectordata.skynet.util.Callback;

public class ResultAwaiter<T extends Job> extends JobAwaiter<T> {

    public ResultAwaiter(Callback<T> callback) {
        super(callback);
    }

    @Override
    public void onJobUpdated(T job) {
        if (job.getState() == JobState.SUCCESSFUL)
            callback.onCallback(job);
    }

}
