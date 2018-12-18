package de.vectordata.skynet.auth;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.data.StorageAccess;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.model.RestoreSessionError;
import de.vectordata.skynet.net.packet.P08RestoreSession;
import de.vectordata.skynet.net.packet.P09RestoreSessionResponse;
import de.vectordata.skynet.util.Callback;

public class Authenticator {

    public static void authenticate(Session session, Callback<RestoreSessionError> callback) {
        List<P08RestoreSession.ChannelItem> channelItems = new ArrayList<>();
        List<Channel> channels = StorageAccess.getDatabase().channelDao().getAll();
        for (Channel channel : channels)
            channelItems.add(new P08RestoreSession.ChannelItem(channel.getChannelId(), channel.getLatestMessage()));
        SkynetContext.getCurrent()
                .getNetworkManager()
                .sendPacket(new P08RestoreSession(session.getAccountId(), session.getSessionKeys().getKeyHash(), session.getSessionId(), channelItems))
                .waitForPacket(P09RestoreSessionResponse.class, p -> callback.onCallback(p.errorCode));
    }

}
