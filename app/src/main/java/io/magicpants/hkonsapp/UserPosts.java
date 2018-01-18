package io.magicpants.hkonsapp;

/**
 * model class for user post history
 * Created by Erik on 17.01.2018.
 */

public class UserPosts {
    public String fact;
    public String timestamp;

    public UserPosts(){}

    UserPosts(String fact, String timestamp){
        this.fact = fact;
        this.timestamp = timestamp;
    }

    void setFact(String fact){this.fact = fact;}
    String getFact(){return fact;}
    void setTimestamp(String timeStamp){this.timestamp = timestamp;}
    String getTimestamp(){return timestamp;}
}
