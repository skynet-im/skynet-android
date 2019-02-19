package de.vectordata.skynet.ui.main.fab;

import android.view.View;

public class FabState {

    private boolean visible;

    private int icon;

    private View.OnClickListener clickListener;

    public FabState(int icon, View.OnClickListener clickListener) {
        this.visible = true;
        this.icon = icon;
        this.clickListener = clickListener;
    }

    public static FabState invisible() {
        FabState state = new FabState(0, null);
        state.visible = false;
        return state;
    }

    boolean isVisible() {
        return visible;
    }

    int getIcon() {
        return icon;
    }

    View.OnClickListener getClickListener() {
        return clickListener;
    }
}
