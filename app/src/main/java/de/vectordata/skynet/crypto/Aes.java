package de.vectordata.skynet.crypto;

import android.util.Log;

import java.io.StreamCorruptedException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.vectordata.skynet.crypto.keys.ChannelKeys;
import de.vectordata.skynet.net.client.ByteUtils;

public class Aes {

    private static final String TAG = "Aes";

    private static SecureRandom random;

    /**
     * Executes an AES encryption.
     *
     * @param buffer Plaintext.
     * @param key    AES key (128 or 256 bit).
     * @param iv     Initialization vector (128 bit).
     * @return
     */
    private static byte[] encrypt(byte[] buffer, byte[] key, byte[] iv) {
        if (key == null)
            throw new IllegalArgumentException("key must not be null");
        if (key.length < 32)
            throw new IllegalArgumentException("Key has to be 256 bit (was " + key.length + ")");

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(buffer);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "Failed to encrypt", e);
        }
        return null;
    }

    /**
     * Executes an AES decryption.
     *
     * @param buffer Ciphertext.
     * @param key    AES key (128 or 256 bit).
     * @param iv     Initialization vector (128 bit).
     * @return
     */
    private static byte[] decrypt(byte[] buffer, byte[] key, byte[] iv) {
        if (key.length < 32)
            throw new IllegalArgumentException("Key has to be 256 bit (was " + key.length + ")");

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(buffer);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "Failed to decrypt", e);
        }
        return null;
    }

    /**
     * Generates a new 256 bit AES key
     *
     * @return
     */
    public static byte[] generateKey() {
        return generateRandom(32);
    }

    /**
     * Generates a new 128 bit initialization vector.
     *
     * @return
     */
    private static byte[] generateIV() {
        return generateRandom(16);
    }

    private static byte[] generateRandom(int length) {
        if (random == null)
            random = new SecureRandom();

        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }

    public static byte[] decryptSigned(byte[] input, ChannelKeys channelKeys) throws StreamCorruptedException {
        byte[] hmac = ByteUtils.takeBytes(input, 32, 0);
        byte[] iv = ByteUtils.takeBytes(input, 16,32);
        byte[] ciphertext = ByteUtils.takeBytes(input, input.length - 48, 48);
        if (!ByteUtils.sequenceEqual(hmac, Hmac.computeHmacSHA256(ByteUtils.concatBytes(iv, ciphertext), channelKeys.getHmacKey())))
            throw new StreamCorruptedException("Data corrupted: HMAC invalid");
        return decrypt(ciphertext, channelKeys.getAesKey(), iv);
    }

    public static byte[] encryptSigned(byte[] input, ChannelKeys channelKeys) {
        byte[] iv = generateIV();
        byte[] ciphertext = encrypt(input, channelKeys.getAesKey(), iv);
        byte[] hmac = Hmac.computeHmacSHA256(ByteUtils.concatBytes(iv, ciphertext), channelKeys.getHmacKey());
        // Some allocations could be avoided but would make the code harder to maintain
        return ByteUtils.concatBytes(hmac, iv, ciphertext);
    }

}
