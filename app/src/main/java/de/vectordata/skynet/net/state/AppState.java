package de.vectordata.skynet.net.state;

import android.util.LongSparseArray;

import de.vectordata.skynet.net.packet.model.ChannelAction;
import de.vectordata.skynet.net.packet.model.OnlineState;

public class AppState {

    private OnlineState onlineState = OnlineState.ACTIVE;

    private LongSparseArray<ChannelAction> channelActions = new LongSparseArray<>();

    public ChannelAction getChannelAction(long channelId) {
        ChannelAction action = channelActions.get(channelId);
        return action == null ? ChannelAction.NONE : action;
    }

    public void setChannelAction(long channelId, long accountId, ChannelAction channelAction) {
        // TODO add multi account support here for groups
        if (channelAction == ChannelAction.NONE)
            channelActions.remove(channelId);
        else
            channelActions.put(channelId, channelAction);
    }

    public OnlineState getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(OnlineState onlineState) {
        this.onlineState = onlineState;
    }

}
