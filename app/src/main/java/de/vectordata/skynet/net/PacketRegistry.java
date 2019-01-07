package de.vectordata.skynet.net;

import de.vectordata.skynet.net.packet.P01ConnectionResponse;
import de.vectordata.skynet.net.packet.P02FCreateChannelResponse;
import de.vectordata.skynet.net.packet.P03CreateAccountResponse;
import de.vectordata.skynet.net.packet.P05DeleteAccountResponse;
import de.vectordata.skynet.net.packet.P07CreateSessionResponse;
import de.vectordata.skynet.net.packet.P09RestoreSessionResponse;
import de.vectordata.skynet.net.packet.P0ACreateChannel;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P0FSyncFinished;
import de.vectordata.skynet.net.packet.P10RealTimeMessage;
import de.vectordata.skynet.net.packet.P13QueueMailAddressChange;
import de.vectordata.skynet.net.packet.P14MailAddress;
import de.vectordata.skynet.net.packet.P15PasswordUpdate;
import de.vectordata.skynet.net.packet.P16LoopbackKeyNotify;
import de.vectordata.skynet.net.packet.P17PrivateKeys;
import de.vectordata.skynet.net.packet.P18PublicKeys;
import de.vectordata.skynet.net.packet.P19KeypairReference;
import de.vectordata.skynet.net.packet.P1AVerifiedKeys;
import de.vectordata.skynet.net.packet.P1BDirectChannelUpdate;
import de.vectordata.skynet.net.packet.P1CDirectChannelCustomization;
import de.vectordata.skynet.net.packet.P1DGroupChannelKeyNotify;
import de.vectordata.skynet.net.packet.P1EGroupChannelUpdate;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.P21MessageOverride;
import de.vectordata.skynet.net.packet.P22MessageReceived;
import de.vectordata.skynet.net.packet.P23MessageRead;
import de.vectordata.skynet.net.packet.P24DaystreamMessage;
import de.vectordata.skynet.net.packet.P25Nickname;
import de.vectordata.skynet.net.packet.P26PersonalMessage;
import de.vectordata.skynet.net.packet.P27ProfileImage;
import de.vectordata.skynet.net.packet.P29DeviceList;
import de.vectordata.skynet.net.packet.P2ABackgroundImage;
import de.vectordata.skynet.net.packet.P2BOnlineState;
import de.vectordata.skynet.net.packet.P2CDeviceListDetails;
import de.vectordata.skynet.net.packet.P2ESearchAccountResponse;
import de.vectordata.skynet.net.packet.base.Packet;

class PacketRegistry {

    private static final Packet[] PACKETS = new Packet[255];

    static {
        register(new P01ConnectionResponse());
        register(new P03CreateAccountResponse());
        register(new P05DeleteAccountResponse());
        register(new P07CreateSessionResponse());
        register(new P09RestoreSessionResponse());
        register(new P0ACreateChannel());
        register(new P02FCreateChannelResponse());
        register(new P0BChannelMessage());
        register(new P0CChannelMessageResponse());
        register(new P0FSyncFinished());
        register(new P10RealTimeMessage());
        register(new P13QueueMailAddressChange());
        register(new P14MailAddress());
        register(new P15PasswordUpdate());
        register(new P16LoopbackKeyNotify());
        register(new P17PrivateKeys());
        register(new P18PublicKeys());
        register(new P19KeypairReference());
        register(new P1AVerifiedKeys());
        register(new P1BDirectChannelUpdate());
        register(new P1CDirectChannelCustomization());
        register(new P1DGroupChannelKeyNotify());
        register(new P1EGroupChannelUpdate());
        register(new P20ChatMessage());
        register(new P21MessageOverride());
        register(new P22MessageReceived());
        register(new P23MessageRead());
        register(new P24DaystreamMessage());
        register(new P25Nickname());
        register(new P26PersonalMessage());
        register(new P27ProfileImage());
        register(new P29DeviceList());
        register(new P2ABackgroundImage());
        register(new P2BOnlineState());
        register(new P2CDeviceListDetails());
        register(new P2ESearchAccountResponse());
    }

    private static void register(Packet packet) {
        PACKETS[packet.getId()] = packet;
    }

    static boolean isValidId(byte id) {
        int uid = id & 0xFF;
        return uid > 0 && uid < PACKETS.length && PACKETS[uid] != null;
    }

    static Packet getPacket(byte id) {
        return PACKETS[id];
    }

}
