package com.team3.fastcampus.record.InDiary.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.team3.fastcampus.record.InDiary.Model.Image;
import com.team3.fastcampus.record.InDiary.Model.ImageData;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.NetworkController;
import com.team3.fastcampus.record.Util.RealmDatabaseManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

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
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ImageData imageData = realmDatabaseManager.get(ImageData.class)
                .equalTo("url", images.get(position).photo)
                .findFirst();
        if (imageData == null) {
            Glide.with(context)
                    .load(images.get(position).photo)
                    .placeholder(R.drawable.night)
                    .into(imageView);
            saveImageToDB(images.get(position));
        } else {
            if (imageData.data != null) {
                Glide.with(context)
                        .load(imageData.data)
                        .placeholder(R.drawable.night)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(imageData.url)
                        .placeholder(R.drawable.night)
                        .into(imageView);
            }
        }
        container.addView(imageView);
        return imageView;
    }

    private void saveImageToDB(Image image) {
        NetworkController.newInstance(image.photo)
                .setMethod(NetworkController.GET)
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        RealmDatabaseManager.getInstance().create(ImageData.class, (realm, realmObject) -> {
                            realmObject.url = image.photo;
                            realmObject.data = responseData.body;
                        });
                    }
                })
                .execute(Schedulers.io());
    }

    private void updateImageToDB(Image image) {
        NetworkController.newInstance(image.photo)
                .setMethod(NetworkController.GET)
                .addCallback(new NetworkController.StatusCallback() {
                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }

                    @Override
                    public void onSuccess(NetworkController.ResponseData responseData) {
                        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.newInstance();

                        RealmDatabaseManager.newInstance().create(ImageData.class, (realm, realmObject) -> {
                            realmObject.url = image.photo;
                            realmObject.data = responseData.body;
                        });
                    }
                })
                .execute(Schedulers.io());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
