package com.team3.fastcampus.record.Diary.Model;

import io.realm.RealmObject;

/**
 * Created by tokijh on 2017. 3. 31..
 */

public class Diary extends RealmObject {
    public String username;
    public long pk;
    public String title;
    public String created_date;

    public Diary() {

    }

    public Diary(String username, long pk, String title, String created_date) {
        this.username = username;
        this.pk = pk;
        this.title = title;
        this.created_date = created_date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
