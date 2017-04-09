package com.team3.fastcampus.record.InDiary.Modal;

import java.util.List;

/**
 * Created by tokijh on 2017. 4. 4..
 */

public class InDiary {
    public long pk;
    public long diary;
    public List<Image> photo_list;
    public String created_date;

    public InDiary() {

    }

    public InDiary(long pk, long diary, List<Image> photo_list, String created_date) {
        this.pk = pk;
        this.diary = diary;
        this.photo_list = photo_list;
        this.created_date = created_date;
    }
}
