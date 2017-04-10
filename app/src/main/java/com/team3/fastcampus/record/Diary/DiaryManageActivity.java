package com.team3.fastcampus.record.Diary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Permission.PermissionControllerActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Diary 관리(추가 및 수정) Activity
 */
public class DiaryManageActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQ_PERMISSION = 100; //권한요청코드
    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드
    Uri fileUri = null;

    EditText et_date, et_end_date;
    FloatingActionButton btn_add_photo, btn_add, btn_update, btn_delete, btn_gallery;
    int mYear, mMonth, mDay, mHour, mMinute;
    int mEndYear, mEndMonth, mEndDay;
    ImageView imageView;


    DatePickerDialog.OnDateSetListener mDateSetListener, mEndDateSetListener;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_manage);

        checkPermission();

        et_date = (EditText) findViewById(R.id.diary_list_detail_tv_diary_date);
        et_end_date = (EditText) findViewById(R.id.diary_list_detail_tv_diary_end_date);
        btn_add = (FloatingActionButton) findViewById(R.id.fab_add);
        btn_add_photo = (FloatingActionButton) findViewById(R.id.fab_photo);
        btn_update = (FloatingActionButton) findViewById(R.id.fab_update);
        btn_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        btn_gallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        imageView = (ImageView) findViewById(R.id.diary_list_detail_image);
        et_date.setOnClickListener(this);
        et_end_date.setOnClickListener(this);
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

        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEndYear = year;
                mEndMonth = month;
                mEndDay = dayOfMonth;

                EndDateCal();
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

            case R.id.diary_list_detail_tv_diary_end_date:

                new DatePickerDialog(this, mEndDateSetListener, mYear, mMonth, mDay).show();
                break;

            case R.id.fab_add:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                break;

            case R.id.fab_photo:
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show();

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

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
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY);
                break;


        }

    }

    private void UpdateCal() {
        et_date.setText(String.format("%d/%d/%d", mYear, mMonth + 1, mDay));

    }
    private void EndDateCal(){
        et_end_date.setText(String.format("%d/%d/%d", mEndYear, mEndMonth + 1, mEndDay));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION) {
            //배열에 넘긴 런타임 권한을 체크해서 승인이 되었으면
            if (PermissionControllerActivity.onCheckResult(grantResults)) {


            }
        } else {
            Toast.makeText(this, "권한을 허용하지 않으면 프로그램을 실행할 수 없습니다", Toast.LENGTH_SHORT).show();

            finish();

        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionControllerActivity.checkPermission(this, REQ_PERMISSION)) {
                //프로그램 실행
            }
        } else {
            //프로그램 실행
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_GALLERY:
                if(data.getData() != null) {
                    fileUri = data.getData();
                    Glide.with(this).load(fileUri)
                            .into(imageView);

                }
                break;



        }
        if (requestCode == REQ_CAMERA) {
            // 누가 일경우만 getData()에 null 이 넘어올것이다
//            if(data.getData() != null){
//                fileUri = data.getData();
//            }
            imageView.setImageURI(fileUri);
        }
    }
}



