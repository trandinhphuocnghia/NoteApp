package com.mycode.noteapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mycode.noteapp.Dao.Notedao;
import com.mycode.noteapp.Entities.Note;

@Database(entities = Note.class,version = 1,exportSchema = false)
public abstract class Notedb extends RoomDatabase {

    private static Notedb notedb;

    public static synchronized Notedb getdb(Context context) {
        if(notedb==null){
            notedb= Room.databaseBuilder(
                    context,
                    Notedb.class,
                    "note_db"
            ).build();
        }

        return notedb;
    }

    public abstract Notedao noteDao();
}


