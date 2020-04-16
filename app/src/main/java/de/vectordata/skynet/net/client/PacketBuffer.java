package de.vectordata.skynet.net.client;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import de.vectordata.skynet.util.date.DateTime;

/**
 * Created by Twometer on 07.03.2018.
 * (c) 2018 Twometer
 */

@SuppressWarnings("WeakerAccess")
public class PacketBuffer {
    public static int SIZE_SMALL = 8192;    // 8K
    public static int SIZE_MEDIUM = 32767;  // 32K
    public static int SIZE_BIG = 131072;    // 128K

    private ByteBuffer buffer;

    private int allocated;

    public PacketBuffer(byte[] buf) {
        buffer = ByteBuffer.wrap(buf);
    }

    public PacketBuffer(int size) {
        buffer = ByteBuffer.allocate(size);
    }

    public PacketBuffer() {
        this(SIZE_SMALL);
    }

    public int getLength() {
        return buffer.capacity();
    }

    public DateTime readDate() {
        return DateTime.fromBinary(readInt64());
    }

    public void writeDate(DateTime date) {
        writeInt64(date.toBinary());
    }

    public void writeString(String string, LengthPrefix prefix) {
        writeByteArray(string.getBytes(StandardCharsets.UTF_8), prefix);
    }

    public String readString(LengthPrefix prefix) {
        return new String(readByteArray(prefix));
    }

    public void writeBool(boolean bool) {
        writeByte(bool ? 0x01 : (byte) 0x00);
    }

    public boolean readBool() {
        return readByte() == 0x01;
    }

    public void writeByte(byte b) {
        writeBytes(new byte[]{b});
    }

    public byte readByte() {
        return buffer.get();
    }

    public void writeUInt8(int b) {
        writeByte((byte) b);
    }

    public int readUInt8() {
        return buffer.get() & 0xFF;
    }

    public void writeByteArray(byte[] arr, LengthPrefix prefix) {
        switch (prefix) {
            case SHORT:
                writeUInt8(arr.length);
                break;
            case MEDIUM:
                writeUInt16(arr.length);
                break;
            case LONG:
                writeInt32(arr.length);
                break;
            case NONE:
                // Well, write none.
                break;
            default:
                throw new IllegalArgumentException("Unknown LengthPrefix " + prefix);
        }
        writeBytes(arr);
    }

    public byte[] readByteArray(LengthPrefix prefix) {
        int len;
        switch (prefix) {
            case SHORT:
                len = readUInt8();
                break;
            case MEDIUM:
                len = readUInt16();
                break;
            case LONG:
                len = readInt32();
                break;
            case NONE:
                throw new IllegalArgumentException("Can't read an array with LengthPrefix none if no fixed length is specified. Use readBytes(int) instead.");
            default:
                throw new IllegalArgumentException("Unknown LengthPrefix " + prefix);
        }
        return readBytes(len);
    }

    public void writeInt16(short i) {
        writeUInt16(i); // I know, I'm lazy, but it's the same code. The bytes are the same.
    }

    public short readInt16() {
        return ByteBuffer.wrap(ByteUtils.reverseBytes(readBytes(2))).getShort();
    }

    public void writeUInt16(int i) {
        writeBytes(new byte[]{(byte) (i), (byte) (i >> 8)});
    }

    public int readUInt16() {
        byte[] bytes = readBytes(2);
        return ByteBuffer.wrap(new byte[]{0, 0, bytes[1], bytes[0]}).getInt();
    }

    public void writeInt32(int i) {
        writeUInt32(i);
    }

    public int readInt32() {
        byte[] b = readBytes(4);
        return ByteBuffer.wrap(new byte[]{b[3], b[2], b[1], b[0]}).getInt();
    }

    public void writeUInt32(long i) {
        writeBytes(new byte[]{(byte) (i), (byte) (i >> 8), (byte) (i >> 16), (byte) (i >> 24)});
    }

    public void writeInt64(long l) {
        byte[] b = new byte[]{(byte) (l), (byte) (l >> 8), (byte) (l >> 16), (byte) (l >> 24), (byte) (l >> 32), (byte) (l >> 40), (byte) (l >> 48), (byte) (l >> 56)};
        writeBytes(b);
    }

    public long readInt64() {
        byte[] b = readBytes(8);
        return ((((long) b[0])) & 0x00000000000000FFL)
                | ((((long) b[1]) << 8) & 0x000000000000FF00L)
                | ((((long) b[2]) << 16) & 0x0000000000FF0000L)
                | ((((long) b[3]) << 24) & 0x00000000FF000000L)
                | ((((long) b[4]) << 32) & 0x000000FF00000000L)
                | ((((long) b[5]) << 40) & 0x0000FF0000000000L)
                | ((((long) b[6]) << 48) & 0x00FF000000000000L)
                | ((((long) b[7]) << 56) & 0xFF00000000000000L);
    }

    private void writeBytes(byte[] array) {
        allocated += array.length;
        buffer.put(array);
    }

    public byte[] readBytes(int amount) {
        if (amount == 0)
            return new byte[]{};

        if (buffer.remaining() == 0)
            throw new IllegalStateException("Buffer underflow: Requested to read " + amount + " bytes from PacketBuffer, but buffer is empty.");

        if (buffer.remaining() < amount)
            amount = buffer.remaining();
        byte[] buf = new byte[amount];
        buffer.get(buf);
        return buf;
    }

    public byte[] toArray() {
        return ByteUtils.takeBytes(buffer.array(), allocated, 0);
    }

    public byte[] readToEnd() {
        byte[] arr = new byte[buffer.remaining()];
        buffer.get(arr);
        return arr;
    }

    public boolean hasBytes() {
        return buffer.hasRemaining();
    }

}
