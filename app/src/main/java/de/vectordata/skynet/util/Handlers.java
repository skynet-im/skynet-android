package de.vectordata.skynet.util;

import android.os.Handler;
import android.os.HandlerThread;

public class Handlers {

    public static Handler createOnThread(String threadName) {
        HandlerThread thread = new HandlerThread(threadName);
        thread.start();
        return new Handler(thread.getLooper());
    }

}
