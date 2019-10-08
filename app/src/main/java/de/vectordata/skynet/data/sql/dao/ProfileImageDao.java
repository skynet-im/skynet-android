package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import de.vectordata.skynet.data.model.ProfileImage;

@Dao
public interface ProfileImageDao {

    @Insert
    void insert(ProfileImage... profileImages);

    @Delete
    void delete(ProfileImage... profileImages);

    @Update
    void update(ProfileImage... profileImages);

    @Query("SELECT * FROM profileImages WHERE channelId=:channelId AND messageId=:messageId")
    ProfileImage get(long channelId, long messageId);

}
