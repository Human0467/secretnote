package org.example.Models;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.example.repositories.NoteRepository;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Note {


    private final String noteId;
    private final String hashedNoteId;
    private final String clearText;
    private final String cipherText;

    private Note(NoteBuilder builder){
        this.noteId=builder.noteId;
        this.hashedNoteId=builder.hashedNoteId;
        this.clearText=builder.clearText;
        this.cipherText=builder.cipherText;
    }

    public String getNoteId() {
        return noteId;
    }

    public String getHashedNoteId() {
        return hashedNoteId;
    }

    public String getClearText() {
        return clearText;
    }

    public String getCipherText() {
        return cipherText;
    }

    public static class NoteBuilder{
        private String noteId;
        private String hashedNoteId;
        private String clearText;
        private String cipherText;

        public NoteBuilder NoteId(String noteId){
            this.noteId = noteId;
            return this;
        }

        public NoteBuilder hashedNoteId(String hashedNoteId){
            this.hashedNoteId = hashedNoteId;
            return this;
        }

        public NoteBuilder clearText(String clearText){
            this.clearText = clearText;
            return this;
        }

        public NoteBuilder cipherText(String cipherText){
            this.cipherText = cipherText;
            return this;
        }

        public Note build() {
            return new Note(this);
        }
    }
}
