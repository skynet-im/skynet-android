package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import de.vectordata.skynet.data.model.DirectChannelCustomization;

@Dao
public interface DirectChannelCustomizationDao {

    @Insert
    void insert(DirectChannelCustomization... directChannelCustomizations);

    @Delete
    void delete(DirectChannelCustomization... directChannelCustomizations);

    @Update
    void update(DirectChannelCustomization... directChannelCustomizations);

    @Query("SELECT * FROM directChannelCustomizations WHERE channelId=:channelId AND messageId=:messageId")
    DirectChannelCustomization get(long channelId, long messageId);

}
