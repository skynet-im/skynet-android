package de.vectordata.skynet.data.sql.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.sql.dao.ChannelDao;

@Database(entities = {Channel.class}, version = 1)
public abstract class SkynetDatabase extends RoomDatabase {

    public abstract ChannelDao channelDao();

}
