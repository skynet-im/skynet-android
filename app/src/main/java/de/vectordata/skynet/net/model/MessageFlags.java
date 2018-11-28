package de.vectordata.skynet.net.model;

public interface MessageFlags {

    byte NONE = 0;
    byte LOOPBACK = 1;
    byte UNENCRYPTED = 2;
    byte FILE_ATTACHED = 4;
    byte NO_SENDER_SYNC = 8;

}
