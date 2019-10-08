package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import de.vectordata.skynet.data.model.DaystreamMessage;

/**
 * Created by Twometer on 18.12.2018.
 * (c) 2018 Twometer
 */
@Dao
public interface DaystreamMessageDao {

    @Insert
    void insert(DaystreamMessage... messages);

    @Delete
    void delete(DaystreamMessage... messages);

    @Update
    void update(DaystreamMessage... messages);

    @Query("SELECT * FROM daystreamMessages WHERE channelId=:channelId AND messageId=:messageId")
    DaystreamMessage get(long channelId, long messageId);

}
