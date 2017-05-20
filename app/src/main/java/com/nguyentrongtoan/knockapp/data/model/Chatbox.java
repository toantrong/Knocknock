package com.nguyentrongtoan.knockapp.data.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

public class Chatbox {
    public String lastMessage;
    public HashMap<String, Boolean> member;

    private String keyBox;
    private Person person;

    public Chatbox() {

    }

    public Chatbox(HashMap<String, Boolean> member, String lastMessage) {
        this.keyBox = keyBox;
        this.member = member;
        this.lastMessage = lastMessage;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("member", member);
        map.put("lastMessage", lastMessage);

        return map;
    }

    @Exclude
    public String getKeyBox() {
        return keyBox;
    }

    @Exclude
    public Person getPerson() {
        return person;
    }

    public void setKeyBox(String keyBox) {
        this.keyBox = keyBox;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
