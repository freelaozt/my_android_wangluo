package com.fxd.wangluo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BlankFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private TextView fragmentTv1;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView rvSearch;
    private List<SearchBean> beanList;
    private SearchAdapter adapter;
    private LinearLayoutManager linearManager;
    private Request request;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private String errorMessage1 = "对不起，不能为空！";
    private String errorMessage2 = "对不起，没有相关数据！";

    public static BlankFragment newInstance(String param1) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            Log.d("onCreate: ", "mParam1: " + mParam1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentTv1 = (TextView) view.findViewById(R.id.fragment_tv1);

        fragmentTv1.setText(mParam1);

        initView(view);
    }

    private void initView(View v) {
        swipeToLoadLayout = (SwipeToLoadLayout) v.findViewById(R.id.swipeToLoadLayout);
        rvSearch = (RecyclerView) v.findViewById(R.id.swipe_target);
        beanList = new ArrayList<SearchBean>();
        //加布局管理
        linearManager = new LinearLayoutManager(getActivity());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSearch.setLayoutManager(linearManager);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        rvSearch.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
    public void onRefresh() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                //再次请求时，清空数据源,
                beanList = null;
                OkHttpClient client = new OkHttpClient();
                request = new Request.Builder()
                        .url(Constants.API.SEARCH_KEYWORD + mParam1 + "&p=0")
                        .get().build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getActivity(), "Info完善 上拉请求失败页面", Toast.LENGTH_SHORT).show();
                        swipeToLoadLayout.setRefreshing(true);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        final String json = response.body().string();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String regEx = "[\"`~!@#$%^&*()+=|{}';',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                                Pattern p = Pattern.compile(regEx);
                                Matcher m = p.matcher(json);
                                String s = m.replaceAll("").trim();
                                System.out.println("-------------true" + s.length());
                                if (s.equals("null")) {
                                    Toast.makeText(getActivity(), "没用搜索到", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Gson gson = new Gson();
                                JSONObject jsonObject;
                                String jsonData = null;
                                try {
                                    jsonObject = new JSONObject(json);
                                    jsonData = jsonObject.getString("data");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (beanList == null || beanList.size() == 0) {
                                    beanList = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
                                    }.getType());
                                } else {
                                    List<SearchBean> more = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
                                    }.getType());
                                    beanList.addAll(more);
                                    Log.i("lao", "1:" + beanList.size());
                                }

                                adapter = new SearchAdapter(getActivity(), beanList);
                                rvSearch.setAdapter(adapter);

                                adapter.setOnItemClickListener(new SearchAdapter.OnRecyclerViewItemClickListener() {
                                    @Override
                                    public void onItemClick(View view) {
                                        int i = rvSearch.getChildAdapterPosition(view);
                                        Toast.makeText(getActivity(), "Info" + beanList.get(i).getLinkHref(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse(beanList.get(i).getLinkHref());
                                        intent.setData(content_url);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onItemLongClick(View view) {
                                        int i = rvSearch.getChildAdapterPosition(view);
                                        Toast.makeText(getContext(), "Info" + i, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
                swipeToLoadLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
//                再次请求时，清空数据源,
//                beanList = null;
                OkHttpClient client = new OkHttpClient();
                request = new Request.Builder()
                        .url(Constants.API.SEARCH_KEYWORD + mParam1 + "&p=" + beanList.size())
                        .get().build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //完善 请求失败页面
                        Toast.makeText(getActivity(), "Info完善 请求失败页面", Toast.LENGTH_SHORT).show();
                        swipeToLoadLayout.setLoadingMore(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String json = response.body().string();

                        //整除有余数为0
                        if (beanList.size() % 10 == 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Gson gson = new Gson();
                                    JSONObject jsonObject;
                                    String jsonData = null;
                                    try {
                                        jsonObject = new JSONObject(json);
                                        jsonData = jsonObject.getString("data");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (beanList == null || beanList.size() == 0) {
                                        beanList = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
                                        }.getType());
                                    } else {
                                        List<SearchBean> more = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
                                        }.getType());
                                        beanList.addAll(more);
                                        Log.i("lao", "1:" + beanList.size());
                                    }
                                    if (beanList.size() > 10) {
                                        linearManager.scrollToPositionWithOffset(beanList.size() - 10, 0);
                                        linearManager.setStackFromEnd(true);//从列表底部展示
                                    }

                                    adapter = new SearchAdapter(getActivity(), beanList);
                                    rvSearch.setAdapter(adapter);

                                    adapter.setOnItemClickListener(new SearchAdapter.OnRecyclerViewItemClickListener() {
                                        @Override
                                        public void onItemClick(View view) {
                                            int i = rvSearch.getChildAdapterPosition(view);
                                            Toast.makeText(getActivity(), "Info" + beanList.get(i).getLinkHref(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent();
                                            intent.setAction("android.intent.action.VIEW");
                                            Uri content_url = Uri.parse(beanList.get(i).getLinkHref());
                                            intent.setData(content_url);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onItemLongClick(View view) {
                                            int i = rvSearch.getChildAdapterPosition(view);
                                            Toast.makeText(getActivity(), "Info" + i, Toast.LENGTH_SHORT).show();
//
                                        }
                                    });

                                    swipeToLoadLayout.setLoadingMore(false);
                                    Log.i("lao", "run2: " + beanList.size());
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "没有数据", Toast.LENGTH_SHORT).show();
                                    swipeToLoadLayout.setLoadingMore(false);
                                }
                            });

                        }

                    }

                });
                swipeToLoadLayout.setLoadingMore(false);
            }
        }, 1000);
    }
}
