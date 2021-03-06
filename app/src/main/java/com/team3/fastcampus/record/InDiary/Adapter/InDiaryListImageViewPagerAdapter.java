package com.team3.fastcampus.record.InDiary.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.team3.fastcampus.record.InDiary.Model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokijh on 2017. 4. 5..
 */

public class InDiaryListImageViewPagerAdapter extends PagerAdapter {

    private List<Image> images;
    private Context context;

    public InDiaryListImageViewPagerAdapter(Context context) {
        images = new ArrayList<>();
        this.context = context;
    }

    public void add(Image image) {
        images.add(image);
        this.notifyDataSetChanged();
    }

    public void set(List<Image> images) {
        this.images = images;
        this.notifyDataSetChanged();
    }

    public Image get(int position) {
        return images.get(position);
    }

    @Override
    public int getCount() {
        try {
            return images.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        Glide.with(context)
                .load(images.get(position).photo)
                .fitCenter()
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
