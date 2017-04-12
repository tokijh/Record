package com.team3.fastcampus.record.InDiary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * InDiary의 리스트를 보여주기 위한 Fragment
 */
public class InDiaryListViewFragment extends Fragment {

    public static final String TAG = "InDiaryListViewFragment";

    private View view;

    private RecyclerView recyclerView;
    private ProgressBar progress;

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
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_in_diary_view_recyclerview);
        progress = (ProgressBar) view.findViewById(R.id.progress);
    }

    private void initAdapter() {
        inDiaryViewRecyclerAdapter = new InDiaryViewRecyclerAdapter(getContext());
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
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_diary))
                .setMethod(NetworkController.GET)
                .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        progressDisable();
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        try {
                            Logger.e(TAG, new String(responseData.body));
                            if (responseData.response.code() == 200) {
                                JSONArray root = NetworkController.decodeArray(new String(responseData.body));
                                for (int i = 0; i < root.length(); i++) {
                                    JSONObject jsonDiary = root.getJSONObject(i);
                                    if (jsonDiary.getLong("pk") == inDiaryListCallback.getDiary().pk) {
                                        JSONArray jsonPost = jsonDiary.getJSONArray("post");
                                        List<InDiary> diaries = NetworkController.decode(new TypeToken<List<InDiary>>() {
                                        }.getType(), jsonPost.toString());
                                        inDiaryViewRecyclerAdapter.set(diaries);
                                        saveToDB(diaries);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            responseData.response.close();
                            progressDisable();
                        }
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
        List<InDiary> inDiaries1 = realmDatabaseManager.getAll(InDiary.class);
        Logger.e(TAG, PreferenceManager.getInstance().getString("username", null));
        Logger.e(TAG, inDiaries.size() + " " + inDiaries1.size());
        for (InDiary inDiary : inDiaries1) {
            Logger.e(TAG, inDiary.username + " " + inDiary.diary + " " + inDiary.pk + " " + inDiary.photo_list.size() + " " + ((inDiary.photo_list.size() > 0) ? inDiary.photo_list.get(0) + " :: " + inDiary.photo_list.get(0).photo: "NULL"));
        }
        inDiaryViewRecyclerAdapter.set(inDiaries);
        progressDisable();
    }

    private void progressEnable() {
        progress.setVisibility(View.VISIBLE);
    }

    private void progressDisable() {
        progress.setVisibility(View.GONE);
    }

    interface InDiaryListCallback {
        Diary getDiary();
    }
}
