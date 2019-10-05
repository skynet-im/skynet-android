package de.vectordata.skynet.fcm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import de.vectordata.skynet.net.SkynetContext;

public class FcmService extends FirebaseMessagingService {

    private static final String TAG = "FcmService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, String.format("FCM Message received from %s with %d data entries.", remoteMessage.getFrom(), remoteMessage.getData().size()));
        SkynetContext.getCurrent().getNetworkManager().connect();
    }

}
