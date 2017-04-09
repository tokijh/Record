package com.team3.fastcampus.record.Diary.Adapter;

/**
 * Created by tokijh on 2017. 3. 31..
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team3.fastcampus.record.Diary.Modal.Diary;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Logger;

import java.util.ArrayList;
import java.util.List;

public class DiaryViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CARD_DIARY = 0;
    public static final int CARD_ADD = 1;

    private Context context;

    private List<DiaryExtend> diaries;

    public DiaryViewRecyclerAdapter(Context context) {
        this.context = context;
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

    public Diary get(int position) {
        return diaries.get(position).diary;
    }

    private void setCardDiary(RecyclerView.ViewHolder holder, int position) {
        CardDiaryHolder diaryViewHolder = (CardDiaryHolder) holder;
        Diary diary = diaries.get(position).diary;
        diaryViewHolder.position = position;
        diaryViewHolder.tv_title.setText(diary.title);
        diaryViewHolder.tv_date.setText(diary.date);
        diaryViewHolder.tv_location.setText(diary.location);
        Glide.with(context)
                .load(diary.image)
                .placeholder(R.drawable.night)
                .into(diaryViewHolder.iv_image);
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
            Logger.e("TAG", v.getId() + " ididididi");
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

    }

    private void cardDiaryMenuCreate(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.diary_list_item_option);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_edit:
                    break;
                case R.id.nav_delete:
                    break;
            }
            return false;
        });
        popup.show();
    }

    public class CardAddHolder extends RecyclerView.ViewHolder {

        public CardAddHolder(View itemView) {
            super(itemView);
        }
    }

    class DiaryExtend {
        int type;
        Diary diary;
        public DiaryExtend(int type, Diary diary) {
            this.type = type;
            this.diary = diary;
        }
    }
}
