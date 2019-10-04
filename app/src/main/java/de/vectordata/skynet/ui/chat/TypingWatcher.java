package de.vectordata.skynet.ui.chat;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

public class TypingWatcher implements TextWatcher {

    private static final int DURATION_THRESHOLD = 1000;

    private Handler handler;

    private Callback callback;

    private boolean typing;

    private long lastChange;

    TypingWatcher(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!typing) {
            typing = true;
            if (callback != null) callback.onStartTyping();
        }
        lastChange = System.currentTimeMillis();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void run() {
        long duration = System.currentTimeMillis() - lastChange;
        if (duration > DURATION_THRESHOLD && typing) {
            typing = false;
            if (callback != null) callback.onStopTyping();
        }

        handler.postDelayed(this::run, 500);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void start() {
        handler.postDelayed(this::run, 500);
    }

    public interface Callback {

        void onStartTyping();

        void onStopTyping();

    }

}
