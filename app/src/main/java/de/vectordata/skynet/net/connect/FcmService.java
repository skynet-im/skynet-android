package de.vectordata.skynet.net.connect;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.Subscribe;

import de.vectordata.skynet.event.ConnectionFailedEvent;
import de.vectordata.skynet.event.SyncFinishedEvent;
import de.vectordata.skynet.net.SkynetContext;

/**
 * Registers events to reconnect to the Skynet server instantly when
 * a FCM message is received
 */
public class FcmService extends FirebaseMessagingService {

    private static final String TAG = "FcmService";

    private PowerManager.WakeLock wakeLock;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, String.format("FCM Message received from %s with %d data entries.", remoteMessage.getFrom(), remoteMessage.getData().size()));

        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Skynet::SyncWakeLock");
        }

        // A sync should never take more than 60 seconds, so set the timeout
        // of the wakelock to 60k ms
        wakeLock.acquire(60000);

        SkynetContext.getCurrent().getNetworkManager().connect();
    }


    // If we are done syncing or lost connection to the server,
    // release the wake lock
    @Subscribe
    public void onConnectionLost(ConnectionFailedEvent event) {
        wakeLock.release();
    }

    @Subscribe
    public void onSyncFinished(SyncFinishedEvent event) {
        wakeLock.release();
    }

}
