package org.example.controllers;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.javalin.http.*;
import org.example.Models.Note;
import org.example.repositories.Repository;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;

public class Controller {

    public static void renderForm(Context ctx) {
        ctx.render("/messageForm.html");
    }

    public static void renderSuccess(Context ctx, String hashedNoteId){
        ctx.render("/successPage.html", Map.of("noteId", hashedNoteId));
    }

    public static void storeMessage(Context ctx)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SQLException {

        // get the note
        Note note = Repository.receiveNote(ctx);

        // store note
        Repository.insertNote(note);

        // render success page
        renderSuccess(ctx, note.getHashedNoteId());
    }


    public static void retrieveMessage(Context ctx) {

    }
}
