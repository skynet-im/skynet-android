package de.vectordata.skynet.auth;

import java.io.Serializable;

import de.vectordata.skynet.crypto.hash.KeyCollection;

public class Session implements Serializable {

    private long accountId;

    private long sessionId;

    private KeyCollection sessionKeys;

    public Session(KeyCollection sessionKeys) {
        this.sessionKeys = sessionKeys;
    }

    public long getAccountId() {
        return accountId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public KeyCollection getSessionKeys() {
        return sessionKeys;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAuthenticated() {
        return !(accountId == 0 || sessionId == 0 || sessionKeys == null);
    }
}
