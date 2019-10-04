package de.vectordata.skynet.ui.chat;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import de.vectordata.skynet.R;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.messages.ChannelMessageConfig;
import de.vectordata.skynet.net.packet.P23MessageRead;
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.ui.view.CheckableRecyclerView;
import de.vectordata.skynet.util.Handlers;

/**
 * Created by Twometer on 21.01.2019.
 * (c) 2019 Twometer
 */
public abstract class ChatActivityBase extends ThemedActivity {

    public static final String EXTRA_CHANNEL_ID = "skynet.chat.channelId";

    /**
     * The ID of the {@see messageChannel}
     */
    long messageChannelId;

    /**
     * This is the channel over which the actual messages are transmitted, such as
     * a group channel or direct channel.
     */
    Channel messageChannel;

    /**
     * Shared handler for executing things like loading from the database on a separate
     * thread.
     */
    Handler backgroundHandler = Handlers.createOnThread("BackgroundThread");

    Handler foregroundHandler = new Handler();

    ImageView avatarView;
    TextView titleView;
    TextView subtitleView;

    CheckableRecyclerView recyclerView;
    EmojiEditText messageInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageChannelId = getIntent().getLongExtra(EXTRA_CHANNEL_ID, 0);

        messageInput = findViewById(R.id.input_message);
        recyclerView = findViewById(R.id.recycler_view);

        ImageButton emojiToggleButton = findViewById(R.id.button_emoji);
        ImageButton sendButton = findViewById(R.id.button_send);

        boolean enterToSend = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enter_to_send", false);
        messageInput.setSingleLine(enterToSend);
        messageInput.setImeOptions(enterToSend ? EditorInfo.IME_ACTION_SEND : EditorInfo.IME_ACTION_NONE);
        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendButton.performClick();
                return true;
            }
            return false;
        });

        boolean animations = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("animations", true);
        if (!animations)
            recyclerView.setItemAnimator(null);

        EmojiPopup popup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root_view))
                .setOnEmojiPopupShownListener(() -> emojiToggleButton.setImageResource(R.drawable.ic_keyboard))
                .setOnEmojiPopupDismissListener(() -> emojiToggleButton.setImageResource(R.drawable.ic_insert_emoji))
                .build(messageInput);

        emojiToggleButton.setOnClickListener(v -> popup.toggle());
        messageInput.setOnClickListener(v -> popup.dismiss());

        setupActionBar();
        initialize();
    }

    public abstract void initialize();

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

        avatarView = customView.findViewById(R.id.image_avatar);
        titleView = customView.findViewById(R.id.label_title);
        subtitleView = customView.findViewById(R.id.label_subtitle);

        Toolbar parent = (Toolbar) customView.getParent();
        parent.setPadding(0, 0, 0, 0);
        parent.setContentInsetsAbsolute(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SkynetContext.getCurrent().getNotificationManager().onBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    void readMessage(long messageId) {
        SkynetContext.getCurrent().getMessageInterface()
                .send(messageChannel.getChannelId(),
                        new ChannelMessageConfig().addDependency(ChannelMessageConfig.ANY_ACCOUNT, messageChannel.getChannelId(), messageId),
                        new P23MessageRead()
                );
        backgroundHandler.post(() -> {
            ChatMessage message = Storage.getDatabase().chatMessageDao().query(messageChannel.getChannelId(), messageId);
            message.setUnread(false);
            Storage.getDatabase().chatMessageDao().update(message);
        });
    }

    void setSubtitle(String subtitle) {
        runOnUiThread(() -> subtitleView.setText(subtitle));
    }

    void setSubtitle(int resId) {
        runOnUiThread(() -> {
            if (subtitleView.getVisibility() != subtitleView.getVisibility())
                subtitleView.setVisibility(View.VISIBLE);
            subtitleView.setText(resId);
        });
    }

    ChannelMessageConfig createConfigWithDependencyTo(long messageId) {
        return ChannelMessageConfig.create().addDependency(ChannelMessageConfig.ANY_ACCOUNT, messageChannel.getChannelId(), messageId);
    }

}
