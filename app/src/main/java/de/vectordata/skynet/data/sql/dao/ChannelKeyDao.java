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

    @Query("DELETE FROM channelKeys WHERE channelId=:channelId")
    void dropKeys(long channelId);

    @Query("SELECT COUNT(channelId) FROM channelKeys WHERE channelId=:channelId")
    int countKeys(long channelId);

    @TypeConverters({KeyTypeConverter.class, ChannelTypeConverter.class})
    @Query("SELECT channelKeys.channelId,channelKeys.messageId,keyType,signatureKeyFormat,signatureKey,derivationKeyFormat,derivationKey FROM channels,channelKeys WHERE channels.channelId=channelKeys.channelId AND channelType=:channelType AND ownerId=:ownerId AND keyType=:keyType ORDER BY messageId DESC LIMIT 1")
    ChannelKey getFromChannel(long ownerId, ChannelType channelType, KeyType keyType);

}
