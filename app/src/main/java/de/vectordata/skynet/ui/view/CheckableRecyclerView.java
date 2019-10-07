package de.vectordata.skynet.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.Set;

import de.vectordata.skynet.ui.chat.recycler.MultiChoiceListener;
import de.vectordata.skynet.ui.util.OnItemClickListener;

public class CheckableRecyclerView extends RecyclerView implements View.OnClickListener, View.OnLongClickListener {

    private Set<Integer> checkedItems = new HashSet<>();
    private OnItemClickListener onItemClickListener;
    private MultiChoiceListener actionModeCallback;
    private ActionMode actionMode;

    private CheckableBehavior behavior = CheckableBehavior.LONG_CLICK;

    private int lastVisiblePosition;

    public CheckableRecyclerView(@NonNull Context context) {
        super(context);
        registerListener();
    }

    public CheckableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        registerListener();
    }

    public CheckableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        registerListener();
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null)
            adapter.registerAdapterDataObserver(new AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                    super.onItemRangeChanged(positionStart, itemCount, payload);
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    shiftChecked(itemCount);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    shiftChecked(-itemCount);
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                }
            });
    }

    public void setBehavior(CheckableBehavior behavior) {
        this.behavior = behavior;
    }

    public void setActionModeCallback(MultiChoiceListener actionModeCallback) {
        this.actionModeCallback = actionModeCallback;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isItemChecked(int position) {
        return checkedItems.contains(position);
    }

    public void setItemChecked(int position, boolean checked, boolean user) {
        if (checked) checkedItems.add(position);
        else checkedItems.remove(position);

        if (actionMode != null) {
            if (checkedItems.isEmpty() && user) {
                actionMode.finish();
                actionMode = null;
            }
            actionModeCallback.onItemCheckedStateChanged(actionMode, position, checked);
        }

        setChildActivated(position, checked);
    }

    private void setChildActivated(int position, boolean activated) {
        if (getLayoutManager() != null) {
            View child = getLayoutManager().findViewByPosition(position);
            if (child != null)
                child.setActivated(activated);
        }
    }

    private void shiftChecked(int num) {
        Set<Integer> newChecked = new HashSet<>(checkedItems.size());
        for (int i : checkedItems) newChecked.add(i + num);
        this.checkedItems = newChecked;
    }

    public void toggleItem(int position) {
        if (isItemChecked(position))
            setItemChecked(position, false, true);
        else
            setItemChecked(position, true, true);
    }

    @Override
    public void onClick(View v) {
        int position = getChildAdapterPosition(v);
        if (actionMode != null || behavior == CheckableBehavior.SINGLE_CLICK) toggleItem(position);
        else if (onItemClickListener != null)
            onItemClickListener.onItemClick(position);
    }

    @Override
    public boolean onLongClick(View v) {
        if (actionMode == null && behavior == CheckableBehavior.LONG_CLICK) {
            actionMode = ((AppCompatActivity) getContext()).startSupportActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return actionModeCallback.onCreateActionMode(mode, menu);
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return actionModeCallback.onPrepareActionMode(mode, menu);
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return actionModeCallback.onActionItemClicked(mode, item);
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    actionModeCallback.onDestroyActionMode(mode);
                    deselectAll();
                    actionMode = null;
                }
            });
            int adapterPosition = getChildAdapterPosition(v);
            setItemChecked(adapterPosition, true, true);
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        return true;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        int currentVisiblePosition = getFirstVisiblePosition();

        if (lastVisiblePosition != currentVisiblePosition)
            refreshChecked();

        lastVisiblePosition = currentVisiblePosition;
    }

    private void refreshChecked() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int adapterIdx = getChildAdapterPosition(child);
            child.setActivated(isItemChecked(adapterIdx));
        }
    }

    public int getFirstVisiblePosition() {
        if (getLayoutManager() == null)
            return -1;
        return ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
    }

    public int getLastVisiblePosition() {
        if (getLayoutManager() == null)
            return -1;
        return ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
    }

    public int getCheckedItemCount() {
        return checkedItems.size();
    }

    private void deselectAll() {
        for (int i = 0; i < getChildCount(); i++)
            getChildAt(i).setActivated(false);
        checkedItems.clear();
    }

    private void registerListener() {
        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                view.setOnLongClickListener(CheckableRecyclerView.this);
                view.setOnClickListener(CheckableRecyclerView.this);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                view.setOnClickListener(null);
                view.setOnLongClickListener(null);
            }
        });
    }


}
