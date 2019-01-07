package de.vectordata.skynet.net.packet.model;

public interface GroupMemberFlags {
    byte NONE = 0;
    byte ADMINISTRATOR = 1;
    byte NO_CONTENT = 2;
    byte NO_METADATA = 4;
    byte INVISIBLE = 8;
}
