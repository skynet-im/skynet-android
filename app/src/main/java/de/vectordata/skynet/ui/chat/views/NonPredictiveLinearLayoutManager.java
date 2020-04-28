package de.vectordata.skynet.ui.chat.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Disable predictive animations
 * See this bug from 2015: https://stackoverflow.com/a/33985508/7702748
 * Which *STILL* occurs in 2020
 * Well done, Android.
 */
public class NonPredictiveLinearLayoutManager extends LinearLayoutManager {
    public NonPredictiveLinearLayoutManager(Context context) {
        super(context);
    }

    public NonPredictiveLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NonPredictiveLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
