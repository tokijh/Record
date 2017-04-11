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
import android.widget.Toast;

import com.team3.fastcampus.record.Account.SigninActivity;
import com.team3.fastcampus.record.Diary.DiaryViewFragment;
import com.team3.fastcampus.record.Diary.Model.Diary;
import com.team3.fastcampus.record.InDiary.InDiaryViewFragment;
import com.team3.fastcampus.record.Util.PreferenceManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DiaryViewFragment.DiaryViewInterface {

    private static final int REQ_LOGIN = 54;

    FragmentManager manager;
    DrawerLayout drawer;
    Intent intent;

    // Fragments
    DiaryViewFragment diaryViewFragment;
    InDiaryViewFragment inDiaryViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.create(this);

        String token = PreferenceManager.getInstance().getString("token", "");
        if ("".equals(token)) {
            toSignUp();
        } else {
            init();
        }
    }

    private void init() {
        initView();

        initFragmentSettings();

        // 초기 화면 지정
        showContentFragment(diaryViewFragment);
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
    }

    private void initFragmentSettings() {
        manager = getSupportFragmentManager();

        diaryViewFragment = new DiaryViewFragment();
        inDiaryViewFragment = new InDiaryViewFragment();
    }

    private void toSignUp() {
        Toast.makeText(this, "로그인을 해야 이용 할 수 있습니다.", Toast.LENGTH_SHORT).show();
        intent = new Intent(MainActivity.this, SigninActivity.class);
        startActivityForResult(intent, REQ_LOGIN);
    }

    private void successSignIn(String token, String username) {
        PreferenceManager.getInstance().putString("token", token);
        PreferenceManager.getInstance().putString("username", username);
        init();
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (manager.getBackStackEntryCount() > 2) {
                manager.popBackStackImmediate();
                manager.beginTransaction().commit();
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Define Fragment Interface
    @Override
    public void showInDiary(Diary diary) {
        inDiaryViewFragment.setDiary(diary);
        showContentFragment(inDiaryViewFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_LOGIN) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle.containsKey("token") && bundle.containsKey("username")) {
                    String token = bundle.getString("token");
                    String username = bundle.getString("username");
                    if (token != null && username != null) {
                        successSignIn(token, username);
                        return;
                    }
                }
                throw new RuntimeException("token or username is null");
            } else {
                Toast.makeText(this, "로그인을 해야 이용 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
