package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import de.vectordata.skynet.data.model.Bio;

@Dao
public interface BioDao {

    @Insert
    void insert(Bio... bios);

    @Delete
    void delete(Bio... bios);

    @Update
    void update(Bio... bios);

    @Query("SELECT * FROM bios WHERE channelId=:channelId AND messageId=:messageId")
    Bio get(long channelId, long messageId);

}
