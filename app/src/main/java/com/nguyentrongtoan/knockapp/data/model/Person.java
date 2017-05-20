package com.nguyentrongtoan.knockapp.data.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by nguyentrongtoan on 5/11/17.
 */


@IgnoreExtraProperties
public class Person {
    public String name;
    public String status;
    public String photoUrl;
    public String coverUrl;

    private String UID;

    public Person() {
        //for firebase
    }

    public Person(String name, String status, String photoUrl, String coverUrl) {
        this.name = name;
        this.status = status;
        this.photoUrl = photoUrl;
        this.coverUrl = coverUrl;
    }

    @Exclude
    public void setUID(String UID) {
        this.UID = UID;
    }

    @Exclude
    public String getUID() {
        return UID;
    }

}
