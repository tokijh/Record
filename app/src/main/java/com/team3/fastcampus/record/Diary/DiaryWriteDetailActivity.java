package com.team3.fastcampus.record.Diary;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.team3.fastcampus.record.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DiaryWriteDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQ_PERMISSION = 100; //권한요청코드
    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드
    Uri fileUri = null;

    EditText tv_date;
    FloatingActionButton btn_add_photo, btn_add, btn_update, btn_delete, btn_gallery;
    int mYear, mMonth, mDay, mHour, mMinute;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write_detail);

        checkPermission();

        init();
    }

    private void init() {
        tv_date = (EditText) findViewById(R.id.diary_list_detail_tv_diary_date);
        btn_add = (FloatingActionButton) findViewById(R.id.fab_add);
        btn_add_photo = (FloatingActionButton) findViewById(R.id.fab_photo);
        btn_update = (FloatingActionButton) findViewById(R.id.fab_update);
        btn_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        btn_gallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        tv_date.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_add_photo.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.diary_list_detail_tv_diary_date:

                new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay).show();
                break;

            case R.id.fab_add:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                break;

            case R.id.fab_photo:
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show();

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 누가는 아래 코드를 반영해야 한다.
                // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
//                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                    }
                // --- 여기 까지 컨텐트 uri 강제세팅 ---

                startActivityForResult(intent, REQ_CAMERA);

                break;

            case R.id.fab_update:
                Toast.makeText(this, "update", Toast.LENGTH_SHORT).show();

                break;

            case R.id.fab_delete:
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();

                break;

            case R.id.fab_gallery:
                Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();

                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*"); // 외부저장소에있는 이미지만 가져오기위한 필터링
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CAMERA);
                break;


        }

    }

    private void UpdateCal() {
        tv_date.setText(String.format("%d/%d/%d", mYear, mMonth + 1, mDay));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION) {
            //배열에 넘긴 런타임 권한을 체크해서 승인이 되었으면
            if (PermissionControl.onCheckResult(grantResults)) {


            }
        } else {
            Toast.makeText(this, "권한을 허용하지 않으면 프로그램을 실행할 수 없습니다", Toast.LENGTH_SHORT).show();

            finish();

        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionControl.checkPermission(this, REQ_PERMISSION)) {
                //프로그램 실행
            }
        } else {
            //프로그램 실행
        }
    }
}




