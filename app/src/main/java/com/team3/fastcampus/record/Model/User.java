package com.team3.fastcampus.record.Model;

/**
 * Created by tokijh on 2017. 4. 9..
 */

public class User {
    private String username;
    private String nickname;
    private String user_type;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}
