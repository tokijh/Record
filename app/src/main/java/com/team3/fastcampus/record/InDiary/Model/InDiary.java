package com.team3.fastcampus.record.InDiary.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by tokijh on 2017. 4. 4..
 */

public class InDiary extends RealmObject {
    public String username;
    public long pk;
    public long diary;
    public RealmList<Image> photo_list;
    public String created_date;
    public String content;
}
