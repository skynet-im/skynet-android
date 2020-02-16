package de.vectordata.skynet.net.client;

import java.nio.ByteBuffer;
import java.util.List;

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

    public int getRemaining() {
        return buffer.remaining();
    }

    public int getPosition() {
        return buffer.position();
    }

    public void setPosition(int pos) {
        buffer.position(pos);
    }

    public DateTime readDate() {
        return DateTime.fromBinary(readInt64());
    }

    public void writeDate(DateTime date) {
        writeInt64(date.toBinary());
    }

    public void writeString(String str) {
        writeByteArray(str.getBytes(), true);
    }

    public String readString() {
        return new String(readByteArray());
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

    public void writeByteArray(byte[] arr, boolean writeLength) {
        if (writeLength)
            writeUInt32(arr.length);
        writeBytes(arr);
    }

    public byte[] readByteArray() {
        return readBytes((int) readUInt32());
    }

    public byte[] readByteArray(int len) {
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

    public long readUInt32() {
        byte[] b = readBytes(4);
        return ByteBuffer.wrap(new byte[]{0, 0, 0, 0, b[3], b[2], b[1], b[0]}).getLong();
    }

    public void writeUInt64(long i) {
        writeBytes(new byte[]{
                (byte) (i),
                (byte) (i >>> 8),
                (byte) (i >>> 16),
                (byte) (i >>> 24),
                (byte) (i >>> 32),
                (byte) (i >>> 40),
                (byte) (i >>> 48),
                (byte) (i >>> 56)
        });
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

    private byte[] readBytes(int amount) {
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

    public void writeUInt32List(List<Long> list) {
        writeUInt16(list.size());
        for (long l : list)
            writeUInt32(l);
    }
}
