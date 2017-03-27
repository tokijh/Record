package com.team3.fastcampus.record.Util.Permission;

/**
 * Created by tokijh on 2017. 3. 28..
 */

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PermissionController.getInstance().getPermissionArray(), REQ_PERMISSION);
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
}
