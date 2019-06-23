package de.vectordata.skynet.ui.chat.recycler;

import androidx.appcompat.view.ActionMode;

public interface MultiChoiceListener extends ActionMode.Callback {

    void onItemCheckedStateChanged(ActionMode mode, int position, boolean checked);

}
