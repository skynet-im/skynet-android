package de.vectordata.skynet.net.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;

import de.vectordata.skynet.net.SkynetContext;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Registers events to reconnect to the Skynet server instantly when
 * WiFi / mobile data reconnects
 */
public class ConnectionListener extends BroadcastReceiver {

    private static ConnectionListener listener;
    private static ConnectivityManager.NetworkCallback networkCallback;

    public static void register(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    SkynetContext.getCurrent().getNetworkManager().connect();
                }
            };
            manager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);

        } else {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

            listener = new ConnectionListener();
            context.registerReceiver(listener, filter);
        }
    }

    public static void unregister(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            manager.unregisterNetworkCallback(networkCallback);
        else if (listener != null)
            context.unregisterReceiver(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            SkynetContext.getCurrent().getNetworkManager().connect();
        }
    }
}
