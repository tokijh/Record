package com.team3.fastcampus.record.Diary.Modal;

import com.team3.fastcampus.record.InDiary.Modal.InDiary;
import com.team3.fastcampus.record.Model.User;

/**
 * Created by tokijh on 2017. 3. 31..
 */

public class Diary {
    public long pk;
    public String title;
    public User author;
    public InDiary post;
    public String created_date;

    public Diary() {

    }

    public Diary(long pk, String title, User author, InDiary post, String created_date) {
        this.pk = pk;
        this.title = title;
        this.author = author;
        this.post = post;
        this.created_date = created_date;
    }
}
