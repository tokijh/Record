package com.team3.fastcampus.record.Diary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.team3.fastcampus.record.R;

public class DiaryWriteDetailActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_location;
    FloatingActionButton btn_add_photo, btn_add, btn_update, btn_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write_detail);



        et_location = (EditText) findViewById(R.id.diary_list_detail_et_diary_locataion);
        btn_add = (FloatingActionButton) findViewById(R.id.fab_add);
        btn_add_photo = (FloatingActionButton) findViewById(R.id.fab_photo);
        btn_update = (FloatingActionButton) findViewById(R.id.fab_update);
        btn_delete = (FloatingActionButton) findViewById(R.id.fab_delete);

        et_location.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_add_photo.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.diary_list_detail_et_diary_locataion:

                //DatePicker 부분을 Dialog로 띄워서 표현해야함

                break;

            case R.id.fab_add:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                break;

            case R.id.fab_photo:
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show();

                break;

            case R.id.fab_update:
                Toast.makeText(this, "update", Toast.LENGTH_SHORT).show();

                break;

            case R.id.fab_delete:
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();

                break;


        }

    }






}
