package de.vectordata.skynet;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;

import de.vectordata.libargon2.Argon2;
import de.vectordata.libargon2.Argon2Type;
import de.vectordata.libargon2.Argon2Version;
import de.vectordata.skynet.net.client.ByteUtils;

@RunWith(AndroidJUnit4.class)
public class CryptoTests {

    @Test
    public void testArgon2() {
        byte[] password = "password".getBytes(StandardCharsets.UTF_8);
        byte[] salt = new byte[]{0x22, -0x4f, 0x0d, 0x1c, 0x1c, -0x6e, 0x42, -0x26, -0x07, 0x67, -0x6e, 0x5e, 0x62, 0x53, -0x0a, -0x0d};
        byte[] hash1 = Argon2.hash(2, 2048, 4, password, salt, Argon2Type.ARGON2ID, Argon2Version.VERSION_13, 16);
        System.out.println(ByteUtils.toHexString(hash1));
        Assert.assertEquals(16, hash1.length);
        assertNonZero(hash1, 0, 16);
        byte[] hash2 = Argon2.hash(4, 1024, 8, password, salt, Argon2Type.ARGON2ID, Argon2Version.VERSION_13, 96);
        System.out.println(ByteUtils.toHexString(hash2));
        Assert.assertEquals(96, hash2.length);
        assertNonZero(hash2, 0, 96);
        assertNonZero(hash2, 64, 32);
    }

    private void assertNonZero(byte[] array, int offset, int count) {
        for (int i = offset; i < offset + count; i++) {
            byte b = array[i];
            if (b != 0) {
                return;
            }
        }
        Assert.fail("An array consists only of zero bytes");
    }
}
