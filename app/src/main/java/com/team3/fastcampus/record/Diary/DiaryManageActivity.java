package com.team3.fastcampus.record.Diary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.team3.fastcampus.record.Diary.Model.Diary;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Logger;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.Permission.PermissionController;
import com.team3.fastcampus.record.Util.PreferenceManager;
import com.team3.fastcampus.record.Util.RealmDatabaseManager;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DiaryManageActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "DiaryManageActivity";

    public static final int MODE_VIEW = 0;
    public static final int MODE_CREATE = 1;
    public static final int MODE_EDIT = 2;
    public static final int MODE_DELETE = 3;

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드

    private int MODE = MODE_VIEW;
    private long PK = -1;
    private Diary diary;

    Uri fileUri = null;

    TextView tv_date, tv_endDate;
    EditText ed_title;
    FloatingActionMenu fab_view;
    FloatingActionButton fab_view_btn_update, fab_view_btn_delete;
    FloatingActionMenu fab_edit;
    FloatingActionButton fab_edit_btn_save, fab_edit_btn_cancel;
    int mYear, mMonth, mDay, mHour, mMinute;
    int mEndYear, mEndMonth, mEndDay;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_manage);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("MODE") && bundle.containsKey("PK")) {
            MODE = bundle.getInt("MODE");
            PK = bundle.getLong("PK");
        }

        init();

        initByMode();
    }

    private void init() {
        initView();

        initListener();

        initDateValue();
    }

    private void initView() {
        tv_date = (TextView) findViewById(R.id.diary_list_detail_tv_diary_date);
        tv_endDate = (TextView) findViewById(R.id.diary_list_detail_tv_diary_end_date);
        ed_title = (EditText) findViewById(R.id.diary_list_detail_et_diary_title);
        fab_view = (FloatingActionMenu) findViewById(R.id.fab_view);
        fab_view_btn_update = (FloatingActionButton) findViewById(R.id.fab_view_update);
        fab_view_btn_delete = (FloatingActionButton) findViewById(R.id.fab_view_delete);
        fab_edit = (FloatingActionMenu) findViewById(R.id.fab_edit);
        fab_edit_btn_save = (FloatingActionButton) findViewById(R.id.fab_edit_save);
        fab_edit_btn_cancel = (FloatingActionButton) findViewById(R.id.fab_edit_cancel);
        imageView = (ImageView) findViewById(R.id.diary_list_detail_image);
    }

    private void initListener() {
        tv_date.setOnClickListener(this);
        tv_endDate.setOnClickListener(this);
        fab_view_btn_update.setOnClickListener(this);
        fab_view_btn_delete.setOnClickListener(this);
        fab_edit_btn_save.setOnClickListener(this);
        fab_edit_btn_cancel.setOnClickListener(this);
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

    private void initByMode() {
        switch (MODE) {
            case MODE_VIEW:
                modeView();
                break;
            case MODE_CREATE:
                modeCreate();
                break;
            case MODE_EDIT:
                modeEdit();
                break;
            case MODE_DELETE:
                modeDelete();
                break;
        }
    }

    private void modeView() {
        fab_view.setVisibility(View.VISIBLE);
        fab_edit.setVisibility(View.GONE);
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        diary = realmDatabaseManager.get(Diary.class)
                .equalTo("pk", PK)
                .findFirst();

        // TODO 시작, 종료날짜, Location Text 표시
        tv_date.setText(diary.start_date);
        tv_endDate.setText(diary.end_date);
        ed_title.setText(diary.title);
        ed_title.setEnabled(false);
    }

    private void modeCreate() {
        fab_view.setVisibility(View.GONE);
        fab_edit.setVisibility(View.VISIBLE);
    }

    private void modeEdit() {
        fab_view.setVisibility(View.GONE);
        fab_edit.setVisibility(View.VISIBLE);
        modeView();
        ed_title.setEnabled(true);
    }

    private void modeDelete() {
        new AlertDialog.Builder(DiaryManageActivity.this)
                .setMessage("정말로 삭제 하겠습니까?")
                .setPositiveButton("확인",
                        (dialog, which) -> deleteDiary())
                .setNegativeButton("취소",
                        (dialog, which) -> finish())
                .create()
                .show();
    }

    private void deleteDiary() {
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        realmDatabaseManager.delete(Diary.class, realmResults -> realmResults.equalTo("pk", PK).findAll());
    }

    private void createDiary() {
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_diary))
                .setMethod(NetworkController.POST)
                .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                .paramsAdd("title", ed_title.getText().toString())
                .paramsAdd("start_date", tv_date.getText().toString())
                .paramsAdd("end_date", tv_endDate.getText().toString())
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Logger.e(TAG, error.getMessage());
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        Logger.e(TAG, new String(responseData.body));
                        Diary diary = NetworkController.decode(Diary.class, new String(responseData.body));
                        RealmDatabaseManager.getInstance().create(Diary.class, (realm, realmObject) -> {
                            realmObject.pk = diary.pk;
                            realmObject.cover_image = diary.cover_image;
                            realmObject.post_count = diary.post_count;
                            realmObject.title = diary.title;
                            realmObject.start_date = diary.start_date;
                            realmObject.end_date = diary.end_date;
                        });
                    }
                })
                .execute();
    }

    public void cancelCreateDiary() {
        finish();
    }

    private void updateDateText() {
        // TODO diary에 시작날자 저장 mYear, mMonth, mDay...
        tv_date.setText(String.format("%04d-%02d-%02d", mYear, mMonth + 1, mDay));
    }

    private void upEndDateDateText() {
        // TODO diary에 종료날자 저장 mEndYear, mEndMonth, mEndDay...
        tv_endDate.setText(String.format("%04d-%02d-%02d", mEndYear, mEndMonth + 1, mEndDay));
    }

    private void showDatePicker() {
        new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay).show();
    }

    private void showEndDatePicker() {
        new DatePickerDialog(this, mEndDateSetListener, mYear, mMonth, mDay).show();
    }

    private void actionPhoto() {

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

    private void actionGallery() {
        Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 외부저장소에있는 이미지만 가져오기위한 필터링
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY);
    }

    private void actionPhotoSelect() {
        new PermissionController(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA})
                .check(new PermissionController.PermissionCallback() {
                    @Override
                    public void success() {
                        new AlertDialog.Builder(DiaryManageActivity.this)
                                .setMessage("Select Camera")
                                .setPositiveButton("camera",
                                        (dialog, which) -> actionPhoto())
                                .setNegativeButton("gallery",
                                        (dialog, which) -> actionGallery())
                                .create()
                                .show();
                    }

                    @Override
                    public void error() {
                        Toast.makeText(DiaryManageActivity.this, "권한을 허용하지 않으면 이 기능을 이용 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
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
            case R.id.diary_list_detail_image:
                actionPhotoSelect();
                break;
            case R.id.fab_edit_save:
                createDiary();
                break;
            case R.id.fab_edit_cancel:
                cancelCreateDiary();
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
