package de.vectordata.skynet.ui.chat.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.vectordata.skynet.R;
import de.vectordata.skynet.ui.util.MessageSide;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private final CheckableRecyclerView recyclerView;

    private final List<MessageItem> dataset;

    public MessageAdapter(CheckableRecyclerView recyclerView, List<MessageItem> dataset) {
        this.recyclerView = recyclerView;
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

        View inflated = inflater.inflate(layout, parent, false);
        inflated.setOnClickListener(recyclerView);
        inflated.setOnLongClickListener(recyclerView);
        return new MessageViewHolder(inflated);
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

    public MessageItem getItem(int position) {
        return dataset.get(position);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
