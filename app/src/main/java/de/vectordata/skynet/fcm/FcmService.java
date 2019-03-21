package de.vectordata.skynet.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import de.vectordata.skynet.net.SkynetContext;

public class FcmService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SkynetContext.getCurrent().getNetworkManager().connect();
    }

}
