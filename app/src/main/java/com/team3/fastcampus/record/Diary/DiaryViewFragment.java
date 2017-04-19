package com.team3.fastcampus.record.Diary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.team3.fastcampus.record.*;
import com.team3.fastcampus.record.Diary.Adapter.DiaryViewRecyclerAdapter;
import com.team3.fastcampus.record.Diary.Model.Diary;
import com.team3.fastcampus.record.Util.Logger;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.PreferenceManager;
import com.team3.fastcampus.record.Util.RealmDatabaseManager;

import java.util.List;

/**
 * Diary를 보여주기 위한 메인뷰
 */
public class DiaryViewFragment extends Fragment implements DiaryViewRecyclerAdapter.DiaryListCallback {

    public static final String TAG = "DiaryViewFragment";

    private View view;

    private EditText ed_search;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private FloatingActionButton fab_add;

    private DiaryViewRecyclerAdapter diaryViewRecyclerAdapter;

    // Connector with Activity
    private DiaryViewInterface diaryViewInterface;

    private int position = 0;

    public DiaryViewFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }

        view = inflater.inflate(R.layout.fragment_diary_view, container, false);

        initView();

        initAdapter();

        initListener();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    public void init() {
        initValue();

        getData(position);
    }

    private void initValue() {
        position = 0;
        ed_search.setText("");
        diaryViewRecyclerAdapter.clear();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ed_search = (EditText) view.findViewById(R.id.fragment_diary_view_search);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_diary_view_swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_diary_view_recyclerview);
        fab_add = (FloatingActionButton) view.findViewById(R.id.fragment_diary_view_fab_add);
    }

    private void initAdapter() {
        diaryViewRecyclerAdapter = new DiaryViewRecyclerAdapter(this);
        recyclerView.setAdapter(diaryViewRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fab_add.setOnClickListener(onClickListener);
    }

    private void initListener() {
        ed_search.addTextChangedListener(searchWatcher);
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshOnRefreshListener);
    }

    private void getData(int position) {
        progressEnable();
        if (NetworkController.isNetworkStatusENABLE(NetworkController.checkNetworkStatus(getContext()))) {
            loadFromServer(position);
        } else {
            loadFromDB(position);
        }
    }

    private void loadFromServer(int position) {
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_diary))
                .setMethod(NetworkController.GET)
                .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        progressDisable();
                        Logger.e(TAG, "getData - error" + error.getMessage());
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        try {
                            Logger.e(TAG, new String(responseData.body));
                            if (responseData.response.code() == 200) {
                                List<Diary> diaries = NetworkController.decode(new TypeToken<List<Diary>>() {
                                }.getType(), new String(responseData.body));
                                diaryViewRecyclerAdapter.set(diaries);
                                saveToDB(diaries);
                                return;
                            }
                        } catch (JsonSyntaxException e) {
                            Logger.e(TAG, "signin - NetworkController - excute - onSuccess - JsonSyntaxException : " + e.getMessage());
                        } catch (Exception e) {
                            Logger.e(TAG, "signin - NetworkController - excute - onSuccess - Exception : " + e.getMessage());
                        } finally {
                            responseData.response.close();
                            progressDisable();
                        }
                    }
                })
                .execute();
    }

    private void saveToDB(List<Diary> diaries) {
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        for (Diary diary : diaries) {
            Diary isSaved = realmDatabaseManager.get(Diary.class)
                    .equalTo("pk", diary.pk)
                    .findFirst();
            if (isSaved == null) {
                realmDatabaseManager.create(Diary.class, (realm, realmObject) -> {
                    realmObject.username = PreferenceManager.getInstance().getString("username", null);
                    realmObject.pk = diary.pk;
                    realmObject.title = diary.title;
                    realmObject.start_date = diary.start_date;
                    realmObject.end_date = diary.end_date;
                    realmObject.post_count = diary.post_count;
                    realmObject.cover_image = diary.cover_image;
                });
            }
        }
    }

    private void loadFromDB(int position) {
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        List<Diary> diaries = realmDatabaseManager.get(Diary.class)
                .equalTo("username", PreferenceManager.getInstance().getString("username", null))
                .findAll();
        diaryViewRecyclerAdapter.set(diaries);
        progressDisable();
    }

    private void progressEnable() {
        swipeRefreshLayout.setRefreshing(true);
    }

    private void progressDisable() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private SwipeRefreshLayout.OnRefreshListener swipeRefreshOnRefreshListener = () -> {
        getData(position);
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.fragment_diary_view_fab_add:
                onDiaryManage(-1l, DiaryManageActivity.MODE_CREATE);
                break;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DiaryViewInterface) {
            diaryViewInterface = (DiaryViewInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DiaryViewInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        diaryViewInterface = null;
        RealmDatabaseManager.destroy();
    }

    @Override
    public void onItemClick(Diary diary) {
        diaryViewInterface.showInDiary(diary);
    }

    @Override
    public void onDiaryManage(long pk, int mode) {
        if (!NetworkController.isNetworkStatusENABLE(NetworkController.checkNetworkStatus(getContext()))) {
            Toast.makeText(getContext(), "이 작업은 인터넷 연결이 필요 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), DiaryManageActivity.class);
        intent.putExtra("PK", pk);
        intent.putExtra("MODE", mode);
        getContext().startActivity(intent);
    }

    public interface DiaryViewInterface {
        void showInDiary(Diary diary);
    }
}
