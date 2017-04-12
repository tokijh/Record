package com.team3.fastcampus.record.Diary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Permission.PermissionController;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DiaryManageActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드

    Uri fileUri = null;

    TextView tv_date, tv_endDate;
    FloatingActionButton btn_add, btn_update, btn_delete;
    int mYear, mMonth, mDay, mHour, mMinute;
    int mEndYear, mEndMonth, mEndDay;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_manage);

        new PermissionController(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA})
                .check(new PermissionController.PermissionCallback() {
                    @Override
                    public void success() {
                        init();
                    }

                    @Override
                    public void error() {
                        Toast.makeText(DiaryManageActivity.this, "권한을 허용하지 않으면 프로그램을 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void init() {
        initView();

        initListener();

        initDateValue();
    }

    private void initView() {
        tv_date = (TextView) findViewById(R.id.diary_list_detail_tv_diary_date);
        tv_endDate = (TextView) findViewById(R.id.diary_list_detail_tv_diary_end_date);
        btn_add = (FloatingActionButton) findViewById(R.id.fab_add);
        btn_update = (FloatingActionButton) findViewById(R.id.fab_update);
        btn_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        imageView = (ImageView) findViewById(R.id.diary_list_detail_image);
    }

    private void initListener() {
        tv_date.setOnClickListener(this);
        tv_endDate.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        imageView.setOnClickListener(this);
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

    private void upEndDateDateText() {
        tv_endDate.setText(String.format("%d/%d/%d", mEndYear, mEndMonth + 1, mEndDay));
    }

    private void showDatePicker() {
        new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay).show();
    }

    private void showEndDatePicker() {
        new DatePickerDialog(this, mEndDateSetListener, mYear, mMonth, mDay).show();
    }

    private void actionAdd() {
        Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
    }

    private void actionPhoto() {
        Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        startActivityForResult(intent, REQ_CAMERA);
    }

    private void actionUpdate() {
        Toast.makeText(this, "update", Toast.LENGTH_SHORT).show();
    }

    private void actionDelete() {
        Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
    }

    private void actionGallery() {
        Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 외부저장소에있는 이미지만 가져오기위한 필터링
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY);
    }

    private void actionCameraSelect() {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(DiaryManageActivity.this);
        alert_confirm.setMessage("Select Camera").setCancelable(false).setPositiveButton("camera",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actionPhoto();
                    }
                }).setNegativeButton("gallery",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actionGallery();
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.diary_list_detail_tv_diary_date:
                showDatePicker();
                break;
            case R.id.diary_list_detail_tv_diary_end_date:
                showEndDatePicker();
                break;
            case R.id.fab_add:
                actionAdd();
                break;
            case R.id.fab_update:
                actionUpdate();
                break;
            case R.id.fab_delete:
                actionDelete();
                break;
            case R.id.diary_list_detail_image:
                actionCameraSelect();
                break;

        }
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, month, dayOfMonth) -> {
        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;
        updateDateText();
    };

    private DatePickerDialog.OnDateSetListener mEndDateSetListener = (view, year, month, dayOfMonth) -> {
        mEndYear = year;
        mEndMonth = month;
        mEndDay = dayOfMonth;
        upEndDateDateText();
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CAMERA:
                    imageView.setImageURI(fileUri);
                    break;
                case REQ_GALLERY:
                    if (data.getData() != null) {
                        fileUri = data.getData();
                        Glide.with(this).load(fileUri)
                                .into(imageView);

                    }
                    break;
            }
        }
    }
}
