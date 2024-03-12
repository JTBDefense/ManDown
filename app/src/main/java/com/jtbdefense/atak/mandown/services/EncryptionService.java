package com.jtbdefense.atak.mandown.services;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.decode;
import static android.util.Base64.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionService {

    private static final String INITIAL_VECTOR = "k8GRxh6g8306";
    private static final String KEY = "z4JMsl";

    public static byte[] encrypt(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(INITIAL_VECTOR.getBytes());
        byte[] ivBytes = md.digest();

        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(KEY.getBytes());
        byte[] keyBytes = sha.digest();

        return encrypt(ivBytes, keyBytes, bytes);
    }

    static byte[] encrypt(byte[] ivBytes, byte[] keyBytes, byte[] bytes) throws Exception {
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(bytes);
    }

    public static byte[] decrypt(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(INITIAL_VECTOR.getBytes());
        byte[] ivBytes = md.digest();

        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(KEY.getBytes());
        byte[] keyBytes = sha.digest();

        return decrypt(ivBytes, keyBytes, bytes);
    }

    static byte[] decrypt(byte[] ivBytes, byte[] keyBytes, byte[] bytes) throws Exception {
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(bytes);
    }

    public static String encryptStrAndToBase64(String enStr) throws Exception {
        byte[] bytes = encrypt(enStr.getBytes(UTF_8));
        return new String(encode(bytes, DEFAULT), UTF_8);
    }

    public static String decryptStrAndFromBase64(String deStr) throws Exception {
        byte[] bytes = decrypt(decode(deStr.getBytes(UTF_8), DEFAULT));
        return new String(bytes, UTF_8);
    }
}