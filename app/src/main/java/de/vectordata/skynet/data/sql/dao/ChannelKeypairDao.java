package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import de.vectordata.skynet.data.model.ChannelKeypair;

@Dao
public interface ChannelKeypairDao {

    @Insert
    void insert(ChannelKeypair... channelKeypairs);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertIgnore(ChannelKeypair channelKeypair);

    @Delete
    void delete(ChannelKeypair... channelKeypairs);

    @Update
    void update(ChannelKeypair... channelKeypairs);

    @Query("SELECT * FROM channelKeypairs WHERE channelId=:channelId")
    ChannelKeypair get(int channelId);

    @Transaction
    default void upsert(ChannelKeypair channelKeypair) {
        long id = insertIgnore(channelKeypair);
        if (id == -1) update(channelKeypair);
    }

}
