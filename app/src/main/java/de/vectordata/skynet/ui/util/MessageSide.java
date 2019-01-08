package de.vectordata.skynet.ui.util;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by Twometer on 14.12.2018.
 * (c) 2018 Twometer
 */
public enum MessageSide {
    LEFT,
    RIGHT,
    CENTER;

    public void apply(ImageView view) {
        if (this == LEFT)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);
    }

}
