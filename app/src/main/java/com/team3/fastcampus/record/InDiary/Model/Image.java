package com.team3.fastcampus.record.InDiary.Model;

import io.realm.RealmObject;

/**
 * Created by tokijh on 2017. 4. 4..
 */

public class Image extends RealmObject {
    public long pk;
    public long post;
    public String photo;
    public String gpsLatitude;
    public String gpsLongitude;
}
