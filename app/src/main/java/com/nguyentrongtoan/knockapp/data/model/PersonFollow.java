package com.nguyentrongtoan.knockapp.data.model;

import com.google.firebase.database.Exclude;

import java.util.Map;

/**
 * Created by nguyentrongtoan on 5/19/17.
 */

public class PersonFollow {
    public String name;
    public String photoUrl;
    public Map<String, Boolean> follower;

    private String UID;

    public PersonFollow() {
        //for firebase
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
