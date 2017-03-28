package com.team3.fastcampus.record.Util.Permission;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * RuntimePermission을 위한 Controller
 *
 * AndroidManifast.xml에서 아래 코드를 추가한다.
 * <activity android:name=".Permission.PermissionControllerActivity" />
 */
public class PermissionController {

    private static PermissionController permissionController;

    private Context context;
    private PermissionCallback permissionCallback;
    private String[] permissionArray;

    public PermissionController(Context context, String[] permissionArray) {
        this.context = context;
        this.permissionArray = permissionArray;
    }

    /**
     * 권한 체크 및 권한 획득
     *
     * @param permissionCallback 권한 획득 성공 실패 후 처리
     */
    public void check(PermissionCallback permissionCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isChecked()) {
                permissionCallback.success();
            } else {
                this.permissionCallback = permissionCallback;
                permissionController = this; // 자기 자신을 static으로 접근 가능하도록 한다.
                context.startActivity(new Intent(context, PermissionControllerActivity.class));
            }
        } else {
            permissionCallback.success();
        }
    }

    /**
     * 권한이 있는지 없는지 확인한다.
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean isChecked() {
        boolean permCheck = true;
        for (String perm : permissionArray) {
            if (context.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                permCheck = false;
                break;
            }
        }
        return permCheck;
    }

    /**
     * onRequestPermissionsResult 의 grantResults를 보며 모두 허가를 받은 경우 true
     *
     * @param grantResults
     * @return
     */
    public boolean onCheckResult(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * PermissionControllerActivity와 연결해줌
     *
     * @return
     */
    protected static PermissionController getInstance() {
        if (permissionController == null) {
            throw new RuntimeException("PermissionController.getInstance is null. this can be use in system");
        }
        return permissionController;
    }

    public String[] getPermissionArray() {
        return permissionArray;
    }

    public PermissionCallback getPermissionCallback() {
        return permissionCallback;
    }

    public interface PermissionCallback {
        void success();

        void error();
    }
}
