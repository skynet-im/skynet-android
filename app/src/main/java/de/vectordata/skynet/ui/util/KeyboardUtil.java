package de.vectordata.skynet.ui.util;

import android.view.View;

public class KeyboardUtil {

    public static void registerOnKeyboardOpen(View view, Runnable listener) {
        view.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) view.post(listener);
        });
    }

}
