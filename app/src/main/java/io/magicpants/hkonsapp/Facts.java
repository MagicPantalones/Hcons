package io.magicpants.hkonsapp;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;

/**
 * Parser for Firebase Database
 * Created by Erik on 18.12.2017.
 */

public class Facts {
    public String content;
    public String creator;
    public String timestamp;

    public Facts(){}

    Facts(String content, String creator, String timestamp){
        this.content = content;
        this.creator = creator;
        this.timestamp = timestamp;
    }

    void setContent(String text){this.content = text;}
    String getContent(){return content;}
    void setCreator(String creator){this.creator = creator;}
    String getCreator(){return creator;}
    void setTimestamp(String timestamp){this.timestamp = timestamp;}
    String getTimestamp(){return timestamp;}
}
