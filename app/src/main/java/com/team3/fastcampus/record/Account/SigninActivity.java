package com.team3.fastcampus.record.Account;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.team3.fastcampus.record.R;

/**
 * 회원 로그인 Activity
 */
public class SigninActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);



    }

    public void btnSignup(View view){
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);
    }
}
