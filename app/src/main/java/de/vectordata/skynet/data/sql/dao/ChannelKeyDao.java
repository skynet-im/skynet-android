package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.ChannelKey;

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

}
