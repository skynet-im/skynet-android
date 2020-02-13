package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import de.vectordata.skynet.data.model.ChannelKey;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.model.enums.KeyType;
import de.vectordata.skynet.data.sql.converters.ChannelTypeConverter;
import de.vectordata.skynet.data.sql.converters.KeyTypeConverter;

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

    @TypeConverters(ChannelTypeConverter.class)
    @Query("SELECT 1 FROM channels,channelKeys WHERE channels.channelId=channelKeys.channelId AND channels.channelType=:type")
    int hasKeys(ChannelType type);

    @TypeConverters(ChannelTypeConverter.class)
    @Query("SELECT 1 FROM channels,channelKeys WHERE channels.channelId=channelKeys.channelId AND channels.channelId=:channelId")
    int hasKeys(long channelId);

    @TypeConverters({KeyTypeConverter.class, ChannelTypeConverter.class})
    @Query("SELECT * FROM channels,channelKeys WHERE channelType=:channelType AND ownerId=:ownerId AND keyType=:keyType ORDER BY messageId DESC LIMIT 1")
    ChannelKey getFromChannel(long ownerId, ChannelType channelType, KeyType keyType);

}
