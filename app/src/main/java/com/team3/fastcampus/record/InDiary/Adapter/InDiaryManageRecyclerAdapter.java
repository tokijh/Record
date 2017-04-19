package com.team3.fastcampus.record.InDiary.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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

public class InDiaryManageRecyclerAdapter extends RecyclerView.Adapter<InDiaryManageRecyclerAdapter.InDiaryManageRecyclerViewHolder> {

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드
    Uri fileUri = null;

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

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionCameraSelect();
            }
        });

    }

    @Override
    public int getItemCount() {
        return 2;
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

    private void actionCameraSelect() {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
        alert_confirm.setMessage("Select Camera").setCancelable(false).setPositiveButton("camera",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //actionPhoto();
                    }
                }).setNegativeButton("gallery",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //actionGallery();
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }





}

