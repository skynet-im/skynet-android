package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import de.vectordata.skynet.data.model.ChannelMessage;

@Dao
public interface ChannelMessageDao {

    @Insert
    void insert(ChannelMessage... messages);

}
