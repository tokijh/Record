package com.team3.fastcampus.record;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team3.fastcampus.record.Account.AccountActivity;
import com.team3.fastcampus.record.Account.SigninActivity;
import com.team3.fastcampus.record.Diary.DiaryViewFragment;
import com.team3.fastcampus.record.Diary.Model.Diary;
import com.team3.fastcampus.record.InDiary.InDiaryViewFragment;
import com.team3.fastcampus.record.Model.User;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.PreferenceManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DiaryViewFragment.DiaryViewInterface, View.OnClickListener {

    private static final int REQ_LOGIN = 54;

    private boolean isViewReady = false;

    FragmentManager manager;
    DrawerLayout drawer;
    Intent intent;

    private ImageView head_iv_profile;
    private TextView head_tv_username;
    private TextView head_tv_nickname;

    // Fragments
    DiaryViewFragment diaryViewFragment;
    InDiaryViewFragment inDiaryViewFragment;

    private Fragment showing_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.create(this);

        loginCheck();
    }

    private void loginCheck() {
        String token = PreferenceManager.getInstance().getString("token", "");
        if ("".equals(token)) {
            toSignUp();
        } else {
            if (NetworkController.isNetworkStatusENABLE(NetworkController.checkNetworkStatus(this))) {
                checkTokenAvailable();
            } else {
                init();
            }
        }
    }

    private void init() {
        if (!isViewReady) {
            initView();

            initFragmentSettings();

            // 초기 화면 지정
            showContentFragment(diaryViewFragment);

            isViewReady = true;
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header = navigationView.getHeaderView(0);
        nav_header.setOnClickListener(this);

        head_iv_profile = (ImageView) nav_header.findViewById(R.id.iv_profile);
        head_tv_nickname = (TextView) nav_header.findViewById(R.id.tv_nickname);
        head_tv_username = (TextView) nav_header.findViewById(R.id.tv_username);

        initHeadView();
    }

    private void initFragmentSettings() {
        manager = getSupportFragmentManager();

        diaryViewFragment = new DiaryViewFragment();
        inDiaryViewFragment = new InDiaryViewFragment();
    }

    private void initHeadView() {
        head_tv_username.setText(PreferenceManager.getInstance().getString("username", ""));
        head_tv_nickname.setText(PreferenceManager.getInstance().getString("nickname", ""));

        Glide.with(this)
                .load(PreferenceManager.getInstance().getString("profile_img", ""))
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(head_iv_profile);
    }

    private void toSignUp() {
        Toast.makeText(this, "로그인을 해야 이용 할 수 있습니다.", Toast.LENGTH_SHORT).show();
        PreferenceManager.getInstance().putString("token", "");
        new User().save();
        intent = new Intent(MainActivity.this, SigninActivity.class);
        startActivityForResult(intent, REQ_LOGIN);
    }

    private void successSignIn(String token) {
        PreferenceManager.getInstance().putString("token", token);
        init();
    }

    private void checkTokenAvailable() {
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_validtoken))
                .setMethod(NetworkController.POST)
                .paramsAdd("key", PreferenceManager.getInstance().getString("token", ""))
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(MainActivity.this, "로그인이 만료되어 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                        toSignUp();
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        if (responseData.response.code() == 200) {
                            init();
                        } else {
                            Toast.makeText(MainActivity.this, "로그인이 만료되어 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                            toSignUp();
                        }
                    }
                })
                .execute();
    }

    /**
     * 해당 fragment로 Fragment를 띄움
     * @param fragment
     */
    public void showContentFragment(Fragment fragment) {
        manager.beginTransaction()
        .replace(R.id.contentView, fragment)
        .addToBackStack(fragment.getTag())
        .commit();
        showing_fragment = fragment;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (showing_fragment != diaryViewFragment) {
                manager.popBackStackImmediate();
                manager.beginTransaction().commit();
            } else {
                finish();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_diary:
                showContentFragment(diaryViewFragment);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_head_layout:
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
                break;
        }
    }

    // Define Fragment Interface
    @Override
    public void showInDiary(Diary diary) {
        inDiaryViewFragment.setDiary(diary);
        showContentFragment(inDiaryViewFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isViewReady) {
            loginCheck();

            initHeadView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_LOGIN) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle.containsKey("token")) {
                    String token = bundle.getString("token");
                    if (token != null) {
                        successSignIn(token);
                        return;
                    }
                }
            } else {
                Toast.makeText(this, "로그인을 해야 이용 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
