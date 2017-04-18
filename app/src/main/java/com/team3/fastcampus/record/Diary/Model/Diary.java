package com.team3.fastcampus.record.Diary.Model;

import io.realm.RealmObject;

/**
 * Created by tokijh on 2017. 3. 31..
 */

public class Diary extends RealmObject {
    // 계정을 구별 하기 위한 username
    public String username;

    public long pk;
    public String title;
    public String post_count;
    public String start_date;
    public String end_date;
    public String cover_image;
}
