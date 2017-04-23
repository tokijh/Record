package com.team3.fastcampus.record.InDiary;

import android.content.Intent;
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
    Intent intent;
    String flag = "INVISIBLE";
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

        tv_content.setText("TEST");

        // layout.setVisibility(View.INVISIBLE);
        intent = getIntent();
        if (flag != "INVISIBLE") {
            flag = intent.getExtras().getString("flag");
        }


        switch (flag) {
            case "INVISIBLE":
                tv_content.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "INVISIBLE 실행", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.INVISIBLE);
                flag = "VISIBLE";
                break;

            case "VISIBLE":
                tv_content.setVisibility(View.VISIBLE);
                Toast.makeText(this, "VISIBLE 실행", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.VISIBLE);
                flag = "INVISIBLE";

                break;

        }

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

        switch (requestCode) {

            case View.VISIBLE:
                layout.setVisibility(View.VISIBLE);
                break;

            case View.INVISIBLE:
                layout.setVisibility(View.INVISIBLE);
                break;

        }
    }
}
