package com.team3.fastcampus.record.InDiary.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team3.fastcampus.record.InDiary.Model.Image;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.LocationPicker;
import com.team3.fastcampus.record.Util.RealmDatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimkyuwan on 2017. 4. 18..
 */

public class InDiaryManageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CARD_IMAGE = 0;
    public static final int CARD_ADD = 1;

    private List<ImageExtend> datas;

    private Context context;
    private InDiaryManageListCallback inDiaryManageListCallback;

    public InDiaryManageRecyclerAdapter(Context context, InDiaryManageListCallback inDiaryManageListCallback) {
        this.context = context;
        this.inDiaryManageListCallback = inDiaryManageListCallback;
        datas = new ArrayList<>();

        addImageAdd();
    }

    public Image get(int postion) {
        if (datas.get(postion).type == CARD_IMAGE) {
            return datas.get(postion).image;
        }
        return null;
    }

    public void add(Image image) {
        datas.add(datas.size() - 1, new ImageExtend(CARD_IMAGE, image));
        this.notifyDataSetChanged();
    }

    public void addImageAdd() {
        datas.add(new ImageExtend(CARD_ADD, null));
        this.notifyDataSetChanged();
    }

    public void set(List<Image> images) {
        datas.clear();
        addImageAdd();
        for (Image image : images) {
            add(image);
        }
    }

    public void removeItem(int position) {
        datas.remove(position);
        if (datas.size() == 0) {
            addImageAdd();
        }
        this.notifyDataSetChanged();
    }

    private void setCardImage(RecyclerView.ViewHolder holder, int position) {
        CardImageHolder cardImageHolder = (CardImageHolder) holder;

        cardImageHolder.position = position;
        String location = datas.get(position).image.location;
        cardImageHolder.tv_location.setText((location == null || "".equals(location)) ? "장소 선택" : location);
        Glide.with(context)
                .load(datas.get(position).image.photo)
                .placeholder(R.drawable.no_photo)
                .into(cardImageHolder.imageView);
    }

    private void setCardAdd(RecyclerView.ViewHolder holder, int position) {
        CardAddHodler cardAddHodler = (CardAddHodler) holder;

        cardAddHodler.position = position;
    }

    private void setLocation(TextView tv_location, int position) {
        new LocationPicker(context).show(
                (address, location) -> {
                    String locationStr = address.getCountryName() + " " + address.getAdminArea() + " " + address.getLocality() + " " + address.getFeatureName();
                    Image image = datas.get(position).image;
                    if (image != null) {
                        RealmDatabaseManager.getInstance().update(() -> {
                            image.gpsLatitude = String.valueOf(location.latitude);
                            image.gpsLongitude = String.valueOf(location.longitude);
                            image.location = locationStr;
                        });
                    }
                    tv_location.setText("".equals(locationStr) ? "장소 선택" : locationStr);
                }
        );
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CARD_IMAGE:
                return new InDiaryManageRecyclerAdapter.CardImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_in_diary_manage_list_item, parent, false));
            case CARD_ADD:
                return new InDiaryManageRecyclerAdapter.CardAddHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_in_diary_manage_list_item_add, parent, false));
            default:
                throw new RuntimeException("There is no type that matches");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case CARD_IMAGE:
                setCardImage(holder, position);
                break;
            case CARD_ADD:
                setCardAdd(holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).type;
    }

    public class CardImageHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tv_location;

        int position;

        public CardImageHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            imageView.setOnClickListener(cardImageHolderOnClickListener);
            tv_location.setOnClickListener(cardImageHolderOnClickListener);
        }

        View.OnClickListener cardImageHolderOnClickListener = v -> {
            switch (v.getId()) {
                case R.id.imageView:
                    inDiaryManageListCallback.onItemClick(position);
                    break;
                case R.id.tv_location:
                    setLocation(tv_location, position);
                    break;
            }
        };
    }

    public class CardAddHodler extends RecyclerView.ViewHolder {

        View view;

        int position;

        public CardAddHodler(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(cardAddHolderOnClickListener);
        }

        View.OnClickListener cardAddHolderOnClickListener = v -> {
            inDiaryManageListCallback.onAddClick();
        };
    }

    public class ImageExtend {
        int type;
        Image image;

        public ImageExtend(int type, Image image) {
            this.type = type;
            this.image = image;
        }
    }

    public interface InDiaryManageListCallback {
        void onItemClick(int position);

        void onAddClick();
    }
}
