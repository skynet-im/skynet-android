package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.GroupChannelKeyNotify;

@Dao
public interface GroupChannelKeyNotifyDao {

    @Insert
    void insert(GroupChannelKeyNotify... groupChannelKeyNotifys);

    @Delete
    void delete(GroupChannelKeyNotify... groupChannelKeyNotifys);

    @Update
    void update(GroupChannelKeyNotify... groupChannelKeyNotifys);

    @Query("SELECT * FROM groupChannelKeyNotifys WHERE channelId=:channelId AND messageId=:messageId")
    GroupChannelKeyNotify get(long channelId, long messageId);

}
