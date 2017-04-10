package com.team3.fastcampus.record.InDiary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.team3.fastcampus.record.Diary.Adapter.DiaryViewRecyclerAdapter;
import com.team3.fastcampus.record.InDiary.Adapter.InDiaryViewRecyclerAdapter;
import com.team3.fastcampus.record.InDiary.Domain.Image;
import com.team3.fastcampus.record.InDiary.Domain.InDiary;
import com.team3.fastcampus.record.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * InDiary의 리스트를 보여주기 위한 Fragment
 */
public class InDiaryListViewFragment extends Fragment {

    private View view;

    private RecyclerView recyclerView;

    private InDiaryViewRecyclerAdapter inDiaryViewRecyclerAdapter;

    public InDiaryListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_in_diary_list_view, container, false);

        initView();

        initAdapter();

        // TestDatas

        // To get byte[] data from R.drawable.night
        Drawable d = getResources().getDrawable(R.drawable.night);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();

        // To get byte[] data from R.drawable.logo
        Drawable d1 = getResources().getDrawable(R.drawable.logo);
        Bitmap bitmap1 = ((BitmapDrawable)d1).getBitmap();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
        byte[] bitmapdata1 = stream1.toByteArray();

        List<Image> images = new ArrayList<>();
        Image image1 = new Image();
        image1.imageData = bitmapdata;
        Image image2 = new Image();
        image2.imageData = bitmapdata1;

        images.add(image1);
        images.add(image2);
        InDiary inDiary = new InDiary(1, "title", "date", "content", images);
        inDiaryViewRecyclerAdapter.add(inDiary);
        inDiaryViewRecyclerAdapter.add(inDiary);
        inDiaryViewRecyclerAdapter.add(inDiary);
        inDiaryViewRecyclerAdapter.add(inDiary);

        return view;
    }


    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_in_diary_view_recyclerview);
    }

    private void initAdapter() {
        inDiaryViewRecyclerAdapter = new InDiaryViewRecyclerAdapter(getContext());
        recyclerView.setAdapter(inDiaryViewRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
