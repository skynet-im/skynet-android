package de.vectordata.skynet.crypto;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;

import de.vectordata.skynet.net.packet.model.AsymmetricKey;
import de.vectordata.skynet.net.packet.model.KeyFormat;

/**
 * Created by Twometer on 21.01.2019.
 * (c) 2019 Twometer
 */
public class EC {

    public static KeyMaterial generateKeypair() {
        try {
            KeyMaterial material = new KeyMaterial();
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(256);
            KeyPair kp = kpg.generateKeyPair();
            material.privateKey = kp.getPrivate().getEncoded();
            material.publicKey = kp.getPublic().getEncoded();
            return material;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] sign(byte[] privateKey, byte[] data) {
        try {
            KeyFactory kf = KeyFactory.getInstance("EC");
            PKCS8EncodedKeySpec pSpec = new PKCS8EncodedKeySpec(privateKey);
            PrivateKey key = kf.generatePrivate(pSpec);
            Signature dsa = Signature.getInstance("SHA1withECDSA");
            dsa.initSign(key);
            dsa.update(data);
            return dsa.sign();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] deriveKey(byte[] privateKey, byte[] publicKey) {
        if (publicKey == null || privateKey == null) {
            return null;
        }
        try {
            KeyFactory kf = KeyFactory.getInstance("EC");

            X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(publicKey);
            PublicKey otherPubKey = kf.generatePublic(pkSpec);

            PKCS8EncodedKeySpec pSpec = new PKCS8EncodedKeySpec(privateKey);
            PrivateKey privKey = kf.generatePrivate(pSpec);

            KeyAgreement ka = KeyAgreement.getInstance("ECDH");
            ka.init(privKey);
            ka.doPhase(otherPubKey, true);
            return ka.generateSecret();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class KeyMaterial {
        byte[] privateKey;
        byte[] publicKey;

        KeyMaterial() {

        }

        public AsymmetricKey getPrivateKey() {
            return new AsymmetricKey(KeyFormat.BOUNCY_CASTLE, privateKey);
        }

        public AsymmetricKey getPublicKey() {
            return new AsymmetricKey(KeyFormat.BOUNCY_CASTLE, publicKey);
        }
    }

}
