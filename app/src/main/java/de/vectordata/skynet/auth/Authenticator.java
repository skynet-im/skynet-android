package de.vectordata.skynet.auth;

import java.util.List;

import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.model.RestoreSessionError;
import de.vectordata.skynet.net.packet.P08RestoreSession;
import de.vectordata.skynet.net.packet.P09RestoreSessionResponse;
import de.vectordata.skynet.util.Callback;

public class Authenticator {

    public static void authenticate(Session session, Callback<RestoreSessionError> callback) {
        List<P08RestoreSession.ChannelItem> channelItems = null; // TODO initialize
        SkynetContext.getCurrent()
                .getNetworkManager()
                .sendPacket(new P08RestoreSession(session.getAccountId(), session.getSessionKeys().getKeyHash(), session.getSessionId(), channelItems))
                .waitForPacket(P09RestoreSessionResponse.class, p -> callback.onCallback(p.errorCode));
    }

}
