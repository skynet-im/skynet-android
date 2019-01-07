package de.vectordata.skynet;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.net.SkynetContext;

public class SkynetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SkynetApplication", "Initializing application");
        Storage.initialize(getApplicationContext());
        FirebaseApp.initializeApp(this);
        SkynetContext.getCurrent().getNetworkManager().connect();
        EmojiManager.install(new IosEmojiProvider());
    }
}
