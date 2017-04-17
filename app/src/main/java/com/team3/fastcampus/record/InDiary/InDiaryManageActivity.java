package com.team3.fastcampus.record.InDiary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.team3.fastcampus.record.R;

/**
 * InDiary 관리(추가 및 수정) Activity
 */
public class InDiaryManageActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_diary_manage_item);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_add:

                break;
            case R.id.fab_update:

                break;
            case R.id.fab_delete:

                break;
        }
    }
}
