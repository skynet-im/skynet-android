package de.vectordata.skynet.data;

import androidx.room.Room;

import android.content.Context;

import de.vectordata.skynet.auth.Session;
import de.vectordata.skynet.data.file.ObjectCache;
import de.vectordata.skynet.data.sql.db.SkynetDatabase;

public class StorageAccess {

    private static boolean initialized = false;

    private static SkynetDatabase skynetDatabase;
    private static ObjectCache<Session> sessionCache;

    public static SkynetDatabase getDatabase() {

        return skynetDatabase;
    }

    public static Session getSession() {
        return sessionCache.get();
    }

    public static void initialize(Context context) {
        if (!checkBeforeInit())
            return;
        skynetDatabase = Room.databaseBuilder(context.getApplicationContext(), SkynetDatabase.class, "skynet-db").build();
        sessionCache = new ObjectCache<>(context, "session");
    }

    private static boolean checkBeforeInit() {
        if (initialized)
            return false;
        initialized = true;
        return true;
    }

}
