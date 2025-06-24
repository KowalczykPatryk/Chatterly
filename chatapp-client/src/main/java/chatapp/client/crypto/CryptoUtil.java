package chatapp.client.crypto;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;

import chatapp.client.crypto.KeyStoreManager;
import chatapp.client.model.Message;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;

import java.util.*;

public class CryptoUtil {
    public static byte[] generateSymmetricKey() throws NoSuchAlgorithmException
    {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, new SecureRandom());
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }
    public static byte[] generateIv()
    {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }
    public static byte[] encryptWithPublicKey(byte[] symKey, String base64Pub) throws GeneralSecurityException {
        PublicKey pub = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64Pub)));
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        c.init(Cipher.ENCRYPT_MODE, pub);
        return c.doFinal(symKey);
    }

    public static byte[] encryptContent(byte[] symKey, byte[] iv, String content) throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(symKey, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, spec);
        return c.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] decryptSymKey(byte[] encryptedKey, PrivateKey priv) throws GeneralSecurityException {
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        c.init(Cipher.DECRYPT_MODE, priv);
        return c.doFinal(encryptedKey);
    }

    public static String decryptContent(byte[] ciphertext, byte[] symKey, byte[] iv) throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(symKey, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.DECRYPT_MODE, keySpec, spec);
        byte[] plain = c.doFinal(ciphertext);
        return new String(plain, StandardCharsets.UTF_8);
    }

}