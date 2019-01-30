package de.vectordata.skynet.ui.chat;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import de.vectordata.skynet.R;
import de.vectordata.skynet.ui.base.ThemedActivity;

/**
 * Created by Twometer on 21.01.2019.
 * (c) 2019 Twometer
 */
public abstract class ChatActivityBase extends ThemedActivity {

    public static final String EXTRA_CHANNEL_ID = "de.vectordata.skynet.chat.channelId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        EmojiEditText messageInput = findViewById(R.id.input_message);
        ImageButton emojiToggleButton = findViewById(R.id.button_emoji);

        EmojiPopup popup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root_view))
                .setOnEmojiPopupShownListener(() -> emojiToggleButton.setImageResource(R.drawable.ic_keyboard))
                .setOnEmojiPopupDismissListener(() -> emojiToggleButton.setImageResource(R.drawable.ic_insert_emoji))
                .build(messageInput);

        emojiToggleButton.setOnClickListener(v -> popup.toggle());
        messageInput.setOnClickListener(v -> popup.dismiss());
        initialize();
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
        configureActionBar(avatar, nickname, onlineState);

        Toolbar parent = (Toolbar) customView.getParent();
        parent.setPadding(0, 0, 0, 0);
        parent.setContentInsetsAbsolute(0, 0);
    }

    public abstract void initialize();

    public abstract void configureActionBar(ImageView avatar, TextView nickname, TextView onlineState);

}
