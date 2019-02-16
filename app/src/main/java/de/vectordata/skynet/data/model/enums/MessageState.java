package de.vectordata.skynet.data.model.enums;

import android.widget.ImageView;

import de.vectordata.skynet.R;

/**
 * Created by Twometer on 14.12.2018.
 * (c) 2018 Twometer
 */
public enum MessageState {
    SENDING,
    SENT,
    DELIVERED,
    SEEN,
    SYSTEM,
    NONE;

    public void apply(ImageView imageView) {
        if (this == SENDING)
            imageView.setImageResource(R.drawable.ic_msg_sending);
        else if (this == SENT)
            imageView.setImageResource(R.drawable.ic_msg_sent);
        else if (this == DELIVERED)
            imageView.setImageResource(R.drawable.ic_msg_delivered);
        else if (this == SEEN)
            imageView.setImageResource(R.drawable.ic_msg_seen);
    }

}