package io.magicpants.hkonsapp;

/**
 * model class for users
 * Created by Erik on 17.01.2018.
 */

public class Users {

    public String username;
    public String mailaddress;
    public String notificationtoken;

    public Users(){}

    void setUsername(String username){this.username = username;}
    String getUsername(){return username;}
    void setMailAddress(String mailAddress){this.mailaddress = mailAddress;}
    String getMailAddress(){return mailaddress;}
    void setNotificationToken(String notificationToken){this.notificationtoken = notificationToken;}
    String getNotificationToken(){return notificationtoken;}


}
