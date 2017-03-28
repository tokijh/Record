package com.team3.fastcampus.record.Util;

/**
 * Created by tokijh on 2017. 3. 26..
 */

import android.util.Log;

import com.team3.fastcampus.record.BuildConfig;

/**
 * 로그를 찍기 위한 Logger
 */
public class Logger {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * Loging Debug Message
     *
     * @param TAG
     * @param content
     */
    public static void d(String TAG, Object content) {
        if (DEBUG) Log.d(TAG, content.toString() + "");
    }

    /**
     * Loging Error Message
     *
     * @param TAG
     * @param content
     */
    public static void e(String TAG, Object content) {
        if (DEBUG) Log.e(TAG, content.toString() + "");
    }

    /**
     * Logging Info Message
     *
     * @param TAG
     * @param content
     */
    public static void i(String TAG, Object content) {
        if (DEBUG) Log.i(TAG, content.toString() + "");
    }
}
