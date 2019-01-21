package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.ChannelKey;
import de.vectordata.skynet.data.model.enums.ChannelType;

@Dao
public interface ChannelKeyDao {

    @Insert
    void insert(ChannelKey... channelKeys);

    @Delete
    void delete(ChannelKey... channelKeypairs);

    @Update
    void update(ChannelKey... channelKeypairs);

    @Query("SELECT * FROM channelKeys WHERE channelId=:channelId AND messageId=:messageId")
    ChannelKey get(long channelId, long messageId);

    @Query("SELECT 1 FROM channels,channelKeys WHERE channels.channelId=channelKeys.channelId AND channels.channelType=:type")
    int hasKeys(ChannelType type);

    @Query("SELECT * FROM channelKeys WHERE channelId=:channelId ORDER BY messageId DESC LIMIT 1")
    ChannelKey getLast(long channelId);

}
