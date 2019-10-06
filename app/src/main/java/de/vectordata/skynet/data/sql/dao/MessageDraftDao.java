package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.vectordata.skynet.data.model.MessageDraft;

@Dao
public interface MessageDraftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageDraft... drafts);

    @Query("DELETE FROM messageDrafts WHERE channelId=:channelId")
    void delete(long channelId);

    @Query("SELECT * FROM messageDrafts WHERE channelId=:channelId")
    MessageDraft query(long channelId);

}
