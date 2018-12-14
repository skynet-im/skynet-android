package de.vectordata.skynet.ui.main.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.skynet.R;

/**
 * Created by Twometer on 14.12.2018.
 * (c) 2018 Twometer
 */
public class ChatsAdapter extends RecyclerView.Adapter<ChatsViewHolder> {

    private List<ChatsItem> dataset;

    public ChatsAdapter(List<ChatsItem> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ChatsViewHolder(inflater.inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        ChatsItem item = dataset.get(position);
        if (item != null)
            holder.configure(item);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
