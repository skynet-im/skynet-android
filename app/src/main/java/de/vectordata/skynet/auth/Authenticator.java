package de.vectordata.skynet.auth;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.P08RestoreSession;
import de.vectordata.skynet.net.packet.P09RestoreSessionResponse;
import de.vectordata.skynet.net.packet.model.RestoreSessionStatus;
import de.vectordata.skynet.util.Callback;

public class Authenticator {

    public static void authenticate(Session session, Callback<RestoreSessionStatus> callback) {
        ChannelMessage lastMessage = Storage.getDatabase().channelMessageDao().queryLast();
        List<Long> channelIds = new ArrayList<>();
        List<Channel> channels = Storage.getDatabase().channelDao().getAll();
        for (Channel channel : channels)
            channelIds.add(channel.getChannelId());
        SkynetContext.getCurrent()
                .getNetworkManager()
                .sendPacket(new P08RestoreSession(session.getSessionId(), session.getSessionToken(), lastMessage.getMessageId(), channelIds))
                .waitForPacket(P09RestoreSessionResponse.class, p -> callback.onCallback(p.statusCode));
    }

}
