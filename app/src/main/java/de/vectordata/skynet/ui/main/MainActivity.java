package de.vectordata.skynet.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import de.vectordata.skynet.R;
import de.vectordata.skynet.auth.Session;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.Nickname;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.listener.PacketListener;
import de.vectordata.skynet.net.packet.P0FSyncFinished;
import de.vectordata.skynet.ui.AddContactActivity;
import de.vectordata.skynet.ui.LoginActivity;
import de.vectordata.skynet.ui.NewGroupActivity;
import de.vectordata.skynet.ui.PreferencesActivity;
import de.vectordata.skynet.ui.WelcomeActivity;
import de.vectordata.skynet.ui.base.ThemedActivity;
import de.vectordata.skynet.ui.main.fab.FabController;
import de.vectordata.skynet.ui.main.fab.FabState;

public class MainActivity extends ThemedActivity {

    private static final String TAG = "MainActivity";

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
                .addState(new FabState(R.drawable.ic_add_a_photo_black_24dp, v -> {

                }))
                .setInitialState(1)
                .attach(tabLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (leftForLogin) {
            leftForLogin = false;
            return;
        }
        registerPacketListener();
        checkNicknames();
        if (Storage.getSession() == null) {
            Log.d(TAG, "The user has not logged in, exiting...");
            finish();
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


    private void registerPacketListener() {
        SkynetContext.getCurrent().getNetworkManager().setPacketListener(packet -> {
            for (Fragment fragment : getSupportFragmentManager().getFragments())
                if (fragment instanceof PacketListener)
                    ((PacketListener) fragment).onPacket(packet);

            if (packet instanceof P0FSyncFinished) checkNicknames();
        });
    }

    private void checkNicknames() {
        new Thread(() -> {
            Channel accountDataChannel = Storage.getDatabase().channelDao().getByType(Storage.getSession().getAccountId(), ChannelType.ACCOUNT_DATA);
            if (accountDataChannel == null) return;
            Nickname nickname = Storage.getDatabase().nicknameDao().last(accountDataChannel.getChannelId());
            if (nickname == null) runOnUiThread(() -> startActivity(WelcomeActivity.class));
        }).start();
    }

}
