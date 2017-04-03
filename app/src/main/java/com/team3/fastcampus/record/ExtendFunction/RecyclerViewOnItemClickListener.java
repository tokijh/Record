package com.team3.fastcampus.record.ExtendFunction;

/**
 * Created by tokijh on 2017. 3. 31..
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * RecylerView의 SimpleOnItemTouchListener의 확장
 *
 * OnItemClickListerner
 */
public class RecyclerViewOnItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    private OnItemClickListener onItemClickListener;
    private GestureDetector gestureDetector;

    public RecyclerViewOnItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && onItemClickListener != null && gestureDetector.onTouchEvent(e)) {
            onItemClickListener.onItemClick(child, rv.getChildAdapterPosition(child));
            return true;
        }
        return false;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);
    }
}
