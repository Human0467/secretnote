package org.example.repositories;

import io.javalin.http.Context;
import org.example.DB;
import org.example.Models.Note;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Repository {

    public static Note receiveNote(Context ctx) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

        NoteRepository noteRepository = new NoteRepository();

        String clearText = ctx.formParamAsClass("note", String.class).get();
        String noteId = noteRepository.generateRandomNoteId();
        String hashedNoteId = noteRepository.hashNoteId(noteId);
        String cipherText = noteRepository.encryptNote(noteId, clearText);

        Note note = new Note.NoteBuilder()
                .NoteId(noteId)
                .hashedNoteId(hashedNoteId)
                .cipherText(cipherText)
                .build();
        return note;
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

