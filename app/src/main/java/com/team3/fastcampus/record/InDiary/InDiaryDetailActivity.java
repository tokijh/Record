package com.team3.fastcampus.record.InDiary;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.team3.fastcampus.record.InDiary.Adapter.InDiaryDetailAdapter;
import com.team3.fastcampus.record.InDiary.Model.Image;
import com.team3.fastcampus.record.R;

import java.util.ArrayList;

public class InDiaryDetailActivity extends AppCompatActivity {

    ViewPager viewPager;
    InDiaryDetailAdapter adapter;
    ArrayList<Image> datas;

    TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_diary_detail);


        tv_content = (TextView) findViewById(R.id.in_diary_detail_content_textview);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new InDiaryDetailAdapter(InDiaryDetailActivity.this);
        //viewPager.setAdapter(adapter);

        tv_content.setText("Test");
    }



}
