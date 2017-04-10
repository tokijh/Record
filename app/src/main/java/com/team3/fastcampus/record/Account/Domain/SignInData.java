package com.team3.fastcampus.record.Account.Domain;

/**
 * Created by tokijh on 2017. 4. 5..
 */

public class SignInData {
    private String key;

    public SignInData(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
