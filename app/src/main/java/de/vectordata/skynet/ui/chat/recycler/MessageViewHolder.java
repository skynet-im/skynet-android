package de.vectordata.skynet.ui.chat.recycler;

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

    MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.label_message);
        time = itemView.findViewById(R.id.label_time);
        state = itemView.findViewById(R.id.image_state);
        quoteGroup = itemView.findViewById(R.id.group_quote);
        quotedName = itemView.findViewById(R.id.label_quoted_name);
        quotedMessage = itemView.findViewById(R.id.label_quoted_text);
    }

    void configure(MessageItem messageItem) {
        message.setText(messageItem.getContent());
        if (time != null && messageItem.getSentDate() != null)
            time.setText(messageItem.getSentDate().toTimeString(time.getContext()));
        if (state != null)
            messageItem.getMessageState().apply(state);

        if (quoteGroup == null) return;

        if (messageItem.hasQuote()) {
            quoteGroup.setVisibility(View.VISIBLE);
            quotedName.setText(messageItem.getQuotedMessage().getName());
            quotedMessage.setText(messageItem.getQuotedMessage().getMessage());
        } else quoteGroup.setVisibility(View.GONE);
    }

}
