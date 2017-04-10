package com.team3.fastcampus.record.Account;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.team3.fastcampus.record.Account.Model.SignUpData;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Logger;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.TextPatternChecker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.Response;

/**
 * 회원 가입 Activity
 */
public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SignupActivity";

    private EditText et_email;
    private EditText et_password;
    private EditText et_nickname;

    private Button btn_signup;

    private ProgressBar progress;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();

        initListener();
    }

    private void initView() {
        et_email = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        progress = (ProgressBar) findViewById(R.id.progress);
    }

    private void initListener() {
        btn_signup.setOnClickListener(this);

        initCheckValid();
    }

    private void initCheckValid() {
        compositeDisposable = new CompositeDisposable();

        Observable<Boolean> nameValid = RxTextView.textChanges(et_nickname)
                .map(t -> t.length() > 0);

        Observable<Boolean> emailValid = RxTextView.textChanges(et_email)
                .map(t -> TextPatternChecker.email(t.toString()));

        Observable<Boolean> passwordValid = RxTextView.textChanges(et_password)
                .map(t -> TextPatternChecker.password(t.toString()));

        compositeDisposable.add(nameValid.distinctUntilChanged()
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(color -> et_nickname.setTextColor(color)));

        compositeDisposable.add(emailValid.distinctUntilChanged()
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(color -> et_email.setTextColor(color)));

        compositeDisposable.add(passwordValid.distinctUntilChanged()
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(color -> et_password.setTextColor(color)));

        Observable<Boolean> signupEnabled =
                Observable.combineLatest(nameValid, emailValid, passwordValid, (name, email, password) -> name && email && password);
        compositeDisposable.add(signupEnabled.distinctUntilChanged()
                .subscribe( enabled -> btn_signup.setEnabled(enabled)));
    }

    private void signup() {
        progressEnable();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String nickname = et_nickname.getText().toString();

        Map<String, Object> postData = new HashMap<>();
        postData.put("username", email);
        postData.put("password", password);
        postData.put("nickname", nickname);
        postData.put("user_type", "NORMAL");
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_signup))
                .setMethod(NetworkController.POST)
                .paramsSet(postData)
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(SignupActivity.this, "회원 가입을 할 수 없습니다.\n잠시 후 다시 시도 해 주세요.", Toast.LENGTH_SHORT).show();
                        progressDisable();
                    }

                    @Override
                    public void onSuccess(Response response) {
                        try {
                            if (response.code() == 201) {
                                SignUpData signUpData = NetworkController.decode(SignUpData.class, response.body().string());
                                Toast.makeText(SignupActivity.this, "회원 가입 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("token", signUpData.getToken());
                                setResult(RESULT_OK, intent);
                                finish();
                                return;
                            }
                        } catch (IOException e) {
                            Logger.e(TAG, "signup - NetworkController - excute - onSuccess - IOException : " + e.getMessage());
                        } catch (JsonSyntaxException e) {
                            Logger.e(TAG, "signup - NetworkController - excute - onSuccess - JsonSyntaxException : " + e.getMessage());
                        } finally {
                            response.close();
                            progressDisable();
                        }
                        Toast.makeText(SignupActivity.this, "회원 가입을 할 수 없습니다.\n계정을 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                })
                .excute();
    }

    private void progressEnable() {
        progress.setVisibility(View.VISIBLE);
    }

    private void progressDisable() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                signup();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
