package com.team3.fastcampus.record.Diary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.team3.fastcampus.record.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DiaryWriteDetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_date;
    FloatingActionButton btn_add_photo, btn_add, btn_update, btn_delete;
    int mYear, mMonth, mDay, mHour, mMinute;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write_detail);

        tv_date = (TextView) findViewById(R.id.diary_list_detail_tv_diary_date);
        btn_add = (FloatingActionButton) findViewById(R.id.fab_add);
        btn_add_photo = (FloatingActionButton) findViewById(R.id.fab_photo);
        btn_update = (FloatingActionButton) findViewById(R.id.fab_update);
        btn_delete = (FloatingActionButton) findViewById(R.id.fab_delete);

        tv_date.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_add_photo.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        //날자, 시간 가져오기
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);

        UpdateCal();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;

                UpdateCal();
            }
        };

    }

    private void UpdateCal() {
        tv_date.setText(String.format("%d / %d / %d",mYear,mMonth +1, mDay));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.diary_list_detail_tv_diary_date:

                new DatePickerDialog(this , mDateSetListener, mYear, mMonth, mDay).show();

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


class PickerDialogSet extends Activity {
    int mYear, MMonth, mDay, mHour, mMinute;

}
