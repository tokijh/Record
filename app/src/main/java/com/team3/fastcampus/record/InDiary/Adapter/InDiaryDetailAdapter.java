package com.team3.fastcampus.record.InDiary.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.team3.fastcampus.record.R;

/**
 * Created by kimkyuwan on 2017. 4. 20..
 */

public class InDiaryDetailAdapter extends PagerAdapter {

    LayoutInflater inflater;

    public InDiaryDetailAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //return super.instantiateItem(container, position);

        View view = inflater.inflate(R.layout.activity_in_diary_detail_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.in_diary_detail_item_imageview);

        imageView.setImageResource(R.drawable.cast_abc_scrubber_control_off_mtrl_alpha+position);
        container.addView(view);


        return view;

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {


        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        container.removeView((View) object);
    }


}
