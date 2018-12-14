package de.vectordata.skynet.ui.chat.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.skynet.R;
import de.vectordata.skynet.ui.util.MessageSide;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private final List<MessageItem> dataset;

    public MessageAdapter(List<MessageItem> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int layout = viewType == 0 ? R.layout.item_msg_left : R.layout.item_msg_right;
        return new MessageViewHolder(inflater.inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageItem item = dataset.get(position);
        if (item != null)
            holder.configure(item);
    }

    @Override
    public int getItemViewType(int position) {
        MessageItem item = dataset.get(position);
        return item.getMessageSide() == MessageSide.LEFT ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
