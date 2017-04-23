package com.team3.fastcampus.record.InDiary;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.team3.fastcampus.record.InDiary.Adapter.InDiaryDetailAdapter;
import com.team3.fastcampus.record.R;

public class InDiaryDetailActivity extends AppCompatActivity {

    ViewPager viewPager;
    InDiaryDetailAdapter adapter;
    TextView tv_content;
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_diary_detail);

        layout = (RelativeLayout) findViewById(R.id.in_diary_detail_content_area);
        tv_content = (TextView) findViewById(R.id.in_diary_detail_content_textview);
        viewPager = (ViewPager) findViewById(R.id.in_diary_list_view_viewPager);
        adapter = new InDiaryDetailAdapter(InDiaryDetailActivity.this);
        viewPager.setAdapter(adapter);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InDiaryDetailActivity.this, "Touch Layout", Toast.LENGTH_SHORT).show();

            }
        });


        tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InDiaryDetailActivity.this, "Touch TextView", Toast.LENGTH_SHORT).show();

            }
        });

    }




}
