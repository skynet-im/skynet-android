package de.vectordata.skynet.ui.notification;

import android.os.Build;

public class NotificationManagerFactory {

    public INotificationManager createManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return new NotificationManagerNew();
        else
            return new NotificationManagerOld();
    }

}
