package com.team3.fastcampus.record.Model;

/**
 * Created by tokijh on 2017. 4. 9..
 */

public class User {
    public String username;
    public String nickname;
    public String user_type;

    public User() {

    }

    public User(String username, String nickname, String user_type) {
        this.username = username;
        this.nickname = nickname;
        this.user_type = user_type;
    }
}
