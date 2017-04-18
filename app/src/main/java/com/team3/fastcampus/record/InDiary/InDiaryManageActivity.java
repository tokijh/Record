package com.team3.fastcampus.record.InDiary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.team3.fastcampus.record.InDiary.Model.InDiary;
import com.team3.fastcampus.record.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * InDiary 관리(추가 및 수정) Activity
 */
public class InDiaryManageActivity extends AppCompatActivity implements View.OnClickListener {

    int mYear, mMonth, mDay, mHour, mMinute;
    FloatingActionButton btn_add, btn_update, btn_delete;

    ArrayList<InDiary> datas;
    EditText et_indiary_title;
    TextView tv_date, tv_location;
    RecyclerView rv;
    InDiaryRecyclerAdapter ra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_diary_manage);


        init();
        initListener();
        initDateValue();

        ra = new InDiaryRecyclerAdapter(datas, R.layout.activity_in_diary_manage_item, this);
        rv.setAdapter(ra);

        rv.setLayoutManager(new LinearLayoutManager(this));


    }

    private void initListener() {
        tv_date.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }

    private void init() {
        et_indiary_title = (EditText) findViewById(R.id.indiary_list_detail_et_diary_title);
        tv_date = (TextView) findViewById(R.id.indiary_list_detail_tv_diary_date);
        tv_location = (TextView) findViewById(R.id.indiary_list_detail_tv_diary_locataion);
        btn_add = (FloatingActionButton) findViewById(R.id.in_diary_fab_add);
        btn_update = (FloatingActionButton) findViewById(R.id.in_diary_fab_update);
        btn_delete = (FloatingActionButton) findViewById(R.id.in_diary_fab_delete);
        rv = (RecyclerView) findViewById(R.id.in_diary_recyclerview);
    }

    private void initDateValue() {
        // 날자, 시간 가져오기
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);

        // date TextView 업데이트
        updateDateText();
    }

    private void updateDateText() {
        tv_date.setText(String.format("%d/%d/%d", mYear, mMonth + 1, mDay));
    }

    private void showDatePicker() {
        new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay).show();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, month, dayOfMonth) -> {
        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;
        updateDateText();
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.indiary_list_detail_tv_diary_date:
                showDatePicker();
                break;

            case R.id.indiary_list_detail_tv_diary_locataion:
                break;
        }
    }
}
