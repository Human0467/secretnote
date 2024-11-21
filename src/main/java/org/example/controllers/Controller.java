package org.example.controllers;

import io.javalin.http.*;
import org.example.Models.Note;
import org.example.repositories.NoteRepository;
import org.example.repositories.NoteRepository;

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

    public static void renderError(Context ctx){
        ctx.render("/error.html");
    }

    public static void renderSuccess(Context ctx, String hashedNoteId){
        ctx.render("/successPage.html", Map.of("noteId", hashedNoteId));
    }

    public static void renderRetrievedMessage(Context ctx, String clearText) {
        ctx.render("/messageDisplay.html", Map.of("clearText", clearText));
    }

    public static void storeMessage(Context ctx) {

        NoteRepository noteRepository = new NoteRepository();

        String clearText;
        String noteId = "";
        String hashedNoteId = "";
        String cipherText = "";

        try {
            clearText = NoteRepository.getNoteFromForm(ctx);
            noteId = noteRepository.generateRandomNoteId();
            hashedNoteId = noteRepository.hashNoteId(noteId);
            cipherText = noteRepository.encryptNote(noteId, clearText);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 NoSuchAlgorithmException e){
            System.out.println("Unable to encrypt message. Please try again.");
        }

        Note note = new Note.NoteBuilder()
                .hashedNoteId(hashedNoteId)
                .cipherText(cipherText)
                .build();

        try {
            NoteRepository.insertNote(note);
        } catch (SQLException e) {
            System.out.println("database error, unable to store note");
        }
        renderSuccess(ctx, noteId);
    }
    
    public static void retrieveMessage(Context ctx) {
        NoteRepository noteRepository = new NoteRepository();

        String clearText;
        String cipherText;
        String hashedNoteId = "";

        try {
            String noteId = ctx.pathParam("noteId");
            hashedNoteId = noteRepository.hashNoteId(noteId);
            cipherText = NoteRepository.getEncyptedNote(hashedNoteId);
            clearText = noteRepository.decryptNote(noteId, cipherText);
            renderRetrievedMessage(ctx, clearText);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            System.out.println("an encryption error occured.");
            System.out.println("please try again!");
            ctx.render("/messageForm.html");
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("unable to load message, please ensure link is correct");
            renderError(ctx);
        }

        try {
            noteRepository.deleteNoteByHashedId(hashedNoteId);
        } catch (SQLException e) {
            System.out.println("unable to delete message!");
        }
    }

}
