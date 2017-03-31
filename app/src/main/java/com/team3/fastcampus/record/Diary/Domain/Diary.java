package com.team3.fastcampus.record.Diary.Domain;

/**
 * Created by tokijh on 2017. 3. 31..
 */

public class Diary {
    public long id;
    public String title;
    public String date;
    public String location;
    public String image;

    public Diary() {

    }

    public Diary(long id, String title, String date, String location, String image) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.location = location;
        this.image = image;
    }
}
