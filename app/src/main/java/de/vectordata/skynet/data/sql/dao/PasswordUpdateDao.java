package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.PasswordUpdate;

@Dao
public interface PasswordUpdateDao {

    @Insert
    void insert(PasswordUpdate... passwordUpdates);

    @Delete
    void delete(PasswordUpdate... passwordUpdates);

    @Update
    void update(PasswordUpdate... passwordUpdates);

    @Query("SELECT * FROM passwordUpdates WHERE channelId=:channelId AND messageId=:messageId")
    PasswordUpdate get(long channelId, long messageId);

}
