package com.team3.fastcampus.record.Account.Model;


import com.team3.fastcampus.record.Model.User;

/**
 * Created by tokijh on 2017. 4. 5..
 */

public class SignInData {
    public String key;
    public User user;

    public SignInData(String key) {
        this.key = key;
    }

    public SignInData(String key, User user) {
        this.key = key;
        this.user = user;
    }

}
