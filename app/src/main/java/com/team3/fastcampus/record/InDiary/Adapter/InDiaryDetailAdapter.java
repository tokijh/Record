package com.team3.fastcampus.record.InDiary.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.team3.fastcampus.record.InDiary.Model.Image;
import com.team3.fastcampus.record.R;

import java.util.ArrayList;

/**
 * Created by kimkyuwan on 2017. 4. 20..
 */

public class InDiaryDetailAdapter extends PagerAdapter {

    ArrayList<Image> datas;
    Context context;
    LayoutInflater inflater;

    public InDiaryDetailAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public Object instantiateItem(ViewGroup container, int position) {
        //return super.instantiateItem(container, position);

        View view = inflater.inflate(R.layout.activity_in_diary_detail_item, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.in_diary_detail_item_imageview);
        Image image = datas.get(position);

        Glide.with(context).load(image.photo).placeholder(R.drawable.no_photo).into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View)object);
    }
}
