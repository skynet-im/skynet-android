package de.vectordata.skynet.net.packet.model;

import de.vectordata.libjvsl.util.cscompat.DateTime;

public class SessionInfo {

    private long sessionId;

    private DateTime lastConnected;

    private int lastVersionCode;

    public SessionInfo(long sessionId, DateTime lastConnected, int lastVersionCode) {
        this.sessionId = sessionId;
        this.lastConnected = lastConnected;
        this.lastVersionCode = lastVersionCode;
    }

    public long getSessionId() {
        return sessionId;
    }

    public DateTime getLastConnected() {
        return lastConnected;
    }

    public int getLastVersionCode() {
        return lastVersionCode;
    }
}
