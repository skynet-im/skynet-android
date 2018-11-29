package de.vectordata.skynet.crypto;

public class KeyStore {

    private byte[] aesKey;
    private byte[] hmacKey;

    public KeyStore(byte[] aesKey, byte[] hmacKey) {
        this.aesKey = aesKey;
        this.hmacKey = hmacKey;
    }

    public byte[] getAesKey() {
        return aesKey;
    }

    public byte[] getHmacKey() {
        return hmacKey;
    }
}
