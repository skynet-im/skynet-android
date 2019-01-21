package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.Nickname;

@Dao
public interface NicknameDao {

    @Insert
    void insert(Nickname... nicknames);

    @Delete
    void delete(Nickname... nicknames);

    @Update
    void update(Nickname... nicknames);

    @Query("SELECT * FROM nicknames WHERE channelId=:channelId AND messageId=:messageId")
    Nickname get(long channelId, long messageId);

    @Query("SELECT * FROM nicknames WHERE channelId=:channelId ORDER BY messageId DESC LIMIT 1")
    Nickname last(long channelId);

}
