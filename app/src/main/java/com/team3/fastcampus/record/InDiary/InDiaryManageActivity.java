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
import android.support.v7.app.AlertDialog;
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
import com.team3.fastcampus.record.InDiary.Model.InDiary;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Logger;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.Permission.PermissionController;
import com.team3.fastcampus.record.Util.PreferenceManager;
import com.team3.fastcampus.record.Util.RealmDatabaseManager;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.realm.RealmList;

/**
 * InDiary 관리(추가 및 수정) Activity
 */
public class InDiaryManageActivity extends AppCompatActivity implements View.OnClickListener, InDiaryManageRecyclerAdapter.InDiaryManageListCallback {

    public static final String TAG = "InDiaryManageActivity";

    public static final int MODE_EDIT = 0;
    public static final int MODE_CREATE = 1;
    public static final int MODE_DELETE = 2;

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드

    private int MODE;
    private long PK;
    private long DIARY;
    private String DATE;

    private InDiary inDiary;

    private Uri fileUri = null;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private EditText ed_content;
    private TextView tv_date;
    private RecyclerView recyclerView;

    private FloatingActionMenu fab_edit;
    private FloatingActionButton fab_edit_delete, fab_edit_save, fab_edit_cancel;

    private InDiaryManageRecyclerAdapter inDiaryManageRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_diary_manage);

        initView();

        initListener();

        initAdapter();

        initIntentValue();

        modeSetting();
    }

    private void initIntentValue() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey("MODE")
                    && bundle.containsKey("PK")
                    && bundle.containsKey("DIARY")
                    && bundle.containsKey("DATE")) {
                MODE = bundle.getInt("MODE");
                PK = bundle.getLong("PK");
                DIARY = bundle.getLong("DIARY");
                DATE = bundle.getString("DATE");
            }
        }
    }

    private void initView() {
        ed_content = (EditText) findViewById(R.id.ed_content);
        tv_date = (TextView) findViewById(R.id.tv_date);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        fab_edit = (FloatingActionMenu) findViewById(R.id.fab_edit);
        fab_edit_delete = (FloatingActionButton) findViewById(R.id.fab_edit_delete);
        fab_edit_save = (FloatingActionButton) findViewById(R.id.fab_edit_save);
        fab_edit_cancel = (FloatingActionButton) findViewById(R.id.fab_edit_cancel);
    }

    private void initListener() {
        tv_date.setOnClickListener(this);
        fab_edit_delete.setOnClickListener(this);
        fab_edit_save.setOnClickListener(this);
        fab_edit_cancel.setOnClickListener(this);
    }

    private void initAdapter() {
        inDiaryManageRecyclerAdapter = new InDiaryManageRecyclerAdapter(this, this);
        recyclerView.setAdapter(inDiaryManageRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
    }

    private void modeSetting() {
        switch (MODE) {
            case MODE_CREATE:
                modeCreate();
                break;
            case MODE_EDIT:
                modeEdit();
                break;
            case MODE_DELETE:
                modeDelete();
                break;
            default:
                throw new RuntimeException("There is no match MODE");
        }
    }

    private void modeCreate() {
        fab_edit_delete.setVisibility(View.GONE);
        if (DIARY == -1l) {
            Toast.makeText(this, "데이터를 읽어 오는데 문제가 생겼습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
        setDate();
        updateDateText();
    }

    private void modeEdit() {
        getInDiary();

        ed_content.setText(inDiary.content);
        inDiaryManageRecyclerAdapter.set(inDiary.photo_list);
        setDate(inDiary.created_date);
        updateDateText();
    }

    private void modeDelete() {
        getInDiary();
        new AlertDialog.Builder(InDiaryManageActivity.this)
                .setMessage("정말로 삭제 하겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인",
                        (dialog, which) -> deleteInDiary())
                .setNegativeButton("취소",
                        (dialog, which) -> finish())
                .create()
                .show();
    }

    private void deleteInDiary() {
        // TODO 테스트용 코드
//        if (NetworkController.isNetworkStatusENABLE(NetworkController.checkNetworkStatus(this))) {
//            deleteInDiary_online();
//        } else {
            deleteInDiary_offline();
//        }
    }

    private void deleteInDiary_online() {
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_indiary) + DIARY + getString(R.string.server_indiary_end) + "/" + inDiary.pk)
                .setMethod(NetworkController.DELETE)
                .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Logger.e(TAG, error.getMessage());
                        Toast.makeText(InDiaryManageActivity.this, "내용 삭제에 실패 했습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        Logger.e(TAG, new String(responseData.body));
                        if (responseData.response.code() == 204) {
                            deleteDB();
                            Toast.makeText(InDiaryManageActivity.this, "내용 삭제에 성공 했습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(InDiaryManageActivity.this, "내용 삭제에 실패 했습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                })
                .execute();
    }

    private void deleteInDiary_offline() {
        RealmDatabaseManager
                .getInstance()
                .delete(InDiary.class,
                        realmResults ->
                                realmResults
                                        .equalTo("pk", inDiary.pk)
                                        .findAll()
                );
        Toast.makeText(InDiaryManageActivity.this, "내용 삭제에 성공 했습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void getInDiary() {
        inDiary = RealmDatabaseManager
                .getInstance()
                .get(InDiary.class)
                .equalTo("pk", PK)
                .findFirst();

        if (inDiary == null) {
            Toast.makeText(this, "데이터를 읽어 오는데 문제가 생겼습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setDate() {
        setDate(DATE);
    }

    private void setDate(String Date) {
        if (Date != null && !"".equals(Date)) {
            String splits[] = Date.split(" ");
            if (splits.length > 1) {
                // Date
                String splits_date[] = splits[0].split("-");
                if (splits_date.length > 2) {
                    mYear = Integer.parseInt(splits_date[0]);
                    mMonth = Integer.parseInt(splits_date[1]);
                    mDay = Integer.parseInt(splits_date[2]);
                }
                // Time
                String splits_time[] = splits[1].split(":");
                if (splits_time.length > 1) {
                    mHour = Integer.parseInt(splits_time[0]);
                    mMinute = Integer.parseInt(splits_time[1]);
                }
            }
        }
    }

    private void setCurrentDate() {
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

    private void saveDB() {
        InDiary inDiary = RealmDatabaseManager
                .getInstance()
                .get(InDiary.class)
                .equalTo("pk", PK)
                .findFirst();
        RealmDatabaseManager.getInstance().update(() -> {
            inDiary.content = ed_content.getText().toString();
            inDiary.created_date = tv_date.getText().toString();
        });
    }

    private void deleteDB() {
        RealmDatabaseManager.getInstance().delete(InDiary.class, realmResults -> realmResults.equalTo("pk", PK).findAll());
    }

    private void actionCreate() {
        // TODO 테스트용 코드
//        if (NetworkController.isNetworkStatusENABLE(NetworkController.checkNetworkStatus(this))) {
//            actionCreate_online();
//        } else {
            actionCreate_offline();
//        }
    }

    private void actionCreate_online() {
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_indiary) + DIARY + getString(R.string.server_indiary_end))
                .setMethod(NetworkController.POST)
                .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                .paramsAdd("diary", DIARY)
                .paramsAdd("created_date", tv_date.getText().toString())
                .paramsAdd("content", ed_content.getText().toString())
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Logger.e(TAG, error.getMessage());
                        Toast.makeText(InDiaryManageActivity.this, "내용 생성에 실패 했습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        Logger.e(TAG, new String(responseData.body));
                        if (responseData.response.code() == 201) {
                            Toast.makeText(InDiaryManageActivity.this, "내용 생성에 성공 했습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(InDiaryManageActivity.this, "내용 생성에 실패 했습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .execute();
    }

    private void actionCreate_offline() {
        RealmDatabaseManager
                .getInstance()
                .create(InDiary.class, (realm, inDiary) -> {
                    inDiary.pk = InDiary.getNxtPK();
                    inDiary.username = PreferenceManager.getInstance().getString("username", null);
                    inDiary.created_date = tv_date.getText().toString();
                    inDiary.diary = DIARY;
                    inDiary.content = ed_content.getText().toString();
                    inDiary.photo_list = new RealmList<>();
                    for (int i = 0; i < inDiaryManageRecyclerAdapter.getItemCount(); i++) {
                        Image image = inDiaryManageRecyclerAdapter.get(i);
                        if (image != null) {
                            Image saveImage = RealmDatabaseManager
                                    .getInstance()
                                    .create(Image.class);

                            saveImage.pk = Image.getNxtPK();
                            saveImage.photo = image.photo;
                            saveImage.gpsLatitude = image.gpsLatitude;
                            saveImage.gpsLongitude = image.gpsLongitude;
                            saveImage.post = inDiary.pk;
                            inDiary.photo_list.add(saveImage);
                        }
                    }

                    Toast.makeText(InDiaryManageActivity.this, "내용 생성에 성공 했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void actionUpdate() {
        // TODO 테스트용 코드
//        if (NetworkController.isNetworkStatusENABLE(NetworkController.checkNetworkStatus(this))) {
//            actionUpdate_online();
//        } else {
            actionUpdate_offline();
//        }
    }

    private void actionUpdate_online() {
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_indiary) + DIARY + getString(R.string.server_indiary_end) + "/" + inDiary.pk)
                .setMethod(NetworkController.PUT)
                .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                .paramsAdd("diary", DIARY)
                .paramsAdd("created_date", inDiary.created_date)
                .paramsAdd("content", ed_content.getText().toString())
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Logger.e(TAG, error.getMessage());
                        Toast.makeText(InDiaryManageActivity.this, "내용 수정에 실패 했습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        Logger.e(TAG, new String(responseData.body));
                        if (responseData.response.code() == 200) {
                            saveDB();
                            Toast.makeText(InDiaryManageActivity.this, "내용 수정에 성공 했습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(InDiaryManageActivity.this, "내용 수정에 실패 했습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .execute();
    }

    private void actionUpdate_offline() {
        InDiary inDiary = RealmDatabaseManager
                .getInstance()
                .get(InDiary.class)
                .equalTo("pk", this.inDiary.pk)
                .findFirst();

        RealmDatabaseManager
                .getInstance()
                .update(() -> {
                    inDiary.content = ed_content.getText().toString();
                    inDiary.created_date = this.inDiary.created_date;
                    inDiary.photo_list.clear();

                    for (int i = 0; i < inDiaryManageRecyclerAdapter.getItemCount(); i++) {
                        Image image = inDiaryManageRecyclerAdapter.get(i);
                        if (image != null) {
                            if (image.pk == -1l) {
                                Image saveImage = RealmDatabaseManager
                                        .getInstance()
                                        .create(Image.class);

                                saveImage.pk = Image.getNxtPK();
                                saveImage.photo = image.photo;
                                saveImage.gpsLatitude = image.gpsLatitude;
                                saveImage.gpsLongitude = image.gpsLongitude;
                                saveImage.post = inDiary.pk;
                                inDiary.photo_list.add(saveImage);
                            } else {
                                inDiary.photo_list.add(image);
                            }
                        }
                    }

                    Toast.makeText(InDiaryManageActivity.this, "내용 수정에 성공 했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void actionCancel() {
        finish();
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
            case R.id.fab_edit_delete:
                MODE = MODE_DELETE;
                modeSetting();
                break;
            case R.id.fab_edit_save:
                if (MODE == MODE_CREATE) {
                    actionCreate();
                } else if (MODE == MODE_EDIT) {
                    actionUpdate();
                }
                break;
            case R.id.fab_edit_cancel:
                actionCancel();
                break;
        }
        fab_edit.close(true);
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
