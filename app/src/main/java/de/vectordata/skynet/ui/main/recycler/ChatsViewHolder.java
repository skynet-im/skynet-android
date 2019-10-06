package de.vectordata.skynet.ui.main.recycler;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;
import de.vectordata.skynet.ui.util.DateUtil;
import de.vectordata.skynet.ui.util.DefaultProfileImage;
import de.vectordata.skynet.ui.util.ThemeUtil;

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

    void configure(ChatsItem item, boolean singleLine) {
        header.setText(item.getHeader());
        DefaultProfileImage.create(item.getHeader().substring(0, 1), item.getCounterpartId(), 128, 128)
                .loadInto(avatar);

        if (singleLine) {
            message.setVisibility(View.GONE);
            bubble.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
            return;
        } else {
            message.setVisibility(View.VISIBLE);
            date.setVisibility(View.VISIBLE);
        }

        DateTime lastActive = item.getLastActiveDate();
        if (lastActive != null)
            date.setText(DateUtil.toString(context, lastActive));
        if (item.getUnreadMessages() == 0)
            bubble.setVisibility(View.GONE);
        else
            bubble.setVisibility(View.VISIBLE);
        bubble.setText(String.valueOf(item.getUnreadMessages()));

        ThemeUtil.resetTextViewColor(message);
        ThemeUtil.resetTextViewColor(date);
        switch (item.getType()) {
            case NORMAL:
                message.setTypeface(null, Typeface.NORMAL);
                message.setAlpha(1.0f);
                message.setText(item.getContent());
                item.getMessageState().apply(messageState);
                item.getMessageSide().apply(messageState);
                break;
            case DRAFT:
                date.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                date.setText(R.string.draft);
                break;
            case DELETED:
                message.setTypeface(null, Typeface.ITALIC);
                message.setAlpha(0.75f);
                message.setText(R.string.message_deleted);
                messageState.setVisibility(View.GONE);
                break;
            case HIGHLIGHTED:
                message.setTypeface(null, Typeface.NORMAL);
                message.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                message.setText(item.getContent());
                messageState.setVisibility(View.GONE);
                break;
        }
    }

}
