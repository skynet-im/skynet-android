package de.vectordata.skynet.data.sql.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import de.vectordata.skynet.data.model.Dependency;

@Dao
public interface DependencyDao {

    @Insert
    void insert(Dependency... dependencies);

    @Query("SELECT * FROM dependencies WHERE srcChannelId=:channelId AND srcMessageId=:messageId")
    List<Dependency> getDependencies(long channelId, long messageId);

}
