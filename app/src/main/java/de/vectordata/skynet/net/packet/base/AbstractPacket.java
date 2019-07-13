package de.vectordata.skynet.net.packet.base;

public abstract class AbstractPacket implements Packet {

    @Override
    public boolean validatePacket() {
        return true;
    }

}
