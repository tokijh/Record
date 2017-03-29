package com.team3.fastcampus.record.Account;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Logger;

/**
 * 회원 로그인 Activity
 */
public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SigninActivity";

    private static final int REQ_GOOGLE_SIGNIN = 9001;

    private CallbackManager facebookOnActivityResult;

    private Button btn_SignIn;
    private Button btn_SignUp;
    private LoginButton btn_Facebook;
    private SignInButton btn_Google;

    private GoogleApiClient mGoogleApiClient;


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
        btn_SignIn = (Button) findViewById(R.id.btn_signin);
        btn_SignUp = (Button) findViewById(R.id.btn_signup);
        btn_Google = (SignInButton) findViewById(R.id.btn_google);
        btn_Facebook = (LoginButton) findViewById(R.id.btn_facebook);
    }

    private void initListener() {
        btn_SignIn.setOnClickListener(this);
        btn_SignUp.setOnClickListener(this);
        btn_Google.setOnClickListener(this);
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
        Logger.d(TAG, "Token : " + loginResult.getAccessToken().getToken());
        Logger.d(TAG, "Userid : " + loginResult.getAccessToken().getUserId());
        Logger.d(TAG, "Permission List : " + loginResult.getAccessToken().getPermissions() + "");
        Logger.d(TAG, "id : " + loginResult.getAccessToken().getUserId());
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    Logger.e("user profile", object.toString());
                }
        );
        request.executeAsync();
    }

    private void successGoogle(GoogleSignInAccount result) {
        Logger.d(TAG, result.getDisplayName());
        Logger.d(TAG, result.getEmail());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_signin:
                break;
            case R.id.btn_signup:
                intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.login_button_google:
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
            Logger.e(TAG, error.getMessage());
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
    }

}
