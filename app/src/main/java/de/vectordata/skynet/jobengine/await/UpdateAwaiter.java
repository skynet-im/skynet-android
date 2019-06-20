package de.vectordata.skynet.jobengine.await;

import de.vectordata.skynet.jobengine.api.Job;
import de.vectordata.skynet.util.Callback;

public class UpdateAwaiter<T extends Job> extends JobAwaiter<T> {

    public UpdateAwaiter(Callback<T> callback) {
        super(callback);
    }

    @Override
    public void onJobUpdated(T job) {
        callback.onCallback(job);
    }

}
