package com.team3.fastcampus.record.InDiary.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.team3.fastcampus.record.R;

/**
 * Created by kimkyuwan on 2017. 4. 18..
 */

public class InDiaryManageRecyclerAdapter extends RecyclerView.Adapter<InDiaryManageRecyclerAdapter.InDiaryManageRecyclerViewHolder>{

    String content = "test";
    Context context;
    int itemLayout;

    public InDiaryManageRecyclerAdapter(Context context, int itemLayout) {
        this.context = context;
        this.itemLayout = itemLayout;
    }

    @Override
    public InDiaryManageRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        InDiaryManageRecyclerViewHolder rv = new InDiaryManageRecyclerViewHolder(view);

        return rv;
    }

    @Override
    public void onBindViewHolder(InDiaryManageRecyclerViewHolder holder, int position) {

        holder.et.setText(content);

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class InDiaryManageRecyclerViewHolder extends RecyclerView.ViewHolder {

    EditText et;
    ImageView imageView;

    public InDiaryManageRecyclerViewHolder(View itemView) {
        super(itemView);

        et = (EditText) itemView.findViewById(R.id.in_diary_manage_item_et);
        imageView = (ImageView) itemView.findViewById(R.id.in_diary_manage_item_image);

    }
}

}
