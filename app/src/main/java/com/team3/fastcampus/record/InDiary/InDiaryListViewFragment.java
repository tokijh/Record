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

import com.google.gson.reflect.TypeToken;
import com.team3.fastcampus.record.Diary.Model.Diary;
import com.team3.fastcampus.record.InDiary.Adapter.InDiaryViewRecyclerAdapter;
import com.team3.fastcampus.record.InDiary.Model.InDiary;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

/**
 * InDiary의 리스트를 보여주기 위한 Fragment
 */
public class InDiaryListViewFragment extends Fragment {

    private View view;

    private RecyclerView recyclerView;

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
    }

    private void initAdapter() {
        inDiaryViewRecyclerAdapter = new InDiaryViewRecyclerAdapter(getContext());
        recyclerView.setAdapter(inDiaryViewRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getData(int position) {
        NetworkController.newInstance(getString(R.string.server_url) + getString(R.string.server_diary))
                .setMethod(NetworkController.GET)
                .headerAdd("Authorization", "Token " + PreferenceManager.getInstance().getString("token", null))
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {

                    }

                    @Override
                    public void onSuccess(Response response) {
                        try {
                            String rspstr = response.body().string();
                            if (response.code() == 200) {
                                JSONArray root = NetworkController.decodeArray(rspstr);
                                for (int i=0;i<root.length();i++) {
                                    JSONObject jsonDiary = root.getJSONObject(i);
                                    if (jsonDiary.getLong("pk") == inDiaryListCallback.getDiary().pk) {
                                        JSONArray jsonPost = jsonDiary.getJSONArray("post");
                                        inDiaryViewRecyclerAdapter.set(NetworkController.decode(new TypeToken<List<InDiary>>() {
                                        }.getType(), jsonPost.toString()));
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .excute();
    }

    interface InDiaryListCallback {
        Diary getDiary();
    }
}
