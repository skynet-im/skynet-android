package de.vectordata.skynet;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

import de.vectordata.skynet.data.StorageAccess;
import de.vectordata.skynet.net.SkynetContext;

public class SkynetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SkynetApplication", "Initializing application");
        StorageAccess.initialize(getApplicationContext());
        FirebaseApp.initializeApp(this);
        SkynetContext.getCurrent().getNetworkManager().connect();
    }
}
