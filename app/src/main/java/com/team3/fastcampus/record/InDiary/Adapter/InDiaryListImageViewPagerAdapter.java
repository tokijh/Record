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

import io.realm.RealmResults;

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
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageProssessing(images.get(position), imageView);
        container.addView(imageView);
        return imageView;
    }

    private void imageProssessing(Image image, ImageView imageView) {
        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
        ImageData imageData = realmDatabaseManager.get(ImageData.class)
                .equalTo("url", image.photo)
                .findFirst();
        if (imageData == null) {
            Glide.with(context)
                    .load(image.photo)
                    .placeholder(R.drawable.night)
                    .into(imageView);
            saveImageToDB(image);
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
                updateImageToDB(image);
            }
        }
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
                        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
                        realmDatabaseManager.create(ImageData.class, (realm, realmObject) -> {
                            realmObject.url = image.photo;
                            realmObject.data = responseData.body;
                        });
                    }
                })
                .execute();
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
                        RealmDatabaseManager realmDatabaseManager = RealmDatabaseManager.getInstance();
                        realmDatabaseManager.update(() -> {
                            RealmResults<ImageData> results = RealmDatabaseManager.getInstance().get(ImageData.class)
                                    .equalTo("url", image.photo)
                                    .findAll();
                            for (ImageData realmObject : results) {
                                realmObject.url = image.photo;
                                realmObject.data = responseData.body;
                            }
                        });
                    }
                })
                .execute();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
