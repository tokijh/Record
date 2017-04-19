package com.team3.fastcampus.record.InDiary.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by tokijh on 2017. 4. 4..
 */

public class InDiary extends RealmObject {
    // 계정을 구별 하기 위한 username
    public String username;

    // Diary이름
    public String title;

    public long pk;
    public long diary;
    public String content;
    public RealmList<Image> photo_list;
    public String created_date;
}
