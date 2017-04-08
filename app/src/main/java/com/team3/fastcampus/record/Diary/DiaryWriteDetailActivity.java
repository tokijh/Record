package com.team3.fastcampus.record.Diary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Permission.PermissionController;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DiaryWriteDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드

    Uri fileUri = null;

    EditText tv_date;
    FloatingActionButton btn_add_photo, btn_add, btn_update, btn_delete, btn_gallery;
    int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write_detail);

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
                        Toast.makeText(DiaryWriteDetailActivity.this, "권한을 허용하지 않으면 프로그램을 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
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
        tv_date = (EditText) findViewById(R.id.diary_list_detail_tv_diary_date);
        btn_add = (FloatingActionButton) findViewById(R.id.fab_add);
        btn_add_photo = (FloatingActionButton) findViewById(R.id.fab_photo);
        btn_update = (FloatingActionButton) findViewById(R.id.fab_update);
        btn_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        btn_gallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
    }

    private void initListener() {
        tv_date.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_add_photo.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.diary_list_detail_tv_diary_date:
                showDatePicker();
                break;
            case R.id.fab_add:
                actionAdd();
                break;
            case R.id.fab_photo:
                actionPhoto();
                break;
            case R.id.fab_update:
                actionUpdate();
                break;
            case R.id.fab_delete:
                actionDelete();
                break;
            case R.id.fab_gallery:
                actionGallery();
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, month, dayOfMonth) -> {
        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;
        updateDateText();
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CAMERA:
                    break;
                case REQ_GALLERY:
                    break;
            }
        }
    }
}
