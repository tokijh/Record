package com.team3.fastcampus.record.Account;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.team3.fastcampus.record.R;

/**
 * 회원 정보 관리 Activity
 */
public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_logout;
    private ImageView iv_profile;
    private TextView tv_username, tv_nickname;
    private EditText ed_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initView();

        initListener();
    }

    private void initView() {
        btn_logout = (Button) findViewById(R.id.btn_logout);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        ed_password = (EditText) findViewById(R.id.ed_password);
    }

    private void initListener() {
        btn_logout.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        ed_password.setOnClickListener(this);
    }

    private void logOut() {

    }

    private void settingProfile() {

    }

    private void changeProfile() {

    }

    private void settingPassword() {

    }

    private void changePassword() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                logOut();
                break;
            case R.id.iv_profile:
                settingProfile();
                break;
            case R.id.ed_password:
                settingPassword();
                break;
        }
    }
}
