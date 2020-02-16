package de.vectordata.skynet.crypto.keys;

import de.vectordata.skynet.net.packet.P0BChannelMessage;

public interface KeyProvider {

    ChannelKeys getChannelKeys(P0BChannelMessage message);

}
