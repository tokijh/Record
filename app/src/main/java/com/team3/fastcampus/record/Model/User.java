package com.team3.fastcampus.record.Model;

import com.team3.fastcampus.record.Util.PreferenceManager;

/**
 * Created by tokijh on 2017. 4. 9..
 */

public class User {
    public String username;
    public String nickname;
    public String user_type;
    public String profile_img;
    public String introduction;

    public User() {

    }

    public User(String username, String nickname, String user_type, String profile_img, String introduction) {
        this.username = username;
        this.nickname = nickname;
        this.user_type = user_type;
        this.profile_img = profile_img;
        this.introduction = introduction;
    }

    public void save() {
        PreferenceManager.getInstance().putString("username", username)
                .putString("nickname", nickname)
                .putString("user_type", user_type)
                .putString("profile_img", profile_img)
                .putString("introduction", introduction);
    }
}
