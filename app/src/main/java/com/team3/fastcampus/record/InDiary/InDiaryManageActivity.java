package com.team3.fastcampus.record.InDiary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.team3.fastcampus.record.InDiary.Adapter.InDiaryManageRecyclerAdapter;
import com.team3.fastcampus.record.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * InDiary 관리(추가 및 수정) Activity
 */
public class InDiaryManageActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드
    Uri fileUri = null;

    int mYear, mMonth, mDay, mHour, mMinute;
    FloatingActionButton btn_add, btn_update, btn_delete;

    EditText et_indiary_title;
    TextView tv_date, tv_location;
    RecyclerView rv;
    ImageView imageView;
    InDiaryManageRecyclerAdapter ra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_diary_manage);


        init();
        initListener();
        initDateValue();

        ra = new InDiaryManageRecyclerAdapter(this,R.layout.activity_in_diary_manage_item);
        rv.setAdapter(ra);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));


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
        imageView = (ImageView) findViewById(R.id.in_diary_manage_item_image);
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

    private void returnCamera(Intent intent) {
        // 롤리팝 체크
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fileUri = intent.getData();
        }
        if (fileUri != null) {
            saveProfile(fileUri);
        } else {
            Toast.makeText(this, "사진파일을 못받아 왔습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void returnGallery(Intent intent) {
        fileUri = intent.getData();
        saveProfile(fileUri);
    }

    private void saveProfile(Uri uri) {

        ra.uriAdd(uri);

//        Glide.with(this)
//                .load(uri)
//                .placeholder(R.drawable.no_photo)
//                .into(imageView);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CAMERA:

                    returnCamera(data);

                    break;
                case REQ_GALLERY:

                    returnGallery(data);

                    break;
            }
        }
    }

}
