package com.team3.fastcampus.record.InDiary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.team3.fastcampus.record.InDiary.Adapter.InDiaryManageRecyclerAdapter;
import com.team3.fastcampus.record.InDiary.Model.Image;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Permission.PermissionController;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * InDiary 관리(추가 및 수정) Activity
 */
public class InDiaryManageActivity extends AppCompatActivity implements View.OnClickListener, InDiaryManageRecyclerAdapter.InDiaryManageListCallback {

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드

    Uri fileUri = null;

    int mYear, mMonth, mDay, mHour, mMinute;

    private EditText ed_content;
    private TextView tv_date;
    private RecyclerView recyclerView;

    private FloatingActionMenu fab_view;
    private FloatingActionButton fab_view_delete, fab_view_update;

    private FloatingActionMenu fab_edit;
    private FloatingActionButton fab_edit_save, fab_edit_cancel;

    InDiaryManageRecyclerAdapter inDiaryManageRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_diary_manage);

        initView();

        initListener();

        initDateValue();

        initAdapter();
    }

    private void initListener() {
        tv_date.setOnClickListener(this);
        fab_view_delete.setOnClickListener(this);
        fab_view_update.setOnClickListener(this);
        fab_edit_save.setOnClickListener(this);
        fab_edit_cancel.setOnClickListener(this);
    }

    private void initView() {
        ed_content = (EditText) findViewById(R.id.ed_content);
        tv_date = (TextView) findViewById(R.id.tv_date);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        fab_view = (FloatingActionMenu) findViewById(R.id.fab_view);
        fab_edit = (FloatingActionMenu) findViewById(R.id.fab_edit);
        fab_view_delete = (FloatingActionButton) findViewById(R.id.fab_view_delete);
        fab_view_update = (FloatingActionButton) findViewById(R.id.fab_view_update);
        fab_edit_save = (FloatingActionButton) findViewById(R.id.fab_edit_save);
        fab_edit_cancel = (FloatingActionButton) findViewById(R.id.fab_edit_cancel);
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

    private void initAdapter() {
        inDiaryManageRecyclerAdapter = new InDiaryManageRecyclerAdapter(this, this);
        recyclerView.setAdapter(inDiaryManageRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
    }

    private void updateDateText() {
        tv_date.setText(String.format("%04d-%02d-%02d %02d:%02d", mYear, mMonth + 1, mDay, mHour, mMinute));
    }

    private void showDatePicker() {
        new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false).show();
    }

    private void actionPhotoSelect() {
        new PermissionController(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA})
                .check(new PermissionController.PermissionCallback() {
                    @Override
                    public void success() {
                        new android.support.v7.app.AlertDialog.Builder(InDiaryManageActivity.this)
                                .setMessage("Select Action")
                                .setCancelable(true)
                                .setPositiveButton("Camera",
                                        (dialog, which) -> {
                                            actionCamera();
                                            dialog.dismiss();
                                        })
                                .setNegativeButton("Gallery",
                                        (dialog, which) -> {
                                            actionGallery();
                                            dialog.dismiss();
                                        })
                                .create()
                                .show();
                    }

                    @Override
                    public void error() {
                        Toast.makeText(InDiaryManageActivity.this, "권한을 허용 하지 않으면 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actionCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        startActivityForResult(intent, REQ_CAMERA);
    }

    private void returnCamera(Intent intent) {
        // 롤리팝 체크
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fileUri = intent.getData();
        }
        if (fileUri != null) {
            addImage(fileUri);
        } else {
            Toast.makeText(this, "사진파일을 못받아 왔습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void actionGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 외부저장소에있는 이미지만 가져오기위한 필터링
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY);
    }

    private void returnGallery(Intent intent) {
        fileUri = intent.getData();
        addImage(fileUri);
    }

    private void addImage(Uri uri) {
        Image image = new Image();
        image.photo = uri.toString();
        inDiaryManageRecyclerAdapter.add(image);
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, month, dayOfMonth) -> {
        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;
        showTimePicker();
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = (timePicker, hourOfDay, minute) -> {
        mHour = hourOfDay;
        mMinute = minute;
        updateDateText();
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date:
                showDatePicker();
                break;
            case R.id.fab_view_delete:
                break;
            case R.id.fab_view_update:
                break;
            case R.id.fab_edit_save:
                break;
            case R.id.fab_edit_cancel:
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

    @Override
    public void onItemClick(int position) {
        new android.support.v7.app.AlertDialog.Builder(InDiaryManageActivity.this)
                .setMessage("이 사진을 삭제 하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인",
                        (dialog, which) -> {
                            inDiaryManageRecyclerAdapter.removeItem(position);
                            dialog.dismiss();
                        })
                .setNegativeButton("취소",
                        (dialog, which) -> {
                            dialog.dismiss();
                        })
                .create()
                .show();
    }

    @Override
    public void onAddClick() {
        actionPhotoSelect();
    }
}
