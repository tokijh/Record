package com.team3.fastcampus.record.Diary;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by kimkyuwan on 2017. 2. 10..
 */

public class PermissionControl {

    public static final String PERMISSIONARRAY[] = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermission(Activity activity, int req_permission) {

        boolean permCheck = true;
        for (String perm : PERMISSIONARRAY) {

            if (activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                permCheck = false;
                break;
            }
        }

        if (permCheck) {
            //loadData();
            return true;
        } else {
            activity.requestPermissions(PERMISSIONARRAY, req_permission);
            return false;
        }
    }





    public static boolean onCheckResult(int[] grantResults) {

        boolean checkResult = true;

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                checkResult = false;
                break;
            }
        }
        return checkResult;
    }


}
