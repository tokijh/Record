package com.team3.fastcampus.record.InDiary;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.team3.fastcampus.record.R;

public class InDiaryDetailActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_diary_detail);

        viewPager = (ViewPager) findViewById(R.id.viewPager);


    }
}
