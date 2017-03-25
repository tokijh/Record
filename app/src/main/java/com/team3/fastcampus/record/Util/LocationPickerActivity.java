package com.team3.fastcampus.record.Util;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.team3.fastcampus.record.R;

/**
 * 지도에서 선택한 위치 또는 검색한 장소의 위치를 선택하여 Return해준다.
 */
public class LocationPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
    }
}
