package com.team3.fastcampus.record.Diary.Adapter;

/**
 * Created by tokijh on 2017. 3. 31..
 */

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team3.fastcampus.record.Diary.DiaryManageActivity;
import com.team3.fastcampus.record.Diary.Model.Diary;
import com.team3.fastcampus.record.R;

import java.util.ArrayList;
import java.util.List;

public class DiaryViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CARD_DIARY = 0;
    public static final int CARD_ADD = 1;

    private Context context;
    private DiaryListCallback diaryListCallback;

    private List<DiaryExtend> diaries;

    public DiaryViewRecyclerAdapter(Object object) {
        if (object instanceof DiaryListCallback) {
            diaryListCallback = (DiaryListCallback) object;
        } else {
            throw new RuntimeException(object.toString() + " must implement DiaryListCallback");
        }
        this.context = ((Fragment) object).getContext();
        diaries = new ArrayList<>();
        addDiaryAdd();
    }

    public void add(Diary diary) {
        if (diaries.size() > 0)
            diaries.add(diaries.size() - 1, new DiaryExtend(CARD_DIARY, diary));
        this.notifyDataSetChanged();
    }

    public void addDiaryAdd() {
        diaries.add(new DiaryExtend(CARD_ADD, null));
        this.notifyDataSetChanged();
    }

    public void set(List<Diary> diaries) {
        this.diaries.clear();
        for (Diary diary : diaries) {
            this.diaries.add(new DiaryExtend(CARD_DIARY, diary));
        }
        this.diaries.add(new DiaryExtend(CARD_ADD, null));
        this.notifyDataSetChanged();
    }

    public void clear() {
        set(new ArrayList<>());
    }

    public Diary get(int position) {
        return diaries.get(position).diary;
    }

    private void setCardDiary(RecyclerView.ViewHolder holder, int position) {
        CardDiaryHolder diaryViewHolder = (CardDiaryHolder) holder;

        Diary diary = diaries.get(position).diary;
        diaryViewHolder.position = position;
        diaryViewHolder.tv_title.setText(diary.title);
        diaryViewHolder.tv_date.setText(diary.start_date + " ~ " + diary.end_date);
        if (diary.cover_image != null) {
            Glide.with(context)
                    .load(Uri.parse(diary.cover_image))
                    .placeholder(R.drawable.night)
                    .into(diaryViewHolder.iv_image);
        }
    }

    private void setCardAdd(RecyclerView.ViewHolder holder, int position) {
        CardAddHolder cardAddHolder = (CardAddHolder) holder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CARD_DIARY:
                return new CardDiaryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_diary_list_view_item, parent, false));
            case CARD_ADD:
                return new CardAddHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_diary_list_view_item_add, parent, false));
            default:
                throw new RuntimeException("There is no type that matches");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case CARD_DIARY:
                setCardDiary(holder, position);
                break;
            case CARD_ADD:
                setCardAdd(holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return diaries.get(position).type;
    }

    public class CardDiaryHolder extends RecyclerView.ViewHolder {

        TextView tv_more;
        TextView tv_title;
        TextView tv_date;
        TextView tv_location;
        ImageView iv_image;

        int position;

        public CardDiaryHolder(View itemView) {
            super(itemView);
            tv_more = (TextView) itemView.findViewById(R.id.diary_card_more);
            tv_title = (TextView) itemView.findViewById(R.id.diary_card_title);
            tv_date = (TextView) itemView.findViewById(R.id.diary_card_date);
            tv_location = (TextView) itemView.findViewById(R.id.diary_card_location);
            iv_image = (ImageView) itemView.findViewById(R.id.diary_card_image);

            itemView.setOnClickListener(cardDiaryHolderOnClickListener);
            tv_more.setOnClickListener(cardDiaryHolderOnClickListener);
        }

        View.OnClickListener cardDiaryHolderOnClickListener = v -> {
            switch (v.getId()) {
                case -1:
                    cardDiaryClicked(position);
                    break;
                case R.id.diary_card_more:
                    cardDiaryMenuCreate(v, position);
                    break;
            }
        };
    }

    private void cardDiaryClicked(int position) {
        diaryListCallback.onItemClick(diaries.get(position).diary);
    }

    private void cardDiaryMenuCreate(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.diary_list_item_option);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_delete:
                    diaryListCallback.onDiaryManage(diaries.get(position).diary.pk, DiaryManageActivity.MODE_DELETE);
                    break;
            }
            return false;
        });
        popup.show();
    }

    public class CardAddHolder extends RecyclerView.ViewHolder {

        public CardAddHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(cardAddHolderOnClickListener);
        }

        View.OnClickListener cardAddHolderOnClickListener = v -> {
            diaryListCallback.onDiaryManage(-1l, DiaryManageActivity.MODE_CREATE);
        };
    }

    class DiaryExtend {
        int type;
        Diary diary;
        public DiaryExtend(int type, Diary diary) {
            this.type = type;
            this.diary = diary;
        }
    }

    public interface DiaryListCallback {
        void onItemClick(Diary diary);

        void onDiaryManage(long pk, int mode);
    }
}
