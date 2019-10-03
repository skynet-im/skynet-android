package de.vectordata.skynet.ui.util;

import android.view.View;

public class KeyboardUtil {

    public static void registerOnKeyboardOpen(View view, Runnable listener) {
        view.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int width = right - left;
            int oldWidth = oldRight - oldLeft;

            if (width != oldWidth) return; // Not a keyboard open, but an orientation change

            if (bottom < oldBottom) view.post(listener);
        });
    }

}
