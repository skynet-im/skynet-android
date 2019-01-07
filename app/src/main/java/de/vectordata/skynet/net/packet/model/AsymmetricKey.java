package de.vectordata.skynet.net.packet.model;

public class AsymmetricKey {

    public KeyFormat format;
    public byte[] key;

    public AsymmetricKey(KeyFormat format, byte[] key) {
        this.format = format;
        this.key = key;
    }

}
