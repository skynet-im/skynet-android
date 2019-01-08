package de.vectordata.skynet.ui.chat.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.skynet.R;

class MessageViewHolder extends RecyclerView.ViewHolder {

    private final TextView message;
    private final TextView time;
    private final ImageView state;

    MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.label_message);
        time = itemView.findViewById(R.id.label_time);
        state = itemView.findViewById(R.id.image_state);
    }

    void configure(MessageItem messageItem) {
        message.setText(messageItem.getContent());
        if (time != null)
            time.setText(messageItem.getSentDate().toTimeString(time.getContext()));
        if (state != null)
            messageItem.getMessageState().apply(state);
    }

}
