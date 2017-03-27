package com.team3.fastcampus.record.Util;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * SharedPreferences의 Manager
 */
public class PreferenceManager {

    private static final String NAME = "com.team3.fastcampus.record";

    private SharedPreferences pre;
    private SharedPreferences.Editor editor;

    private static PreferenceManager preferenceManager;

    private PreferenceManager(Context context) {
        pre = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = pre.edit();
    }

    /**
     * PreferenceManager를 사용하기 전 필수 메소드
     * @param context
     */
    public static void create(Context context) {
        preferenceManager = new PreferenceManager(context);
    }

    public static PreferenceManager getInstance() {
        if (preferenceManager == null) {
            throw new RuntimeException("PreferenceManager must to create first");
        }
        return preferenceManager;
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value).commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public void putStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value).commit();
    }

    public int getInt(String key, int value) {
        return pre.getInt(key, value);
    }

    public String getString(String key, String value) {
        return pre.getString(key, value);
    }

    public float getFloat(String key, float value) {
        return pre.getFloat(key, value);
    }

    public long getLong(String key, long value) {
        return pre.getLong(key, value);
    }

    public boolean getBoolean(String key, boolean value) {
        return pre.getBoolean(key, value);
    }

    public Set<String> getStringSet(String key, Set<String> value) {
        return pre.getStringSet(key, value);
    }
}
