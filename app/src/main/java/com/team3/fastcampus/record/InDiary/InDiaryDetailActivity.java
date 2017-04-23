package com.team3.fastcampus.record.InDiary;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team3.fastcampus.record.InDiary.Adapter.InDiaryDetailAdapter;
import com.team3.fastcampus.record.InDiary.Model.Image;
import com.team3.fastcampus.record.R;

import java.util.ArrayList;

public class InDiaryDetailActivity extends AppCompatActivity {

    ViewPager viewPager;
    InDiaryDetailAdapter adapter;
    ArrayList<Image> datas;
    RelativeLayout relativeLayout;
    TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_diary_detail);


        relativeLayout = (RelativeLayout) findViewById(R.id.in_diary_detail_content_area);
        tv_content = (TextView) findViewById(R.id.in_diary_detail_content_textview);
        viewPager = (ViewPager) findViewById(R.id.in_diary_list_view_viewPager);
        adapter = new InDiaryDetailAdapter(InDiaryDetailActivity.this);

       // tv_content.setVisibility(View.INVISIBLE);
       // relativeLayout.setVisibility(View.INVISIBLE);


        viewPager.setAdapter(adapter);
        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
                tv_content.setVisibility(View.VISIBLE);
                tv_content.setText("Test");
            }
        });





    }



}
