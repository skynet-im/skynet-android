package de.vectordata.skynet.ui.chat;

import android.os.Bundle;
import android.widget.ImageButton;

import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.R;
import de.vectordata.skynet.ui.chat.recycler.MessageAdapter;
import de.vectordata.skynet.ui.chat.recycler.MessageItem;
import de.vectordata.skynet.ui.util.MessageSide;
import de.vectordata.skynet.ui.util.MessageState;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        List<MessageItem> items = new ArrayList<>();
        items.add(new MessageItem("This is a test", DateTime.now(), MessageState.SEEN, MessageSide.LEFT));
        items.add(new MessageItem("This is a test", DateTime.now(), MessageState.SEEN, MessageSide.RIGHT));
        items.add(new MessageItem("This is a very long test of a very long message and is this a very long sentence that makes no sense at all but can be used for testing.", DateTime.now(), MessageState.SEEN, MessageSide.RIGHT));
        items.add(new MessageItem("This is a very long test of a very long message and is this a very long sentence that makes no sense at all but can be used for testing.", DateTime.now(), MessageState.SEEN, MessageSide.LEFT));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MessageAdapter(items));

        EmojiEditText messageInput = findViewById(R.id.input_message);
        ImageButton emojiToggleButton = findViewById(R.id.button_emoji);

        EmojiPopup popup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root_view))
                .setOnEmojiPopupShownListener(() -> emojiToggleButton.setImageResource(R.drawable.ic_keyboard))
                .setOnEmojiPopupDismissListener(() -> emojiToggleButton.setImageResource(R.drawable.ic_insert_emoji))
                .build(messageInput);

        emojiToggleButton.setOnClickListener(v -> popup.toggle());
        messageInput.setOnClickListener(v -> popup.dismiss());

    }
}
