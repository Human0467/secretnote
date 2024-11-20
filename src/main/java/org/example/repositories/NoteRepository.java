package org.example.repositories;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.HexFormat;

public class NoteRepository {

    public final String generateRandomNoteId() {
        return NanoIdUtils.randomNanoId().substring(0, 16);
    }

    public final String hashNoteId(String noteId) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(noteId.getBytes());
        return bytesToHex(messageDigest.digest());
    }

    public final String encryptNote(String noteId, String clearText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey = new SecretKeySpec(noteId.getBytes(), "AES");
        c.init(Cipher.ENCRYPT_MODE, secretKey);
        String cipherText = bytesToHex(c.doFinal(clearText.getBytes()));
        String iv = bytesToHex(c.getIV());
        return iv+cipherText;
    }


    public final String decryptNote(String noteId, String cipherRecord) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] cipherTextBytes = hexToBytes(cipherRecord);
        byte[] iv = Arrays.copyOfRange(cipherTextBytes, 0, 12);
        byte[] cipherText = Arrays.copyOfRange(cipherTextBytes, 12, cipherTextBytes.length );
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey = new SecretKeySpec(noteId.getBytes(), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv); // 128-bit authentication tag
        c.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        return new String(c.doFinal(cipherText));
    }

    public final String bytesToHex(byte[] bytes) {
        HexFormat hexFormat = HexFormat.of();
        return hexFormat.formatHex(bytes);
    }

    public final byte[] hexToBytes(String hex){
        HexFormat hexFormat = HexFormat.of();
        return hexFormat.parseHex(hex);
    }
}
