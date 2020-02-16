package de.vectordata.skynet;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.FirebaseApp;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.connect.ConnectionListener;
import de.vectordata.skynet.net.packet.P34SetClientState;
import de.vectordata.skynet.net.packet.model.OnlineState;

public class SkynetApplication extends Application implements DefaultLifecycleObserver {

    public static final String TAG = "SkynetApplication";

    public static final String APPLICATION_IDENTIFIER = "android/de.vectordata.skynet";
    public static final int VERSION_CODE = BuildConfig.VERSION_CODE;

    public static final String SERVER_IP = "skynet.lerchen.net";
    public static final int SERVER_PORT = 32761;
    public static final int PROTOCOL_VERSION = 2;

    @RawRes
    public static final int CERTIFICATE_RES = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Initializing application");
        Storage.initialize(getApplicationContext());
        FirebaseApp.initializeApp(this);
        EmojiManager.install(new IosEmojiProvider());
        SkynetContext.getCurrent().initialize(this);
        SkynetContext.getCurrent().getNetworkManager().connect();
        SkynetContext.getCurrent().getNotificationManager().onInitialize(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        ConnectionListener.register(this);
        Log.i(TAG, "Application init completed");
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        ConnectionListener.unregister(this);
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
