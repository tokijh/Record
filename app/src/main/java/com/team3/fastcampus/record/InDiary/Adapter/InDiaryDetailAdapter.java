package com.team3.fastcampus.record.InDiary.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team3.fastcampus.record.InDiary.InDiaryDetailActivity;
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
    String flag = "INVISIBLE";
    RelativeLayout layout;

    public InDiaryDetailAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //return super.instantiateItem(container, position);

        View view = inflater.inflate(R.layout.activity_in_diary_detail_item, null);
        layout = (RelativeLayout) view.findViewById(R.id.in_diary_detail_content_area);
        //     layout.setVisibility(View.INVISIBLE);
        ImageView imageView = (ImageView) view.findViewById(R.id.in_diary_detail_item_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (flag) {
                    case "INVISIBLE":
                        intent = new Intent(context, InDiaryDetailActivity.class);
                        Toast.makeText(context, "INVISIBLE", Toast.LENGTH_SHORT).show();
                        startActivityForResult(intent, view.VISIBLE);

                        break;

                    case "VISIBLE":
                        intent = new Intent(context, InDiaryDetailActivity.class);
                        Toast.makeText(context, "VISIBLE", Toast.LENGTH_SHORT).show();
                        startActivityForResult(intent, view.INVISIBLE);

                        break;

                }
            }
        });


        Glide.with(context).load("null").placeholder(R.drawable.no_photo).into(imageView);

        container.addView(view);
        return view;
    }

    private void startActivityForResult(Intent intent, int requestCode) {
        Activity activity = null;
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }


}
