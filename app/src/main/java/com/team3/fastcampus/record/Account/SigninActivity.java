package com.team3.fastcampus.record.Account;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.team3.fastcampus.record.Account.Domain.SignInData;
import com.team3.fastcampus.record.Account.Domain.SignUpData;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Logger;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.TextPatternChecker;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 회원 로그인 Activity
 */
public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SigninActivity";

    private static final int REQ_GOOGLE_SIGNIN = 9001;
    private static final int REQ_SIGNUP = 55;

    private CallbackManager facebookOnActivityResult;

    private EditText et_email;
    private EditText et_password;
    private Button btn_SignIn;
    private Button btn_SignUp;
    private LoginButton btn_Facebook;
    private SignInButton btn_Google;

    private GoogleApiClient mGoogleApiClient;

    private CompositeDisposable compositeDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        init();
    }

    private void init() {
        initView();
        initListener();
        settingFacebook();
        settingGoogle();
    }

    private void initView() {
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_SignIn = (Button) findViewById(R.id.btn_signin);
        btn_SignUp = (Button) findViewById(R.id.btn_signup);
        btn_Google = (SignInButton) findViewById(R.id.btn_google);
        btn_Facebook = (LoginButton) findViewById(R.id.btn_facebook);
    }

    private void initListener() {
        btn_SignIn.setOnClickListener(this);
        btn_SignUp.setOnClickListener(this);
        btn_Google.setOnClickListener(this);

        initCheckValid();
    }

    private void initCheckValid() {
        compositeDisposable = new CompositeDisposable();

        Observable<Boolean> emailValid = RxTextView.textChanges(et_email)
                .map(t -> TextPatternChecker.email(t.toString()));

        Observable<Boolean> passwordValid = RxTextView.textChanges(et_password)
                .map(t -> TextPatternChecker.password(t.toString()));

        compositeDisposable.add(emailValid.distinctUntilChanged()
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(color -> et_email.setTextColor(color)));

        compositeDisposable.add(passwordValid.distinctUntilChanged()
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(color -> et_password.setTextColor(color)));

        Observable<Boolean> signupEnabled =
                Observable.combineLatest(emailValid, passwordValid, (email, password) -> email && password);
        compositeDisposable.add(signupEnabled.distinctUntilChanged()
                .subscribe(enabled -> btn_SignIn.setEnabled(enabled)));
    }

    private void settingFacebook() {
        facebookOnActivityResult = CallbackManager.Factory.create();

        btn_Facebook.setReadPermissions("public_profile", "email");
        btn_Facebook.registerCallback(facebookOnActivityResult, facebook_callback);
    }

    private void settingGoogle() {
        btn_Google.setSize(SignInButton.SIZE_STANDARD);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, google_callbackManager)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void successFacebook(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                (object, response) -> {
                    try {
                        String name = object.getString("name");
                        String uuid = object.getString("id");
                        Logger.e("UUID", uuid);
                        Map<String, String> postData = new HashMap<>();
                        postData.put("username", uuid);
                        postData.put("nickname", name);
                        postData.put("password", "");
                        postData.put("user_type", "FACEBOOK");
                        signup(postData);
                    } catch (JSONException e) {
                        Toast.makeText(SigninActivity.this, "로그인을 할 수 없습니다.\n로그아웃 후 다시 시도 해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        request.executeAsync();
    }

    private void successGoogle(GoogleSignInAccount result) {
        Map<String, String> postData = new HashMap<>();
        postData.put("username", result.getEmail());
        postData.put("nickname", result.getDisplayName());
        postData.put("password", "");
        postData.put("user_type", "GOOGLE");
        signup(postData);
    }

    private void successSignIn(SignInData signInData) {
        Intent intent = new Intent();
        intent.putExtra("token", signInData.getKey());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void signup(Map<String, String> postData) {
        NetworkController networkController = NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_signup));
        networkController.excuteJsonCon(NetworkController.POST, postData, SignUpData.class, new NetworkController.NetworkControllerInterface<SignUpData>() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(SigninActivity.this, "로그인을 할 수 없습니다.\n로그아웃 후 다시 시도 해 주세요.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished(SignUpData result) {
                successSignIn(new SignInData(result.getKey()));
            }
        });
    }

    private void signin() {
        Map<String, String> postData = new HashMap<>();
        postData.put("username", et_email.getText().toString());
        postData.put("password", et_password.getText().toString());
        NetworkController networkController = NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_signin));
        networkController.excuteJsonCon(NetworkController.POST, postData, SignInData.class, new NetworkController.NetworkControllerInterface<SignInData>() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(SigninActivity.this, "로그인을 할 수 없습니다.\n잠시 후 다시 시도 해 주세요.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished(SignInData result) {
                successSignIn(result);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_signin:
                signin();
                break;
            case R.id.btn_signup:
                intent = new Intent(this, SignupActivity.class);
                startActivityForResult(intent, REQ_SIGNUP);
                break;
            case R.id.btn_google:
                intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent, REQ_GOOGLE_SIGNIN);
                break;
        }
    }

    private GoogleApiClient.OnConnectionFailedListener google_callbackManager = (connectionResult) -> {
        Logger.e(TAG, connectionResult.getErrorCode() + " " + connectionResult.getErrorMessage());
    };

    private FacebookCallback<LoginResult> facebook_callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            successFacebook(loginResult);
        }

        @Override
        public void onCancel() {
            Logger.i(TAG, "facebook onCancel");
        }

        @Override
        public void onError(FacebookException error) {
            Logger.e(TAG, "facebook error : " + error.getMessage());
        }
    };

    private void googleOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_GOOGLE_SIGNIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                successGoogle(result.getSignInAccount());
            } else {
                Logger.e(TAG, "google sign in is not success!");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookOnActivityResult.onActivityResult(requestCode, resultCode, data);
        googleOnActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_SIGNUP:
                    Bundle bundle = data.getExtras();
                    if (bundle.containsKey("token")) {
                        String token = bundle.getString("token");
                        if (token != null)
                            successSignIn(new SignInData(token));
                        break;
                    }
            }
        }
    }
}
