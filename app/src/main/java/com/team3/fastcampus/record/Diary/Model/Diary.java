package com.team3.fastcampus.record.Diary.Model;

import com.team3.fastcampus.record.InDiary.Model.InDiary;
import com.team3.fastcampus.record.Model.User;

import java.util.List;

/**
 * Created by tokijh on 2017. 3. 31..
 */

public class Diary {
    public long pk;
    public String title;
    public User author;
    public String created_date;

    public Diary() {

    }

    public Diary(long pk, String title, User author, String created_date) {
        this.pk = pk;
        this.title = title;
        this.author = author;
        this.created_date = created_date;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
