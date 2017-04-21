package com.team3.fastcampus.record.InDiary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.reflect.TypeToken;
import com.team3.fastcampus.record.Diary.Model.Diary;
import com.team3.fastcampus.record.InDiary.Adapter.InDiaryViewRecyclerAdapter;
import com.team3.fastcampus.record.InDiary.Model.Image;
import com.team3.fastcampus.record.InDiary.Model.InDiary;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Logger;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.PreferenceManager;
import com.team3.fastcampus.record.Util.RealmDatabaseManager;

import java.util.List;

/**
 * InDiary의 리스트를 보여주기 위한 Fragment
 */
public class InDiaryListViewFragment extends Fragment implements InDiaryViewRecyclerAdapter.InDiaryListCallback {

    public static final String TAG = "InDiaryListViewFragment";

    private View view;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FloatingActionButton fab_add;

    private InDiaryViewRecyclerAdapter inDiaryViewRecyclerAdapter;

    private InDiaryListCallback inDiaryListCallback;

    private int position = 0;

    public InDiaryListViewFragment(Fragment fragment) {
        if (fragment instanceof InDiaryListCallback) {
            inDiaryListCallback = (InDiaryListCallback)  fragment;
        } else {
            throw new RuntimeException(fragment.toString() + " must implement InDiaryListCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }

        view = inflater.inflate(R.layout.fragment_in_diary_list_view, container, false);

        initView();

        initListener();

        initAdapter();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initValue();
    }

    public void init() {
        initValue();
    }

    private void initValue() {
        position = 0;
        if (inDiaryViewRecyclerAdapter != null) {
            inDiaryViewRecyclerAdapter.clear();
            getData(position);
        }
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_in_diary_view_swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_in_diary_view_recyclerview);
        fab_add = (FloatingActionButton) view.findViewById(R.id.fragment_in_diary_view_fab_add);
    }

    private void initListener() {
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshOnRefreshListener);
        fab_add.setOnClickListener(onClickListener);
    }

    private void initAdapter() {
        inDiaryViewRecyclerAdapter = new InDiaryViewRecyclerAdapter(getContext(), this);
        recyclerView.setAdapter(inDiaryViewRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_indiary) + inDiaryListCallback.getDiary().pk + getString(R.string.server_indiary_end))
                .setMethod(NetworkController.GET)
                .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        progressDisable();
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        Logger.e(TAG, new String(responseData.body));
                        if (responseData.response.code() == 200) {
                            List<InDiary> inDiaries = NetworkController.decode(new TypeToken<List<InDiary>>() {
                            }.getType(), new String(responseData.body));
                            for (InDiary inDiary : inDiaries) {
                                inDiary.title = inDiaryListCallback.getDiary().title;
                                // TODO 서버에서 수정 완료시 삭제
                                inDiary.created_date = inDiary.created_date.replace("T", " ");
                                // TODO 서버에서 수정 완료시 삭제
                                inDiary.created_date = inDiary.created_date.replace("Z", " ");
                            }
                            inDiaryViewRecyclerAdapter.set(inDiaries);
                            saveToDB(inDiaries);
                        }
                        progressDisable();
                    }
                })
                .execute();
    }

    private void saveToDB(List<InDiary> inDiaries) {
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        for (InDiary inDiary : inDiaries) {
            InDiary isSaved = realmDatabaseManager.get(InDiary.class)
                    .equalTo("username", PreferenceManager.getInstance().getString("username", null))
                    .equalTo("pk", inDiary.pk)
                    .findFirst();
            if (isSaved == null) {
                realmDatabaseManager.create(InDiary.class, (realm, realmObject) -> {
                    realmObject.pk = inDiary.pk;
                    realmObject.username = PreferenceManager.getInstance().getString("username", null);
                    realmObject.diary = inDiary.diary;
                    realmObject.title = inDiary.title;
                    realmObject.content = inDiary.content;
                    for (Image image : inDiary.photo_list) {
                        Image forsave = realmDatabaseManager.create(Image.class);
                        forsave.photo = image.photo;
                        forsave.post = image.post;
                        forsave.gpsLatitude = image.gpsLatitude;
                        forsave.gpsLongitude = image.gpsLongitude;
                        forsave.pk = image.pk;
                        realmObject.photo_list.add(forsave);
                    }
                    realmObject.created_date = inDiary.created_date;
                });
            }
        }
    }

    private void loadFromDB(int position) {
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        List<InDiary> inDiaries = realmDatabaseManager.get(InDiary.class)
                .equalTo("username", PreferenceManager.getInstance().getString("username", null))
                .equalTo("diary", inDiaryListCallback.getDiary().pk)
                .findAll();
        inDiaryViewRecyclerAdapter.set(inDiaries);
        progressDisable();
    }

    private void progressEnable() {
        swipeRefreshLayout.setRefreshing(true);
    }

    private void progressDisable() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private SwipeRefreshLayout.OnRefreshListener swipeRefreshOnRefreshListener = () -> {
        getData(position);
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.fragment_in_diary_view_fab_add:
                onInDiaryManage(-1l, InDiaryManageActivity.MODE_CREATE, InDiary.getCurrentTime());
                break;
        }
    };

    @Override
    public void onItemClick(long pk) {
        // TODO InDiaryDetailActivity 호출
    }

    @Override
    public void onInDiaryManage(long pk, int mode, String date) {
        startActivity(new Intent(getContext(), InDiaryManageActivity.class)
                .putExtra("PK", pk)
                .putExtra("DIARY", inDiaryListCallback.getDiary().pk)
                .putExtra("MODE", mode)
                .putExtra("DATE", date));
    }

    interface InDiaryListCallback {
        Diary getDiary();
    }
}
