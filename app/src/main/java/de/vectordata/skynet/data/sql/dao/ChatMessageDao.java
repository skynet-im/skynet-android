package de.vectordata.skynet.data.sql.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.vectordata.skynet.data.model.ChatMessage;

@Dao
public interface ChatMessageDao {

    @Insert
    void insert(ChatMessage... chatMessages);

    @Update
    void update(ChatMessage... chatMessages);

    @Delete
    void delete(ChatMessage... chatMessages);

    @Query("SELECT * FROM chatMessages WHERE channelId=:channelId AND messageId=:messageId")
    ChatMessage query(long channelId, long messageId);

    @Query("SELECT * FROM chatMessages WHERE channelId=:channelId")
    List<ChatMessage> query(long channelId);

    @Query("SELECT * FROM chatMessages WHERE channelId=:channelId ORDER BY messageId DESC LIMIT :limit")
    List<ChatMessage> queryLast(long channelId, int limit);

    @Query("SELECT * FROM chatMessages WHERE channelId=:channelId ORDER BY messageId DESC LIMIT 1")
    ChatMessage queryLast(long channelId);

}
