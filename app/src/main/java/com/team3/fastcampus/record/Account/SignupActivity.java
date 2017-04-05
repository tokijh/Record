package com.team3.fastcampus.record.Account;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.TextPatternChecker;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 회원 가입 Activity
 */
public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_email;
    private EditText et_password;
    private EditText et_name;
    private EditText et_nickname;

    private Button btn_signup;

    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();

        initListener();
    }

    private void initView() {
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_name = (EditText) findViewById(R.id.et_name);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        btn_signup = (Button) findViewById(R.id.btn_signup);
    }

    private void initListener() {
        btn_signup.setOnClickListener(this);

        initCheckValid();
    }

    private void initCheckValid() {
        compositeDisposable = new CompositeDisposable();

        Observable<Boolean> nameValid = RxTextView.textChanges(et_name)
                .map(t -> t.length() > 0);

        Observable<Boolean> emailValid = RxTextView.textChanges(et_email)
                .map(t -> TextPatternChecker.email(t.toString()));

        Observable<Boolean> passwordValid = RxTextView.textChanges(et_password)
                .map(t -> TextPatternChecker.password(t.toString()));

        compositeDisposable.add(nameValid.distinctUntilChanged()
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(color -> et_name.setTextColor(color)));

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

    private void signUp() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String name = et_name.getText().toString();
        String nickname = et_nickname.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                signUp();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
