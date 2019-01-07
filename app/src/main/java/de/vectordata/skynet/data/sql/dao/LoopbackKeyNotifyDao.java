package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.LoopbackKeyNotify;

@Dao
public interface LoopbackKeyNotifyDao {

    @Insert
    void insert(LoopbackKeyNotify... loopbackKeyNotifys);

    @Delete
    void delete(LoopbackKeyNotify... loopbackKeyNotifys);

    @Update
    void update(LoopbackKeyNotify... loopbackKeyNotifys);

    @Query("SELECT * FROM loopbackKeyNotifys WHERE channelId=:channelId AND messageId=:messageId")
    LoopbackKeyNotify get(long channelId, long messageId);

}
