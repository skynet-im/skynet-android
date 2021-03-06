package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.vectordata.skynet.data.model.ChannelMessage;

@Dao
public interface ChannelMessageDao {

    @Insert
    void insert(ChannelMessage... messages);

    @Update
    void update(ChannelMessage... messages);

    @Query("SELECT * FROM channelMessages WHERE channelId=:channelId AND messageId=:messageId")
    ChannelMessage getById(long channelId, long messageId);

    @Query("SELECT * FROM channelMessages WHERE channelId=:channelId")
    List<ChannelMessage> query(long channelId);

    @Query("SELECT * FROM channelMessages ORDER BY messageId DESC LIMIT 1")
    ChannelMessage queryLast();

    @Query("SELECT * FROM channelMessages WHERE channelId=:channelId ORDER BY messageId DESC LIMIT 1")
    ChannelMessage queryLast(long channelId);
}
