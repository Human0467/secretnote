package org.example.controllers;

import io.javalin.http.*;
import org.example.Models.Note;
import org.example.repositories.NoteRepository;
import org.example.repositories.Repository;

import javax.crypto.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SQLException, InvalidAlgorithmParameterException {

        // get the note
        Note note = Repository.receiveNote(ctx);

        // store note
        Repository.insertNote(note);

        // render success page
        renderSuccess(ctx, note.getNoteId());
    }


    public static void retrieveMessage(Context ctx) throws NoSuchAlgorithmException, SQLException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        NoteRepository noteRepository = new NoteRepository();

        String noteId = ctx.pathParam("noteId");
        String hashedNoteId = noteRepository.hashNoteId(noteId);
        String cipherText = Repository.getEncyptedNote(hashedNoteId);
        String clearText = noteRepository.decryptNote(noteId, cipherText);

        ctx.render("/messageDisplay.html", Map.of("clearText", clearText));
    }
}
