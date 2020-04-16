package de.vectordata.skynet.data;

import android.content.Context;

import androidx.room.Room;

import de.vectordata.skynet.auth.Session;
import de.vectordata.skynet.data.file.ObjectCache;
import de.vectordata.skynet.data.sql.db.SkynetDatabase;

public class Storage {

    private static boolean initialized = false;

    private static SkynetDatabase skynetDatabase;
    private static ObjectCache<Session> sessionCache;

    public static SkynetDatabase getDatabase() {
        return skynetDatabase;
    }

    public static Session getSession() {
        return sessionCache.get();
    }

    public static void setSession(Session session) {
        sessionCache.set(session);
    }

    public static void initialize(Context context) {
        if (!ensureUninitialized())
            return;
        skynetDatabase = Room.databaseBuilder(context.getApplicationContext(), SkynetDatabase.class, "skynet-db").build();
        sessionCache = new ObjectCache<>(context, "session");
    }

    private static boolean ensureUninitialized() {
        if (initialized)
            return false;
        initialized = true;
        return true;
    }

    public static void clear() {
        skynetDatabase.clearAllTables();
        sessionCache.clear();
    }

}
