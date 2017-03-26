package com.team3.fastcampus.record.Diary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team3.fastcampus.record.InDiary.InDiaryViewFragment;
import com.team3.fastcampus.record.R;

/**
 * Diary를 보여주기 위한 메인뷰
 */
public class DiaryViewFragment extends Fragment {

    private View view;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    // Fragment
    private DiaryListViewFragment diaryListViewFragment;
    private DiaryMapViewFragment diaryMapViewFragment;
    private InDiaryViewFragment inDiaryViewFragment;

    // Adaper
    private DiaryViewPagerAdapter diaryViewPagerAdapter;

    // Connector with Activity
    private DiaryViewInterface diaryViewInterface;


    public DiaryViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_diary_view, container, false);

        initView();

        initAdapter();

        initListener();

        initFragment();

        // #3 테스트용 소스 View가 로드되면 바로 InDiaryViewFragment 를 실행한다.
        diaryViewInterface.showContent(inDiaryViewFragment);

        return view;
    }

    private void initView() {
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    private void initAdapter() {
        diaryViewPagerAdapter = new DiaryViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(diaryViewPagerAdapter);
    }

    private void initListener() {
        // 1. Pager가 변경 되었을 때 Tab를 바꿔주는 리스너
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // 2. Tab이 변경 되었을 때 페이지를 바꿔주는 리스너
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private void initFragment() {
        diaryListViewFragment = new DiaryListViewFragment();
        diaryMapViewFragment = new DiaryMapViewFragment();
        inDiaryViewFragment = new InDiaryViewFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DiaryViewInterface) {
            diaryViewInterface = (DiaryViewInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DiaryViewInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        diaryViewInterface = null;
    }

    class DiaryViewPagerAdapter extends FragmentStatePagerAdapter {

        public static final int TAB_COUNT = 2;

        public DiaryViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0: // Show List
                    fragment = diaryListViewFragment;
                    break;
                case 1: // Show Map
                    fragment = diaryMapViewFragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }

    public interface DiaryViewInterface {
        void showContent(Fragment fragment);
    }
}
