package com.mycode.noteapp.Listeners;

import com.mycode.noteapp.Entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
