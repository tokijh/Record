package com.team3.fastcampus.record.InDiary;

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
 * InDiary의 리스트를 Map으로 보여주기 위한 Fragment
 */
public class InDiaryMapViewFragment extends Fragment {

    private View view;

    public InDiaryMapViewFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }

        view = inflater.inflate(R.layout.fragment_in_diary_map_view, container, false);

        return view;
    }

    public void init() {

    }

}
