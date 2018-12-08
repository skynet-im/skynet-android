package de.vectordata.skynet.data;

import androidx.room.Room;
import android.content.Context;

import de.vectordata.skynet.data.sql.db.SkynetDatabase;

public class StorageAccess {

    private static SkynetDatabase skynetDatabase;

    public static SkynetDatabase getDatabase(Context context) {
        if (skynetDatabase == null)
            skynetDatabase = Room.databaseBuilder(context.getApplicationContext(), SkynetDatabase.class, "skynet-db").build();
        return skynetDatabase;
    }

}
