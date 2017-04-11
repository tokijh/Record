package com.team3.fastcampus.record.Util.Permission;

/**
 * Created by tokijh on 2017. 3. 28..
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * requestPermissions 을 하고 onRequestPermissionsResult를 받기 위한 Activity
 */
public class PermissionControllerActivity extends AppCompatActivity {

    private final int REQ_PERMISSION = 100;

    @Override
    protected void onStart() {
        super.onStart();

        PermissionController permissionController = PermissionController.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionController != null) {
            requestPermissions(PermissionController.getInstance().getPermissionArray(), REQ_PERMISSION);
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            if (PermissionController.getInstance().onCheckResult(grantResults)) {
                PermissionController.getInstance().getPermissionCallback().success();
            } else {
                PermissionController.getInstance().getPermissionCallback().error();
            }
        }
        finish();
    }

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
