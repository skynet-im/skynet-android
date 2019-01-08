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
        MessageSide messageSide = MessageSide.values()[viewType];
        int layout = 0;
        if (messageSide == MessageSide.LEFT) layout = R.layout.item_msg_left;
        else if (messageSide == MessageSide.CENTER) layout = R.layout.item_msg_center;
        else if (messageSide == MessageSide.RIGHT) layout = R.layout.item_msg_right;
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
        return item.getMessageSide().ordinal();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
