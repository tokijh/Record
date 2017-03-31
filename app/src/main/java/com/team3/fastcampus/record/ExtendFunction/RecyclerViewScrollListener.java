package com.team3.fastcampus.record.ExtendFunction;

/**
 * Created by tokijh on 2017. 3. 31..
 */

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * RecylerView의 ScrollListener의 확장
 *
 * toolbar와 같은것을 일정 스크롤 이상 했을경우 표시, 숨기기
 */
public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private static final int STD_HIDE = 20;
    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int mFirstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        if (mFirstVisibleItem == 0) {
            if (!mControlsVisible) {
                onShow();
                mControlsVisible = true;
            }
        } else {
            if (mScrolledDistance > STD_HIDE && mControlsVisible) {
                onHide();
                mControlsVisible = false;
                mScrolledDistance = 0;
            } else if (mScrolledDistance < -STD_HIDE && !mControlsVisible) {
                onShow();
                mControlsVisible = true;
                mScrolledDistance = 0;
            }
        }

        if ((mControlsVisible && dy > 0) || (!mControlsVisible && dy < 0)) {
            mScrolledDistance += dy;
        }
    }
    public abstract void onHide();
    public abstract void onShow();
}
