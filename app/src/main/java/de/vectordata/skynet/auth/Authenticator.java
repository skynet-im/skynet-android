package de.vectordata.skynet.auth;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.P08RestoreSession;
import de.vectordata.skynet.net.packet.P09RestoreSessionResponse;
import de.vectordata.skynet.net.packet.model.RestoreSessionStatus;
import de.vectordata.skynet.util.Callback;

public class Authenticator {

    public static void authenticate(Session session, Callback<RestoreSessionStatus> callback) {
        List<P08RestoreSession.ChannelItem> channelItems = new ArrayList<>();
        List<Channel> channels = Storage.getDatabase().channelDao().getAll();
        for (Channel channel : channels)
            channelItems.add(new P08RestoreSession.ChannelItem(channel.getChannelId(), channel.getLatestMessage()));
        SkynetContext.getCurrent()
                .getNetworkManager()
                .sendPacket(new P08RestoreSession(session.getAccountId(), session.getSessionToken(), channelItems))
                .waitForPacket(P09RestoreSessionResponse.class, p -> callback.onCallback(p.statusCode));
    }

}
