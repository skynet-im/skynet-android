#set($entityName = $NAME.substring(0, 1).toLowerCase() + $NAME.substring(1))

#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
#parse("File Header.java")

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ${NAME}Dao {

    @Insert
    void insert(${NAME}... ${entityName}s);

    @Delete
    void delete(${NAME}... ${entityName}s);

    @Update
    void update(${NAME}... ${entityName}s);

    @Query("SELECT * FROM ${entityName}s WHERE channelId=:channelId AND messageId=:messageId")
    ${NAME} get(long channelId, long messageId);

}
