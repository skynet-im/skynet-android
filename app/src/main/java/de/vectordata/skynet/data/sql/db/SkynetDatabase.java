package de.vectordata.skynet.data.sql.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.Dependency;
import de.vectordata.skynet.data.sql.dao.ChannelDao;
import de.vectordata.skynet.data.sql.dao.ChannelMessageDao;
import de.vectordata.skynet.data.sql.dao.ChatMessageDao;
import de.vectordata.skynet.data.sql.dao.DependencyDao;

@Database(entities = {Channel.class, ChannelMessage.class, Dependency.class, ChatMessage.class}, version = 1)
public abstract class SkynetDatabase extends RoomDatabase {

    public abstract ChannelDao channelDao();

    public abstract ChannelMessageDao channelMessageDao();

    public abstract DependencyDao dependencyDao();

    public abstract ChatMessageDao chatMessageDao();

}
