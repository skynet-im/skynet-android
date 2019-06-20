package de.vectordata.skynet.jobengine.await;

import de.vectordata.skynet.jobengine.api.Job;
import de.vectordata.skynet.util.Callback;

public abstract class JobAwaiter<T extends Job> {

    Callback<T> callback;

    JobAwaiter(Callback<T> callback) {
        this.callback = callback;
    }

    public abstract void onJobUpdated(T job);

}
