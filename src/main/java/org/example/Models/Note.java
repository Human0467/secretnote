package org.example.Models;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Note {

    private final String noteId;
    private final String hashedNoteId;
    private final String cipherText;

    public Note(String clearText) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        this.noteId = NanoIdUtils.randomNanoId().substring(0,16);
        System.out.println(noteId);
        this.hashedNoteId = hashNoteId();
        this.cipherText = encryptNote();
    }

    private String hashNoteId() throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(this.noteId.getBytes());
        return bytesToHex(messageDigest.digest());
    }

    private String encryptNote() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey = new SecretKeySpec(this.noteId.getBytes(), "AES");
        c.init(Cipher.ENCRYPT_MODE, secretKey);
        return bytesToHex(c.doFinal());
    }

    private String bytesToHex(byte[] bytes) {
        HexFormat hexFormat = HexFormat.of();
        return hexFormat.formatHex(bytes);
    }

    private byte[] hexToBytes(String hex){
        HexFormat hexFormat = HexFormat.of();
        return hexFormat.parseHex(hex);
    }

    public String getNoteId() {
        return noteId;
    }

    public String getHashedNoteId() {
        return hashedNoteId;
    }

    public String getCipherText() {
        return cipherText;
    }
}
