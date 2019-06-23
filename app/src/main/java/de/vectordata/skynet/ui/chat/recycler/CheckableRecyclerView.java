package de.vectordata.skynet.ui.chat.recycler;

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

import de.vectordata.skynet.ui.util.OnItemClickListener;

public class CheckableRecyclerView extends RecyclerView implements View.OnClickListener, View.OnLongClickListener {

    private Set<Integer> checkedItems = new HashSet<>();
    private OnItemClickListener onItemClickListener;
    private MultiChoiceListener actionModeCallback;
    private ActionMode actionMode;

    private int lastFirstVisibleItem;

    public CheckableRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CheckableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

        if (checkedItems.isEmpty() && user) {
            actionMode.finish();
            actionMode = null;
        }

        actionModeCallback.onItemCheckedStateChanged(actionMode, position, checked);
        getChildAt(position - getFirstVisiblePosition()).setActivated(checked);
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
        if (actionMode != null) toggleItem(position);
        else if (onItemClickListener != null)
            onItemClickListener.onItemClick(position);
    }

    @Override
    public boolean onLongClick(View v) {
        if (actionMode == null) {
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

        if (lastFirstVisibleItem != currentVisiblePosition)
            for (int i = 0; i < getChildCount(); i++)
                getChildAt(i).setActivated(isItemChecked(i + currentVisiblePosition));

        lastFirstVisibleItem = currentVisiblePosition;
    }

    public int getFirstVisiblePosition() {
        return ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
    }

    public int getLastVisiblePosition() {
        return ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
    }

    public int getCheckedItemCount() {
        return checkedItems.size();
    }

    private void deselectAll() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setActivated(false);
        }
        checkedItems.clear();
    }

}
