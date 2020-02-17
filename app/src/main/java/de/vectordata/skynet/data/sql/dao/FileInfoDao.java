package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import de.vectordata.skynet.data.model.FileInfo;

@Dao
public interface FileInfoDao {

    @Insert
    void insert(FileInfo... fileInfos);

    @Delete
    void delete(FileInfo... fileInfos);

    @Update
    void update(FileInfo... fileInfos);

    @Query("SELECT * FROM fileInfos WHERE channelId=:channelId AND messageId=:messageId")
    FileInfo get(long channelId, long messageId);

}
