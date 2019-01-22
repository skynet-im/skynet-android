package de.vectordata.skynet.crypto.hash;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.vectordata.libargon2.Argon2;
import de.vectordata.libargon2.Argon2Type;
import de.vectordata.libargon2.Argon2Version;
import de.vectordata.skynet.crypto.keys.KeyStore;
import de.vectordata.skynet.util.Callback;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
public class HashProvider {

    private static final String TAG = "HashProvider";

    private static final int TIME_COST = 2;
    private static final int MEM_COST = 8192;
    private static final int PARALLELISM = 4;

    public static void buildHashesAsync(String username, String password, Callback<KeyCollection> completed) {
        new Thread(() -> {
            byte[] argon2Data = argon2(username, password);
            KeyStore loopbackChannelKeys = KeyStore.from64ByteArray(argon2Data);
            byte[] keyHash = sha256(argon2Data);
            completed.onCallback(new KeyCollection(loopbackChannelKeys, keyHash));
        }).start();
    }

    private static byte[] argon2(String username, String password) {
        byte[] passwordBytes = getBytes(password);
        byte[] saltBytes = sha256(getBytes(username));
        return Argon2.hash(TIME_COST, MEM_COST, PARALLELISM, passwordBytes, saltBytes, Argon2Type.ARGON2ID, Argon2Version.VERSION_13, 64);
    }

    private static byte[] sha256(byte[] buf) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(buf);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to compute SHA-256 hash", e);
            return null;
        }
    }

    public static byte[] sha512(byte[] buf) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(buf);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to compute SHA-512 hash", e);
            return null;
        }
    }

    private static byte[] getBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

}
