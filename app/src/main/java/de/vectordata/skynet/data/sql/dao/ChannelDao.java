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
    void insert(Channel... channels);

    @Delete
    void delete(Channel... channels);

    @Update
    void update(Channel... channels);

    @Query("DELETE FROM channels WHERE channelId=:channelId")
    void deleteById(long channelId);

    @Query("SELECT * FROM channels WHERE channelId=:channelId")
    Channel getById(long channelId);

    @TypeConverters(ChannelTypeConverter.class)
    @Query("SELECT * FROM channels WHERE ownerId=:ownerId AND channelType=:channelType")
    Channel getByType(long ownerId, ChannelType channelType);

    @Query("SELECT * FROM channels")
    List<Channel> getAll();

    @TypeConverters(ChannelTypeConverter.class)
    @Query("SELECT * FROM channels WHERE channelType=:channelType")
    List<Channel> getAllOfType(ChannelType channelType);

}
