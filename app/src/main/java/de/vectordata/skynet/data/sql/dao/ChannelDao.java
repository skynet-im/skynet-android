package de.vectordata.skynet.data.sql.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.sql.converters.ChannelTypeConverter;

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

    @TypeConverters(ChannelTypeConverter.class)
    @Query("SELECT * FROM channels WHERE channelType=:channelType")
    Channel getChannelByType(ChannelType channelType);

    @Query("SELECT * FROM channels")
    List<Channel> getChannels();

}
