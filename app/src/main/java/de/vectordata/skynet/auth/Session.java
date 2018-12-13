package de.vectordata.skynet.auth;

import de.vectordata.skynet.crypto.hash.KeyCollection;

public class Session {

    private long accountId;

    private long sessionId;

    private KeyCollection sessionKeys;

    public Session(long accountId, long sessionId, KeyCollection sessionKeys) {
        this.accountId = accountId;
        this.sessionId = sessionId;
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

    public boolean isAuthenticated() {
        return accountId == 0 || sessionId == 0 || sessionKeys == null;
    }
}
