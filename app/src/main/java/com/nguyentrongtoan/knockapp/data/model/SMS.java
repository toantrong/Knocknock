package com.nguyentrongtoan.knockapp.data.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

@IgnoreExtraProperties
public class SMS {
    public String message;
    public long date;
    public String attachPhoto;
    public String authorUID;

    private String keyBox;
    private Person person;

    public SMS() {
        super();
    }

    public SMS(String authorUID, String message, String attachPhoto) {
        this.authorUID = authorUID;
        this.message = message;
        this.attachPhoto = attachPhoto;
        this.date = new Date().getTime();
    }

    @Exclude
    public Person getPerson() {
        return person;
    }

    @Exclude
    public String getKeyBox() {
        return keyBox;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setKeyBox(String keyBox) {
        this.keyBox = keyBox;
    }
}
