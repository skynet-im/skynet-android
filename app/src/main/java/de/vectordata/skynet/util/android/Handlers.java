package de.vectordata.skynet.util.android;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.HashMap;
import java.util.Map;

public final class Handlers {

    public static final String THREAD_NETWORK = "NetworkThread";
    public static final String THREAD_BACKGROUND = "BackgroundThread";
    public static final String THREAD_NOTIFICATIONS = "NotificationThread";

    private static Map<String, Handler> cache = new HashMap<>();

    public static Handler createOnThread(String threadName) {
        Handler handler = cache.get(threadName);
        if (handler != null && handler.getLooper().getThread().isAlive())
            return handler;

        HandlerThread thread = new HandlerThread(threadName);
        thread.start();
        handler = new Handler(thread.getLooper());
        cache.put(threadName, handler);
        return handler;
    }

}
