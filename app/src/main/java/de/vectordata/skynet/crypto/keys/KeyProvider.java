package de.vectordata.skynet.crypto.keys;

public interface KeyProvider {

    KeyStore getChannelKeys(long channelId);

}
