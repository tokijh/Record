package com.team3.fastcampus.record.Account.Model;

import com.team3.fastcampus.record.Model.User;

/**
 * Created by tokijh on 2017. 4. 5..
 */

public class SignUpData {
    private String token;
    private User user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
