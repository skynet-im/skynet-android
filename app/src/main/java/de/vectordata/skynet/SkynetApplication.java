package de.vectordata.skynet;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.FirebaseApp;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.P34SetClientState;
import de.vectordata.skynet.net.packet.model.OnlineState;

public class SkynetApplication extends Application implements DefaultLifecycleObserver {

    public static final String TAG = "SkynetApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Initializing application");
        Storage.initialize(getApplicationContext());
        FirebaseApp.initializeApp(this);
        SkynetContext.getCurrent().getNetworkManager().connect();
        EmojiManager.install(new IosEmojiProvider());
        SkynetContext.getCurrent().getNotificationManager().onInitialize(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        Log.i(TAG, "Application init completed");
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        SkynetContext.getCurrent().getAppState().setOnlineState(OnlineState.ACTIVE);
        updateOnlineState();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        SkynetContext.getCurrent().getAppState().setOnlineState(OnlineState.INACTIVE);
        updateOnlineState();
    }

    private void updateOnlineState() {
        if (SkynetContext.getCurrent().isInSync())
            SkynetContext.getCurrent().getNetworkManager().sendPacket(new P34SetClientState(SkynetContext.getCurrent().getAppState().getOnlineState()));
    }

}
