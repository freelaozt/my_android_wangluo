package com.fxd.wangluo;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author S.Shahini
 * @since 10/19/16
 */

public class FragmentF extends Fragment implements OnLoadMoreListener, OnRefreshListener {
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView rvLabel;
    private List<LabelBean> beanList;
    private LabelAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        rvLabel = (RecyclerView) view.findViewById(R.id.swipe_target);

        beanList = new ArrayList<LabelBean>();

        //加布局管理
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        rvLabel.setLayoutManager(staggeredGridLayoutManager);

        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        rvLabel.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setLoadingMore(false);
            }
        }, 3000);
    }

    @Override
    public void onRefresh() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                beanList = null;//重新加载清除
                //解析本地Json文件
                String filename = "label.json";
                String json = getJson(getContext(), filename);
                Gson gson = new Gson();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String jsonData = jsonObject.getString("data");
                    if (beanList == null || beanList.size() == 0) {
                        beanList = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                        }.getType());
                    } else {
                        List<LabelBean> more = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                        }.getType());
                        beanList.addAll(more);
                    }
//                    Log.i("lao", "1:" + beanList.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter = new LabelAdapter(getContext(), beanList);
                rvLabel.setAdapter(mAdapter);

                mAdapter.setmOnItemClickListener(new LabelAdapter.OnRVItemClickListener() {
                    @Override
                    public void onItemClick(View view) {
                        int i = rvLabel.getChildAdapterPosition(view);
                        Toast.makeText(getContext(), "" + beanList.get(i).getLabelTitle(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), LabelInfoActivity.class);
                        intent.putExtra("keyLabel", beanList.get(i).getLabelTitle());
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view) {

                    }
                });

                swipeToLoadLayout.setRefreshing(false);
            }
        }, 3000);
    }

    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
