package com.team3.fastcampus.record.InDiary;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team3.fastcampus.record.R;

/**
 * Created by kimkyuwan on 2017. 4. 18..
 */

public class InDiaryRecyclerAdapter extends RecyclerView.Adapter<InDiaryRecyclerAdapter.RecyclerViewHolder> {

    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드
    Uri fileUri = null;

    int itemLayout;
    String datas;
    Context context;
    ImageView imageView;

    public InDiaryRecyclerAdapter(String datas, int itemLayout, Context context) {
        this.itemLayout = itemLayout;
        this.context = context;
        this.datas = datas;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        RecyclerViewHolder rv = new RecyclerViewHolder(view);

        return rv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionCameraSelect();
            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        EditText et;
        ImageView imageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.in_diary_manage_item_image);
            et = (EditText) itemView.findViewById(R.id.in_diary_manage_item_et);
            et.setText(datas);

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

    private void actionPhoto() {
        Toast.makeText(context, "photo", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            fileUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        Glide.with(context).load(fileUri).into(imageView);
    }


    private void actionGallery() {
        Toast.makeText(context, "Gallery", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 외부저장소에있는 이미지만 가져오기위한 필터링

        if (intent.getData() != null) {
            fileUri = intent.getData();
            Glide.with(context).load(fileUri)
                    .into(imageView);

        }
    }




}
