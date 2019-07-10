package de.vectordata.skynet.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import de.vectordata.skynet.R;
import de.vectordata.skynet.auth.Session;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.Nickname;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.event.PacketEvent;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.P0FSyncFinished;
import de.vectordata.skynet.ui.AddContactActivity;
import de.vectordata.skynet.ui.LoginActivity;
import de.vectordata.skynet.ui.NewGroupActivity;
import de.vectordata.skynet.ui.PreferencesActivity;
import de.vectordata.skynet.ui.WelcomeActivity;
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.ui.chat.ChatActivityBase;
import de.vectordata.skynet.ui.chat.ChatActivityDirect;
import de.vectordata.skynet.ui.main.fab.FabController;
import de.vectordata.skynet.ui.main.fab.FabState;

public class MainActivity extends ThemedActivity {

    private static final String TAG = "MainActivity";

    public static final String ACTION_OPEN_CHAT = "skynet.open_chat";
    public static final String EXTRA_CHANNEL_ID = "skynet.open_chat.channel";

    private boolean leftForLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Session session = Storage.getSession();
        if (session == null) {
            startActivity(LoginActivity.class);
            leftForLogin = true;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        Objects.requireNonNull(tabLayout.getTabAt(1)).select();

        FloatingActionButton fab = findViewById(R.id.fab);
        FabController.with(fab)
                .addState(new FabState(R.drawable.ic_person_add, v -> startActivity(AddContactActivity.class)))
                .addState(FabState.invisible())
                .addState(new FabState(R.drawable.ic_add_a_photo, v -> {

                }))
                .setInitialState(1)
                .attach(tabLayout);

        if (savedInstanceState == null) handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (leftForLogin) {
            leftForLogin = false;
            return;
        }
        checkNicknames();
        if (Storage.getSession() == null) {
            Log.d(TAG, "The user has not logged in, exiting...");
            finish();
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        long channelId = intent.getLongExtra(EXTRA_CHANNEL_ID, 0);
        if (channelId != 0) {
            Intent chatIntent = new Intent(this, ChatActivityDirect.class);
            chatIntent.putExtra(ChatActivityBase.EXTRA_CHANNEL_ID, channelId);
            startActivity(chatIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_contact) {
            startActivity(AddContactActivity.class);
            return true;
        }

        if (id == R.id.action_new_group) {
            startActivity(NewGroupActivity.class);
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(PreferencesActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean hasCustomToolbar() {
        return true;
    }

    @Subscribe
    public void onPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacket() instanceof P0FSyncFinished)
            checkNicknames();
    }

    private void checkNicknames() {
        if (!SkynetContext.getCurrent().isInSync()) return;
        new Thread(() -> {
            Channel accountDataChannel = Storage.getDatabase().channelDao().getByType(Storage.getSession().getAccountId(), ChannelType.ACCOUNT_DATA);
            if (accountDataChannel == null) return;
            Nickname nickname = Storage.getDatabase().nicknameDao().last(accountDataChannel.getChannelId());
            if (nickname == null) runOnUiThread(() -> startActivity(WelcomeActivity.class));
        }).start();
    }

}
