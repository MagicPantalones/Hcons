package io.magicpants.hkonsapp;

/**
 * model class for user post history
 * Created by Erik on 17.01.2018.
 */

public class UserPosts {
    String fact;
    String timestamp;

    public UserPosts(){}

    UserPosts(String fact, String timeStamp){
        this.fact = fact;
        this.timestamp = timeStamp;
    }

    void setFact(String fact){this.fact = fact;}
    String getFact(){return fact;}
    void setTimeStamp(String timeStamp){this.timestamp = timeStamp;}
    String getTimeStamp(){return timestamp;}
}
