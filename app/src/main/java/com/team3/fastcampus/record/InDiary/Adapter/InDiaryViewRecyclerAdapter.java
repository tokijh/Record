package com.team3.fastcampus.record.InDiary.Adapter;

/**
 * Created by tokijh on 2017. 3. 31..
 */

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.team3.fastcampus.record.InDiary.Model.InDiary;
import com.team3.fastcampus.record.R;

import java.util.ArrayList;
import java.util.List;

public class InDiaryViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CARD_INDIARY = 0;
    public static final int CARD_ADD = 1;

    private Context context;

    private List<InDiaryExtend> inDiaries;

    public InDiaryViewRecyclerAdapter(Context context) {
        this.context = context;
        inDiaries = new ArrayList<>();
        addInDiaryAdd();
    }

    public void add(InDiary inDiary) {
        inDiaries.add(new InDiaryExtend(CARD_INDIARY, inDiary));
        addInDiaryAdd();
        this.notifyDataSetChanged();
    }

    public void addInDiaryAdd() {
        inDiaries.add(new InDiaryExtend(CARD_ADD, null));
        this.notifyDataSetChanged();
    }

    public void set(List<InDiary> inDiaries) {
        this.inDiaries.clear();
        addInDiaryAdd();
        for (InDiary inDiary : inDiaries) {
            add(inDiary);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        set(new ArrayList<>());
    }

    public InDiary get(int position) {
        return inDiaries.get(position).inDiary;
    }

    private void setCardDiary(RecyclerView.ViewHolder holder, int position) {
        CardInDiaryHolder inDiaryViewHolder = (CardInDiaryHolder) holder;
        InDiary inDiary = inDiaries.get(position).inDiary;
        inDiaryViewHolder.position = position;
//        inDiaryViewHolder.tv_title.setText(inDiary.title);
        inDiaryViewHolder.tv_date.setText(inDiary.created_date);
//        inDiaryViewHolder.tv_content.setText(inDiary.content);
        inDiaryViewHolder.inDiaryListImageViewPagerAdapter.set(inDiaries.get(position).inDiary.photo_list);
    }

    private void setCardAdd(RecyclerView.ViewHolder holder, int position) {
        CardAddHolder cardAddHolder = (CardAddHolder) holder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CARD_INDIARY:
                return new CardInDiaryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_in_diary_list_view_item, parent, false));
            case CARD_ADD:
                return new CardAddHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_in_diary_list_view_item_add, parent, false));
            default:
                throw new RuntimeException("There is no type that matches");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case CARD_INDIARY:
                setCardDiary(holder, position);
                break;
            case CARD_ADD:
                setCardAdd(holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return inDiaries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return inDiaries.get(position).type;
    }

    public class CardInDiaryHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_more;
        TextView tv_date;
        TextView tv_content;

        InDiaryListImageViewPagerAdapter inDiaryListImageViewPagerAdapter;

        int position;

        public CardInDiaryHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.in_diary_list_view_title);
            tv_more = (TextView) itemView.findViewById(R.id.in_diary_list_view_more);
            tv_date = (TextView) itemView.findViewById(R.id.in_diary_list_view_date);
            tv_content = (TextView) itemView.findViewById(R.id.in_diary_list_view_content);
            ViewPager imageViewPager = (ViewPager) itemView.findViewById(R.id.in_diary_list_view_imageViewPager);

            inDiaryListImageViewPagerAdapter = new InDiaryListImageViewPagerAdapter(context);
            imageViewPager.setAdapter(inDiaryListImageViewPagerAdapter);

            itemView.setOnClickListener(cardDiaryHolderOnClickListener);
            tv_more.setOnClickListener(cardDiaryHolderOnClickListener);
        }

        View.OnClickListener cardDiaryHolderOnClickListener = v -> {
            switch (v.getId()) {
                case -1:
                    cardInDiaryClicked(position);
                    break;
                case R.id.in_diary_list_view_more:
                    cardInDiaryMenuCreate(v, position);
                    break;
            }
        };
    }

    private void cardInDiaryClicked(int position) {

    }

    private void cardInDiaryMenuCreate(View v, int position) {
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

    class InDiaryExtend {
        int type;
        InDiary inDiary;
        public InDiaryExtend(int type, InDiary inDiary) {
            this.type = type;
            this.inDiary = inDiary;
        }
    }
}
