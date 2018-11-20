package de.vectordata.skynet.data.sql.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.sql.dao.ChannelDao;

@Database(entities = {Channel.class}, version = 1)
public abstract class SkynetDatabase extends RoomDatabase {

    public abstract ChannelDao channelDao();

}
