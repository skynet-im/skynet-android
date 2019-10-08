package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

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

    @Query("SELECT chatMessages.* FROM chatMessages,channelMessages WHERE channelMessages.channelId=chatMessages.channelId AND channelMessages.messageId=chatMessages.messageId AND chatMessages.channelId=:channelId ORDER BY dispatchTime DESC LIMIT :limit")
    List<ChatMessage> queryLast(long channelId, int limit);

    @Query("SELECT chatMessages.* FROM chatMessages,channelMessages WHERE channelMessages.channelId=chatMessages.channelId AND channelMessages.messageId=chatMessages.messageId AND chatMessages.channelId=:channelId AND channelMessages.messageId<:firstMessage ORDER BY dispatchTime DESC LIMIT :limit")
    List<ChatMessage> queryLast(long channelId, long firstMessage, int limit);

    @Query("SELECT chatMessages.* FROM chatMessages,channelMessages WHERE channelMessages.channelId=chatMessages.channelId AND channelMessages.messageId=chatMessages.messageId AND chatMessages.channelId=:channelId ORDER BY dispatchTime DESC LIMIT 1")
    ChatMessage queryLast(long channelId);

    @Query("SELECT * FROM chatMessages WHERE messageState=0")
    List<ChatMessage> queryPending();

    @Query("SELECT chatMessages.* FROM chatMessages,channelMessages WHERE channelMessages.channelId=chatMessages.channelId AND channelMessages.messageId=chatMessages.messageId AND messageState=1 AND channelMessages.senderId!=:accountId")
    List<ChatMessage> queryUnconfirmed(long accountId);

    @Query("SELECT * FROM chatMessages WHERE isUnread=1")
    List<ChatMessage> queryUnread();

    @Query("SELECT * FROM chatMessages WHERE channelId=:channelId AND isUnread=1")
    List<ChatMessage> queryUnread(long channelId);
}
