package de.vectordata.skynet.data.sql.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

}
