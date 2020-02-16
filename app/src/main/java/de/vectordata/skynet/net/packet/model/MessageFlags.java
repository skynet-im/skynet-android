package de.vectordata.skynet.net.packet.model;

public interface MessageFlags {

    byte NONE = 0;
    byte LOOPBACK = 1;
    byte UNENCRYPTED = 2;
    byte NO_SENDER_SYNC = 4;
    byte MEDIA_MESSAGE = 8;
    byte EXTERNAL_FILE = 16;

}
