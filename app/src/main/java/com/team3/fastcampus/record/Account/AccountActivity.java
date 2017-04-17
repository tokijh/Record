package com.team3.fastcampus.record.Account;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.team3.fastcampus.record.Model.User;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Logger;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.Permission.PermissionController;
import com.team3.fastcampus.record.Util.PreferenceManager;
import com.team3.fastcampus.record.Util.TextPatternChecker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 회원 정보 관리 Activity
 */
public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "AccountActivity";

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드

    private Uri fileUri;

    private Button btn_logout;
    private ImageView iv_profile;
    private TextView tv_username, tv_nickname;
    private EditText ed_password;

    private CompositeDisposable compositeDisposable;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initView();

        initListener();

        initValue();
    }

    private void initView() {
        btn_logout = (Button) findViewById(R.id.btn_logout);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        ed_password = (EditText) findViewById(R.id.ed_password);
    }

    private void initListener() {
        btn_logout.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        ed_password.setOnClickListener(this);
    }

    private void initValue() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance();
        String username = preferenceManager.getString("username", null);
        String nickname = preferenceManager.getString("nickname", null);

        tv_username.setText(username);
        tv_nickname.setText(nickname);
    }

    private void logOut() {
        PreferenceManager.getInstance().putString("token", "");
        new User().save();
    }

    private void initView_password(View view) {
        EditText ed_password = (EditText) view.findViewById(R.id.ed_password);
        EditText ed_password_confirm = (EditText) view.findViewById(R.id.ed_password_confirm);
        TextView tv_password = (TextView) view.findViewById(R.id.tv_password);
        TextView tv_password_confirm = (TextView) view.findViewById(R.id.tv_password_confirm);
        Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        initCheckValid_password(ed_password, ed_password_confirm, tv_password, tv_password_confirm, btn_submit);

        View.OnClickListener passwordOnClickListener = v -> {
            switch (v.getId()) {
                case R.id.btn_submit:
                    changePassword(ed_password.getText().toString(), ed_password_confirm.getText().toString());
                    break;
                case R.id.btn_cancel:
                    dialog.cancel();
                    break;
            }
        };

        btn_submit.setOnClickListener(passwordOnClickListener);
        btn_cancel.setOnClickListener(passwordOnClickListener);
    }

    private void initCheckValid_password(EditText ed_password, EditText ed_password_confirm, TextView tv_password, TextView tv_password_confirm, Button btn_submit) {
        Observable<Boolean> passwordValid = RxTextView.textChanges(ed_password)
                .map(t -> TextPatternChecker.password(t.toString()));

        Observable<Boolean> passwordValid_Confirm = RxTextView.textChanges(ed_password_confirm)
                .map(t -> TextPatternChecker.password(t.toString()));

        compositeDisposable.add(passwordValid.distinctUntilChanged()
                .cast(Boolean.class)
                .subscribe(confirm -> {
                    ed_password.setTextColor((confirm) ? Color.BLACK : Color.RED);
                    tv_password.setVisibility((confirm) ? View.GONE : View.VISIBLE);
                }));

        compositeDisposable.add(passwordValid_Confirm.distinctUntilChanged()
                .cast(Boolean.class)
                .subscribe(confirm -> {
                    ed_password_confirm.setTextColor((confirm) ? Color.BLACK : Color.RED);
                    tv_password_confirm.setVisibility((confirm) ? View.GONE : View.VISIBLE);
                }));

        Observable<Boolean> submitEnable =
                Observable.combineLatest(passwordValid, passwordValid_Confirm, (password, password_confirm) -> password && password_confirm);

        compositeDisposable.add(submitEnable.distinctUntilChanged()
                .subscribe(enabled -> btn_submit.setEnabled(enabled)));
    }

    private void settingPassword() {
        compositeDisposable = new CompositeDisposable();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View profileView = inflater.inflate(R.layout.dialog_account_password, null);
        dialog = new android.app.AlertDialog.Builder(this)
                .setView(profileView)
                .show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        initView_password(profileView);
    }

    private void changePassword(String change_password, String change_password_confirm) {
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_changepassword))
                .setMethod(NetworkController.POST)
                .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                .paramsAdd("change_password1", change_password)
                .paramsAdd("change_password2", change_password_confirm)
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Logger.e(TAG, error.getMessage());
                        Toast.makeText(AccountActivity.this, "비밀번호 변경 오류", Toast.LENGTH_SHORT).show();
                        if (dialog != null) dialog.dismiss();
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        Logger.e(TAG, new String(responseData.body));
                        if (responseData.response.code() == 200) {
                            Toast.makeText(AccountActivity.this, "비밀번호 변경 성공", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountActivity.this, "비밀번호 변경 오류", Toast.LENGTH_SHORT).show();
                        }
                        if (dialog != null) dialog.dismiss();
                    }
                }).execute();
    }

    private void actionPhotoSelect() {
        new PermissionController(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA})
                .check(new PermissionController.PermissionCallback() {
                    @Override
                    public void success() {
                        new android.support.v7.app.AlertDialog.Builder(AccountActivity.this)
                                .setMessage("Select Action")
                                .setCancelable(false)
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
                        Toast.makeText(AccountActivity.this, "권한을 허용 하지 않으면 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
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
            saveProfile(fileUri);
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
        saveProfile(fileUri);
    }

    private void saveProfile(Uri uri) {
        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.no_photo)
                .into(iv_profile);
        try {
            NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_changeprofile))
                    .setMethod(NetworkController.POST)
                    .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                    .paramsAdd("photo", getBinaryfromUri(uri))
                    .addCallback(new NetworkController.StatusCallback() {
                        @Override
                        public void onError(Throwable error) {
                            Logger.e(TAG, error.getMessage());
                            Toast.makeText(AccountActivity.this, "프로필 등록 실패", Toast.LENGTH_SHORT).show();
                            Glide.with(AccountActivity.this)
                                    .load(R.drawable.no_photo)
                                    .into(iv_profile);
                        }

                        @Override
                        public void onSuccess(NetworkController.ResponseData responseData) {
                            Logger.e(TAG, new String(responseData.body));
                            Logger.e(TAG, responseData.response.code());
                            if (responseData.response.code() == 201) {
                                Toast.makeText(AccountActivity.this, "프로필 등록 완료", Toast.LENGTH_SHORT).show();
                                User user = NetworkController.decode(User.class, new String(responseData.body));
                                user.save();
                            } else {
                                Glide.with(AccountActivity.this)
                                        .load(R.drawable.no_photo)
                                        .into(iv_profile);
                                Toast.makeText(AccountActivity.this, "프로필 등록 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getBinaryfromUri(Uri uri) throws IOException {
        InputStream iStream =   getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                logOut();
                break;
            case R.id.iv_profile:
                actionPhotoSelect();
                break;
            case R.id.ed_password:
                settingPassword();
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
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null)
            compositeDisposable.dispose();
    }
}
