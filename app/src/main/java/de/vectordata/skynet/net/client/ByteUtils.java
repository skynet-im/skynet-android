package de.vectordata.skynet.net.client;

/**
 * Created by Daniel Lerch on 07.03.2018.
 * © 2018 Daniel Lerch
 */

@SuppressWarnings("WeakerAccess")
public class ByteUtils {

    @SuppressWarnings("SpellCheckingInspection")
    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    /**
     * Splits a byte array into blocks.
     *
     * @param b         byte array
     * @param blockSize size of the individual blocks
     * @return an array of arrays (blocks)
     */
    public static byte[][] splitBytes(byte[] b, int blockSize) {
        int length = b.length;
        int blocks = getTotalSize(length, blockSize) / blockSize;
        byte[][] value = new byte[blocks][];
        if (length == 0) {
            value[0] = new byte[0];
            return value;
        }
        int i;
        for (i = 0; i < blocks - 1; i++) {
            value[i] = new byte[blockSize];
            System.arraycopy(b, i * blockSize, value[i], 0, blockSize);
        }
        int pending = length - i * blockSize;
        value[blocks - 1] = new byte[pending];
        System.arraycopy(b, i * blockSize, value[blocks - 1], 0, pending);
        return value;
    }

    /**
     * Concatenates multiple byte arrays to one.
     *
     * @param byteArrays An array of byte arrays to concatenate.
     * @return The fully concatenated array
     */
    public static byte[] concatBytes(byte[]... byteArrays) {
        int n = 0;
        for (byte[] byteArray : byteArrays) {
            n += byteArray.length;
        }
        byte[] concatenated = new byte[n];
        n = 0;
        for (byte[] b : byteArrays) {
            System.arraycopy(b, 0, concatenated, n, b.length);
            n += b.length;
        }
        return concatenated;
    }

    public static byte[] skipBytes(byte[] array, int amount) {
        byte[] result = new byte[array.length - amount];
        System.arraycopy(array, amount, result, 0, result.length);
        return result;
    }

    public static byte[] takeBytes(byte[] array, int amount, int index) {
        byte[] result = new byte[amount];
        System.arraycopy(array, index, result, 0, amount);
        return result;
    }

    public static boolean sequenceEqual(byte[] first, byte[] second) {
        if (first.length != second.length) return false;
        for (int i = 0; i < first.length; i++) {
            if (first[i] != second[i]) return false;
        }
        return true;
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param buffer The byte array to convert
     * @return The hex string
     */
    public static String toHexString(byte[] buffer) {
        char[] hexChars = new char[buffer.length * 2];
        for (int j = 0; j < buffer.length; j++) {
            int v = buffer[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Gets the total size if only full blocks are allowed.
     *
     * @param normalSize The default size of the input data.
     * @param blockSize  The blocksize of the algorithm to apply on the data.
     * @return The total size
     */
    public static int getTotalSize(int normalSize, int blockSize) {
        int mod = normalSize % blockSize;
        if (mod > 0)
            return normalSize - mod + blockSize;
        else
            return normalSize;
    }

    /**
     * Gets the total size if only full blocks are allowed.
     *
     * @param normalSize The default size of the input data.
     * @param blockSize  The blocksize of the algorithm to apply on the data.
     * @return The total size
     */
    public static long getTotalSize(long normalSize, int blockSize) {
        long mod = normalSize % blockSize;
        if (mod > 0)
            return normalSize - mod + blockSize;
        else
            return normalSize;
    }

    /**
     * Reverses the byte array in-place and returns the reversed
     * byte array
     *
     * @param b The byte array to reverse
     * @return The byte array that was put into the method, but in reverse
     */
    public static byte[] reverseBytes(byte[] b) {
        if (b == null) throw new IllegalArgumentException("Array must not be null");
        int i = 0;
        int j = b.length - 1;
        byte tmp;
        while (j > i) {
            tmp = b[j];
            b[j] = b[i];
            b[i] = tmp;
            j--;
            i++;
        }
        return b;
    }
}
