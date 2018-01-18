package io.magicpants.hkonsapp;

import android.support.annotation.Nullable;

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
    public String resurl;

    public Facts(){}

    Facts(String content, String creator, String timestamp, @Nullable String resurl){
        this.content = content;
        this.creator = creator;
        this.timestamp = timestamp;
        this.resurl = resurl;
    }

    void setContent(String text){this.content = text;}
    String getContent(){return content;}
    void setCreator(String creator){this.creator = creator;}
    String getCreator(){return creator;}
    void setTimestamp(String timestamp){this.timestamp = timestamp;}
    String getTimestamp(){return timestamp;}
    void setResurl(String resurl){this.resurl = resurl;}
    String getResurl(){return resurl;}
}
