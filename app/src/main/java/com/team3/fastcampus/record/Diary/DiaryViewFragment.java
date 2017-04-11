package com.team3.fastcampus.record.Diary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;

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
    private RecyclerView recyclerView;
    private ProgressBar progress;

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
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_diary_view_recyclerview);
        progress = (ProgressBar) view.findViewById(R.id.progress);
    }

    private void initAdapter() {
        diaryViewRecyclerAdapter = new DiaryViewRecyclerAdapter(this);
        recyclerView.setAdapter(diaryViewRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initListener() {
        ed_search.addTextChangedListener(searchWatcher);
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
                            Logger.e(TAG, responseData.body);
                            if (responseData.response.code() == 200) {
                                List<Diary> diaries = NetworkController.decode(new TypeToken<List<Diary>>() {
                                }.getType(), responseData.body);
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
                .excute();
    }

    private void saveToDB(List<Diary> diaries) {
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        for (Diary diary : diaries) {
            Diary isSaved = realmDatabaseManager.get(Diary.class)
                    .equalTo("username", PreferenceManager.getInstance().getString("username", null))
                    .equalTo("pk", diary.pk)
                    .findFirst();
            if (isSaved == null) {
                realmDatabaseManager.create(Diary.class, (realm, realmObject) -> {
                    realmObject.pk = diary.pk;
                    realmObject.username = PreferenceManager.getInstance().getString("username", null);
                    realmObject.title = diary.title;
                    realmObject.created_date = diary.created_date;
                });
            }
        }
    }

    private void loadFromDB(int position) {
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        List<Diary> diaries = realmDatabaseManager.get(Diary.class)
                .equalTo("username", PreferenceManager.getInstance().getString("username", null))
                .findAll();
        List<Diary> diaries1 = realmDatabaseManager.getAll(Diary.class);
        Logger.e(TAG, PreferenceManager.getInstance().getString("username", null));
        Logger.e(TAG, diaries.size() + " " + diaries1.size());
        for (Diary diary : diaries1) {
            Logger.e(TAG, diary.username + " " + diary.title + " " + diary.pk);
        }
        diaryViewRecyclerAdapter.set(diaries);
        progressDisable();
    }

    private void progressEnable() {
        progress.setVisibility(View.VISIBLE);
    }

    private void progressDisable() {
        progress.setVisibility(View.GONE);
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

    public interface DiaryViewInterface {
        void showInDiary(Diary diary);
    }
}
