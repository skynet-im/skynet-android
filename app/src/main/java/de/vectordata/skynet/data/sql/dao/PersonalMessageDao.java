package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.PersonalMessage;

@Dao
public interface PersonalMessageDao {

    @Insert
    void insert(PersonalMessage... personalMessages);

    @Delete
    void delete(PersonalMessage... personalMessages);

    @Update
    void update(PersonalMessage... personalMessages);

    @Query("SELECT * FROM personalMessages WHERE channelId=:channelId AND messageId=:messageId")
    PersonalMessage get(long channelId, long messageId);

}
