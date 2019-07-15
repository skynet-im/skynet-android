package de.vectordata.skynet.ui.chat.recycler;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import de.vectordata.skynet.R;

class MessageViewHolder extends RecyclerView.ViewHolder {

    private final Group quoteGroup;
    private final TextView quotedName;
    private final TextView quotedMessage;
    private final TextView message;
    private final TextView time;
    private final ImageView state;
    private final ImageView edited;

    MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.label_message);
        time = itemView.findViewById(R.id.label_time);
        state = itemView.findViewById(R.id.image_state);
        quoteGroup = itemView.findViewById(R.id.group_quote);
        quotedName = itemView.findViewById(R.id.label_quoted_name);
        quotedMessage = itemView.findViewById(R.id.label_quoted_text);
        edited = itemView.findViewById(R.id.image_edited);
    }

    void configure(MessageItem messageItem) {
        if (messageItem.getContent().equals("\0")) {
            message.setTypeface(null, Typeface.ITALIC);
            message.setAlpha(0.75f);
            message.setText(R.string.message_deleted);
            if (state != null)
                state.setVisibility(View.GONE);
        } else {
            message.setTypeface(null, Typeface.NORMAL);
            message.setAlpha(1.0f);
            message.setText(messageItem.getContent());
            if (state != null)
                state.setVisibility(View.VISIBLE);
        }

        if (edited != null)
            edited.setVisibility(messageItem.isEdited() ? View.VISIBLE : View.GONE);

        if (time != null && messageItem.getSentDate() != null)
            time.setText(messageItem.getSentDate().toTimeString(time.getContext()));
        messageItem.getMessageState().apply(state);

        if (quoteGroup == null) return;

        if (messageItem.hasQuote()) {
            quoteGroup.setVisibility(View.VISIBLE);
            quotedName.setText(messageItem.getQuotedMessage().getName());
            quotedMessage.setText(messageItem.getQuotedMessage().getMessage());
        } else quoteGroup.setVisibility(View.GONE);
    }

}
