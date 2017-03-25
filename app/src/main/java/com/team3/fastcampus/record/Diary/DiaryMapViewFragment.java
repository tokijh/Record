package com.team3.fastcampus.record.Diary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team3.fastcampus.record.R;

/**
 * Diary의 리스트를 Map으로 보여주기 위한 Fragment
 */
public class DiaryMapViewFragment extends Fragment {


    public DiaryMapViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary_map_view, container, false);
    }

}
