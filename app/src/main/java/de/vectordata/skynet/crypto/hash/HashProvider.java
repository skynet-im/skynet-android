package de.vectordata.skynet.crypto.hash;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.vectordata.libargon2.Argon2;
import de.vectordata.libargon2.Argon2Type;
import de.vectordata.libargon2.Argon2Version;
import de.vectordata.skynet.crypto.keys.KeyStore;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
public class HashProvider {

    private static final String TAG = "HashProvider";

    private static final int TIME_COST = 2;
    private static final int MEM_COST = 8192;
    private static final int PARALLELISM = 4;

    public static void buildHashesAsync(String username, String password, HashResultCallback callback) {
        new Thread(() -> {
            byte[] passwordBytes = getBytes(password);
            byte[] saltBytes = sha256(getBytes(username));
            byte[] argon2Data = Argon2.hash(TIME_COST, MEM_COST, PARALLELISM, passwordBytes, saltBytes, Argon2Type.ARGON2ID, Argon2Version.VERSION_13, 64);
            KeyStore loopbackChannelKeys = KeyStore.from64ByteArray(argon2Data);
            byte[] keyHash = sha256(argon2Data);
            callback.onFinished(new HashResult(loopbackChannelKeys, keyHash));
        }).start();
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

    private static byte[] getBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
