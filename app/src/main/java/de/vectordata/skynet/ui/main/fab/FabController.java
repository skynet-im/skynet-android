package de.vectordata.skynet.ui.main.fab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FabController implements TabLayout.OnTabSelectedListener {

    private FloatingActionButton floatingActionButton;
    private List<FabState> fabStates = new ArrayList<>();
    private int currentState;

    private FabController(FloatingActionButton floatingActionButton) {
        this.floatingActionButton = floatingActionButton;
    }

    public static FabController with(FloatingActionButton floatingActionButton) {
        return new FabController(floatingActionButton);
    }

    public FabController addState(FabState fabState) {
        this.fabStates.add(fabState);
        return this;
    }

    public FabController setInitialState(int initialState) {
        currentState = initialState;
        FabState state = fabStates.get(currentState);
        if (!state.isVisible()) {
            floatingActionButton.setScaleX(0);
            floatingActionButton.setScaleY(0);
        } else {
            floatingActionButton.setOnClickListener(state.getClickListener());
            floatingActionButton.setImageResource(state.getIcon());
        }
        return this;
    }

    public void attach(TabLayout tabLayout) {
        tabLayout.addOnTabSelectedListener(this);
    }

    private void onStateChange(int state) {
        if (state == currentState)
            return;
        FabState prevState = fabStates.get(currentState);
        FabState nextState = fabStates.get(state);
        floatingActionButton.clearAnimation();

        if (prevState.isVisible() && !nextState.isVisible())
            floatingActionButton.animate().scaleX(0.0f).scaleY(0.0f).setDuration(100).setListener(null);
        else if (prevState.isVisible() && nextState.isVisible()) {
            floatingActionButton.animate().scaleX(0.0f).scaleY(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            floatingActionButton.setImageResource(nextState.getIcon());
                            floatingActionButton.setOnClickListener(nextState.getClickListener());
                            floatingActionButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).setListener(null);
                        }
                    });
        } else if (!prevState.isVisible() && nextState.isVisible()) {
            floatingActionButton.setImageResource(nextState.getIcon());
            floatingActionButton.setOnClickListener(nextState.getClickListener());
            floatingActionButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).setListener(null);
        }

        currentState = state;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        onStateChange(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
