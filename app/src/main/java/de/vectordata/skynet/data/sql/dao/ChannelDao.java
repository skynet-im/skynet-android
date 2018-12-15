package de.vectordata.skynet.data.sql.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.Channel;

@Dao
public interface ChannelDao {

    @Insert
    void insertChannels(Channel... channels);

    @Delete
    void removeChannels(Channel... channels);

    @Update
    void updateChannels(Channel... channels);

    @Query("SELECT * FROM channels WHERE channelId=:channelId")
    Channel getChannel(long channelId);

    @Query("SELECT * FROM channels")
    List<Channel> getChannels();

}
