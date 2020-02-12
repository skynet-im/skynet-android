package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import de.vectordata.skynet.data.sql.converters.DateTimeConverter;
import de.vectordata.skynet.net.packet.P29DeviceList;
import de.vectordata.skynet.util.date.DateTime;

@Entity(tableName = "deviceList")
public class Device {

    @PrimaryKey
    private long sessionId;

    @TypeConverters(DateTimeConverter.class)
    private DateTime creationTime;

    private String applicationIdentifier;

    public static Device fromPacket(P29DeviceList.PDevice packet) {
        Device device = new Device();
        device.sessionId = packet.sessionId;
        device.creationTime = packet.creationTime;
        device.applicationIdentifier = packet.applicationIdentifier;
        return device;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getApplicationIdentifier() {
        return applicationIdentifier;
    }

    public void setApplicationIdentifier(String applicationIdentifier) {
        this.applicationIdentifier = applicationIdentifier;
    }
}
