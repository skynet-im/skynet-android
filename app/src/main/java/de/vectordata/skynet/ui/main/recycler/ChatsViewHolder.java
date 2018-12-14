package de.vectordata.skynet.ui.main.recycler;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;

/**
 * Created by Twometer on 14.12.2018.
 * (c) 2018 Twometer
 */
class ChatsViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    private ImageView avatar;
    private ImageView messageState;
    private TextView header;
    private TextView date;
    private TextView bubble;
    private TextView message;

    ChatsViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        avatar = itemView.findViewById(R.id.image_avatar);
        messageState = itemView.findViewById(R.id.image_state);
        header = itemView.findViewById(R.id.label_title);
        date = itemView.findViewById(R.id.label_date);
        bubble = itemView.findViewById(R.id.label_bubble);
        message = itemView.findViewById(R.id.label_message);
    }

    void configure(ChatsItem item) {
        header.setText(item.getHeader());
        DateTime lastActive = item.getLastActiveDate();
        if (lastActive.isToday())
            date.setText(lastActive.toTimeString(context));
        else if (lastActive.isYesterday())
            date.setText(context.getString(R.string.yesterday));
        else
            date.setText(lastActive.toDateString(context));
        if (item.getUnreadMessages() == 0)
            bubble.setVisibility(View.GONE);
        else
            bubble.setVisibility(View.VISIBLE);
        bubble.setText(String.valueOf(item.getUnreadMessages()));
        message.setText(item.getContent());
        item.getMessageState().apply(messageState);
        item.getMessageSide().apply(messageState);
    }
}
