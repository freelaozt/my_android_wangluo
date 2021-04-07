package com.fxd.wangluo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxd.wangluo.fragment.BaseNavPagerFragment;
import com.fxd.wangluo.fragment.BaseNavigationFragment;
import com.fxd.wangluo.fragment.HomeRecyclerFragment;
import com.fxd.wangluo.utils.IconManager;
import com.fxd.wangluo.utils.NetworkUtils;

/**
 * Created by LaoZhang on 2018/3/9.
 */
public class HomeFragment extends BaseNavPagerFragment {
    private IconManager iconManager;
    private static final String TAG = "HomeFragment";

    public static BaseNavigationFragment newInstance() {
        BaseNavigationFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nav_twitter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iconManager = new IconManager();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected String[] getTitles() {
        return new String[]{"热门:qHot:3", "动画:qAnimation:1",
                "影视:qTv:1", "特摄:qTokusatsu:4",
                "小说:qFiction:1", "图片:qPic:2", "技术:qProgram:0"};
    }

    @Override
    protected Fragment getFragment(int position) {
        String[] title = getTitles()[position].split(":");
        Fragment fragment = null;
        Log.d(TAG, "getFragment: " + title[0]);
        Log.d(TAG, "getFragment: " + title[1]);
        Log.d(TAG, "getFragment: " + title[2]);
        if (title[0].equals(title[0])) {
            fragment = HomeRecyclerFragment.newInstance(HomeRecyclerFragment.TYPE_LINEAR, title[1], Integer.parseInt(title[2]));
        }
        return fragment;
    }

}
