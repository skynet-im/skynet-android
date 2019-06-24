package de.vectordata.skynet.ui.chat.action;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;

import de.vectordata.skynet.R;

public class MessageActionController {

    private Group messageActionGroup;
    private ImageView imageView;
    private TextView headerTextView;
    private TextView contentTextView;

    private MessageAction currentAction = MessageAction.NONE;

    public MessageActionController(Activity parent) {
        messageActionGroup = parent.findViewById(R.id.group_message_action);
        headerTextView = parent.findViewById(R.id.label_message_action_header);
        contentTextView = parent.findViewById(R.id.label_message_action_content);
        imageView = parent.findViewById(R.id.image_message_action);
    }

    public void setAction(MessageAction action) {
        this.currentAction = action;

        messageActionGroup.setVisibility(currentAction == MessageAction.NONE ? View.GONE : View.VISIBLE);

        switch (currentAction) {
            case EDIT:
                imageView.setImageResource(R.drawable.ic_edit);
                headerTextView.setText(R.string.header_editing);
                break;
            case QUOTE:
                imageView.setImageResource(R.drawable.ic_quote);
                break;
        }
    }

    public void exit() {
        setAction(MessageAction.NONE);
    }

    public void setHeader(String header) {
        headerTextView.setText(header);
    }

    public void setContent(String content) {
        contentTextView.setText(content);
    }

    public boolean isOpen() {
        return currentAction != MessageAction.NONE;
    }
}
