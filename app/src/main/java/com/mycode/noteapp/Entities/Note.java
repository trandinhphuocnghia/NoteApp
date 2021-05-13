package com.mycode.noteapp.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;


@Entity(tableName = "notes")
public class Note implements Serializable {


    //column
    @PrimaryKey(autoGenerate = true)
    private  int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name="datetime")
    private String datetime;

    @ColumnInfo(name="subtitle")
    private String subtitle;

    @ColumnInfo(name="textnote")
    private String textnote;

    @ColumnInfo(name="color")
    private String color;

    @ColumnInfo(name="imagepath")
    private String imagepath;

    @ColumnInfo(name="weblinkstring")
    private String weblinkstring;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTextnote() {
        return textnote;
    }

    public void setTextnote(String textnote) {
        this.textnote = textnote;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getWeblinkstring() {
        return weblinkstring;
    }

    public void setWeblinkstring(String weblinkstring) {
        this.weblinkstring = weblinkstring;
    }

    @NonNull
    @Override
    public String toString(){
        return title + "note was created " + datetime;
    }


}
