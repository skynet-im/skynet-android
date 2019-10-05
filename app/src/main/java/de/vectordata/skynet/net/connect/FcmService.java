package de.vectordata.skynet.net.connect;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import de.vectordata.skynet.net.SkynetContext;

/**
 * Registers events to reconnect to the Skynet server instantly when
 * a FCM message is received
 */
public class FcmService extends FirebaseMessagingService {

    private static final String TAG = "FcmService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, String.format("FCM Message received from %s with %d data entries.", remoteMessage.getFrom(), remoteMessage.getData().size()));
        SkynetContext.getCurrent().getNetworkManager().connect();
    }

}
