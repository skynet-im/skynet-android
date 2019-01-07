#set($entityName = $NAME.substring(0, 1).toLowerCase() + $NAME.substring(1))

#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
#parse("File Header.java")

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "${entityName}s", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"channelId", "messageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        primaryKeys = {"channelId", "messageId"}
)
public class ${NAME} {

    private long channelId;

    private long messageId;

}
