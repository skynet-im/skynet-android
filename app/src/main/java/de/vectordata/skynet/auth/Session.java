package de.vectordata.skynet.auth;

import java.io.Serializable;

import de.vectordata.skynet.crypto.hash.KeyCollection;

public class Session implements Serializable {

    private long accountId;

    private long sessionId;

    private KeyCollection sessionKeys;

    private byte[] sessionToken;

    private String webToken;

    public Session(KeyCollection sessionKeys) {
        this.sessionKeys = sessionKeys;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public byte[] getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(byte[] sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getWebToken() {
        return webToken;
    }

    public void setWebToken(String webToken) {
        this.webToken = webToken;
    }

    public KeyCollection getSessionKeys() {
        return sessionKeys;
    }

    public boolean isAuthenticated() {
        return !(accountId == 0 || sessionId == 0 || sessionKeys == null);
    }
}
