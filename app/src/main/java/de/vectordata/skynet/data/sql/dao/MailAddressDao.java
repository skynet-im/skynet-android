package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.MailAddress;

@Dao
public interface MailAddressDao {

    @Insert
    void insert(MailAddress... mailAddresses);

    @Delete
    void delete(MailAddress... mailAddresses);

    @Update
    void update(MailAddress... mailAddresses);

    @Query("SELECT * FROM mailAddresses WHERE channelId=:channelId AND messageId=:messageId")
    MailAddress get(long channelId, long messageId);

    @Query("SELECT * FROM mailAddresses WHERE channelId=:channelId ORDER BY messageId DESC LIMIT 1")
    MailAddress last(long channelId);
}
