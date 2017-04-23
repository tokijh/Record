package com.team3.fastcampus.record.InDiary.Model;

import com.team3.fastcampus.record.Util.PreferenceManager;

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
    public String location;

    public static long getNxtPK() {
        long pk = PreferenceManager.getInstance().getLong("ImagePK", 0);
        PreferenceManager.getInstance().putLong("ImagePK", pk + 1);
        return pk;
    }
}
