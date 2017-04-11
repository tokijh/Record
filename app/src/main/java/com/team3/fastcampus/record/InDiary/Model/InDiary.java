package com.team3.fastcampus.record.InDiary.Model;

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

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public long getDiary() {
        return diary;
    }

    public void setDiary(long diary) {
        this.diary = diary;
    }

    public List<Image> getPhoto_list() {
        return photo_list;
    }

    public void setPhoto_list(List<Image> photo_list) {
        this.photo_list = photo_list;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
