package org.example.repositories;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.javalin.http.Context;
import org.example.DB;
import org.example.Models.Note;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HexFormat;

public class NoteRepository {

    public final String generateRandomNoteId() {
        return NanoIdUtils.randomNanoId().substring(0, 16);
    }

    public final String hashNoteId(String noteId) throws
            NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(noteId.getBytes());
        return bytesToHex(messageDigest.digest());
    }

    public final String encryptNote(String noteId, String clearText) throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey = new SecretKeySpec(noteId.getBytes(), "AES");
        c.init(Cipher.ENCRYPT_MODE, secretKey);
        String cipherText = bytesToHex(c.doFinal(clearText.getBytes()));
        String iv = bytesToHex(c.getIV());
        return iv+cipherText;
    }


    public final String decryptNote(String noteId, String cipherRecord) throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            IllegalArgumentException,
            BadPaddingException,
            InvalidAlgorithmParameterException {
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

    public final void deleteNoteByHashedId (String hashedNoteId) throws SQLException {
        String query = "DELETE FROM notes \n";
        query += "WHERE id = (?); \n";

        try (var connection = DB.getConnection();
             var statement = connection.prepareStatement(query)) {

            statement.setString(1, hashedNoteId);

            statement.executeUpdate();
        }
    }

    public static String getNoteFromForm(Context ctx) {

        String clearText = ctx.formParamAsClass("note", String.class).get();
        return clearText;
    }


    public static int insertNote(Note note) throws SQLException {

        String query = "INSERT INTO notes \n";
        query += "(id, note) \n";
        query += ("VALUES (?, ?); \n");

        // try with resources - get connection
        try (var con = DB.getConnection();
             var stmt = con.prepareStatement(query);) {

            stmt.setString(1, note.getHashedNoteId());
            stmt.setString(2, note.getCipherText());

            // returns the number of rows that were updated - should be 1 if successful
            int updatedRows = stmt.executeUpdate();

            if (updatedRows == 0) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static String getEncyptedNote(String hashedNoteId) throws SQLException {

        String query = "SELECT note FROM notes \n";
        query += "WHERE id = (?);\n";

        try (var con = DB.getConnection();
             var stmt = con.prepareStatement(query);) {

            stmt.setString(1, hashedNoteId);

            try (var rs = stmt.executeQuery();) {

                String cipherText = "";

                while (rs.next()) {
                    cipherText = rs.getString("note");
                }
                return cipherText;
            }
        }
    }
}
