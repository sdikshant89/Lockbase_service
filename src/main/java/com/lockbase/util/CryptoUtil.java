package com.lockbase.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptoUtil {

    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;

    public static byte[] generateSalt() {
        return generateRandomBytes(SALT_LENGTH);
    }

    public static byte[] generateIv() {
        return generateRandomBytes(IV_LENGTH);
    }

    public static byte[] generateRandomBytes(int length) {
        SecureRandom sr = new SecureRandom();
        byte[] bytes = new byte[length];
        sr.nextBytes(bytes);
        return bytes;
    }

    public static SecretKeySpec deriveKeyFromPassword(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String plaintext, String password, byte[] salt, byte[] iv) throws Exception {
        SecretKeySpec key = deriveKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(ciphertext);
    }

    public static String decrypt(String encryptedBase64, String password, byte[] salt, byte[] iv) throws Exception {
        SecretKeySpec key = deriveKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

        byte[] decodedCiphertext = Base64.getDecoder().decode(encryptedBase64);
        byte[] plaintextBytes = cipher.doFinal(decodedCiphertext);

        return new String(plaintextBytes);
    }

    public static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] fromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }
}
