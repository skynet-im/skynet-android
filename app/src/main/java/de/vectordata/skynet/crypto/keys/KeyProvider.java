package de.vectordata.skynet.crypto.keys;

import de.vectordata.skynet.net.packet.P0BChannelMessage;

public interface KeyProvider {

    KeyStore getMessageKeys(P0BChannelMessage message);

}
