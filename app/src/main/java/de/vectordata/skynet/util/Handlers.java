package de.vectordata.skynet.util;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.HashMap;
import java.util.Map;

public class Handlers {

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
