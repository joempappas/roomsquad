package com.example.mishaberkovich.roomsquad;


import java.lang.String;


/**
 * Created by mishaberkovich on 2/4/16.
 */
public class User {
    public long userId;
    public String username;
    public String password;
    public String firstname;
    public String lastname;



    public User(long userId, String username, String password){
        this.userId=userId;
        this.username=username;
        this.password=password;
    }

    public void EditName(String d_firstname, String d_lastname){
        if (d_firstname.length() > 0 && d_firstname.length() > 0){
            this.firstname = d_firstname;
            this.lastname = d_lastname;
        }

    }
}


