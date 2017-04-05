package com.team3.fastcampus.record.InDiary.Domain;

import java.util.List;

/**
 * Created by tokijh on 2017. 4. 4..
 */

public class InDiary {
    public long id;
    public String title;
    public String date;
    public String content;
    public List<Image> images;

    public InDiary() {

    }

    public InDiary(long id, String title, String date, String content, List<Image> images) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
        this.images = images;
    }
}
