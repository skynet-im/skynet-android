package de.vectordata.skynet.ui.notification;

public class NotificationManagerFactory {

    public INotificationManager createManager() {
        // TODO: Return correct manager based on the system version
        return new NotificationManagerNew();
    }

}
