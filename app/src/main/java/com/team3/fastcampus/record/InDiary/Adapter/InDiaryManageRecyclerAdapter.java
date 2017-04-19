package com.team3.fastcampus.record.InDiary.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.team3.fastcampus.record.R;

import java.util.ArrayList;

/**
 * Created by kimkyuwan on 2017. 4. 18..
 */

public class InDiaryManageRecyclerAdapter extends RecyclerView.Adapter<InDiaryManageRecyclerAdapter.InDiaryManageRecyclerViewHolder> {

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드
    Uri fileUri = null;

    ArrayList<String> url;

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


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionCameraSelect();
            }
        });

    }

    @Override
    public int getItemCount() {
        return url.size();
    }

    public class InDiaryManageRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public InDiaryManageRecyclerViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.in_diary_manage_item_image);

        }
    }

    private void actionCameraSelect() {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
        alert_confirm.setMessage("Select Camera").setCancelable(false).setPositiveButton("camera",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actionPhoto();
                    }
                }).setNegativeButton("gallery",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actionGallery();
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    private void actionGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 외부저장소에있는 이미지만 가져오기위한 필터링

        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY);
        Activity activity = (Activity) context;
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY);

    }



    private void actionPhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            fileUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        //startActivityForResult(intent, REQ_CAMERA);
        Activity activity = (Activity) context;
        activity.startActivityForResult(intent, REQ_CAMERA);

    }




}

