package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import de.vectordata.skynet.data.model.OnlineStateDb;

@Dao
public interface OnlineStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OnlineStateDb... onlineStates);

    @Delete
    void delete(OnlineStateDb... onlineStates);

    @Update
    void update(OnlineStateDb... onlineStates);

    @Query("DELETE FROM onlineStates WHERE channelId=:channelId")
    void clear(long channelId);

    @Query("SELECT * FROM onlineStates WHERE channelId=:channelId ORDER BY messageId DESC LIMIT 1")
    OnlineStateDb get(long channelId);

}
