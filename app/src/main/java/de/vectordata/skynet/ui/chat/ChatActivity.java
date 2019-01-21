package de.vectordata.skynet.ui.chat;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
        items.add(MessageItem.newSystemMessage("YESTERDAY"));
        items.add(new MessageItem("Hi", ago(16, 55, 0), MessageState.SEEN, MessageSide.RIGHT));
        items.add(MessageItem.newSystemMessage("TODAY"));
        items.add(new MessageItem("Eyy moin", ago(16, 45, 0), MessageState.SEEN, MessageSide.LEFT));
        items.add(new MessageItem("Lass uns mal ne Runde zocken, ich will meine neue Grafikkarte ausprobieren \uD83D\uDE02", ago(16, 45, 0), MessageState.SEEN, MessageSide.RIGHT));
        items.add(new MessageItem("Ne mann ich geh jetzt ins Bett, morgen dann", ago(16, 40, 0), MessageState.SEEN, MessageSide.LEFT));
        items.add(new MessageItem("Mhh okay, ich kann aber morgen erst nachmittags.", ago(16, 39, 0), MessageState.SEEN, MessageSide.RIGHT));
        items.add(new MessageItem("Jetzt könnte ich", ago(0, 59, 0), MessageState.SEEN, MessageSide.RIGHT));
        items.add(new MessageItem("Okay komm online, jetzt bin ich am PC", ago(0, 39, 0), MessageState.SEEN, MessageSide.LEFT));
        items.add(new MessageItem("Sehr gut", ago(0, 35, 0), MessageState.SENT, MessageSide.RIGHT));

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

        setupActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View customView = View.inflate(actionBar.getThemedContext(), R.layout.actionbar_chat, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customView, layout);

        ImageButton backButton = customView.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> onBackPressed());

        ImageView avatar = customView.findViewById(R.id.image_avatar);
        TextView nickname = customView.findViewById(R.id.label_nickname);
        TextView onlineState = customView.findViewById(R.id.label_online_state);
        nickname.setText("Jan");
        onlineState.setText("last seen today 4:13 PM");

        Toolbar parent = (Toolbar) customView.getParent();
        parent.setPadding(0, 0, 0, 0);
        parent.setContentInsetsAbsolute(0, 0);
    }

    private DateTime ago(int hr, int min, int sec) {
        return DateTime.fromMillis(System.currentTimeMillis() - sec * 1000 - min * 60000 - hr * 3600000);
    }

}
