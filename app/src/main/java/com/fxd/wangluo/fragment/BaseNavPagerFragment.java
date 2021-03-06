package com.fxd.wangluo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fxd.wangluo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LaoZhang on 2018/3/9.
 */
public abstract class BaseNavPagerFragment extends BaseNavigationFragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Adapter mAdapter;
    private static final String TAG = "BaseNavPagerFragment";

    public BaseNavPagerFragment() {
        // Required empty public constructor
    }

    protected abstract String[] getTitles();

    protected abstract Fragment getFragment(int position);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new Adapter(getChildFragmentManager());

        String[] titles = getTitles();
        for (int i = 0; i < titles.length; i++) {
            String[] classify = titles[i].split(":");
            Log.d(TAG, "onCreate: " + classify[0]);
            Log.d(TAG, "onCreate: " + classify[1]);
            Log.d(TAG, "onCreate: " + classify[2]);
            mAdapter.addFragment(getFragment(i), classify[0]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_nav_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    protected static class Adapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<CharSequence> titles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, CharSequence title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
