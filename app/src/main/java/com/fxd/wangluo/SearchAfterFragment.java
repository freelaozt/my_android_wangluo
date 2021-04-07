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
import com.fxd.wangluo.Adapter.SearchAdapter;
import com.fxd.wangluo.Bean.SearchBean;
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

public class SearchAfterFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private TextView fragmentTv1;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView rvSwipeTarget;
    private List<SearchBean> beanList;
    private SearchAdapter mAdapter;
    private LinearLayoutManager linearManager;
    private Request request;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private String errorMessage1 = "对不起，不能为空！";
    private String errorMessage2 = "对不起，没有相关数据！";
    private String regEx = "[\"`~!@#$%^&*()+=|{}';',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    public static SearchAfterFragment newInstance(String param1) {
        SearchAfterFragment fragment = new SearchAfterFragment();
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
        return inflater.inflate(R.layout.fragment_search_after, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        fragmentTv1 = (TextView) view.findViewById(R.id.fragment_tv1);
//        fragmentTv1.setText(mParam1);

        initView(view);
    }

    private void initView(View v) {
        swipeToLoadLayout = (SwipeToLoadLayout) v.findViewById(R.id.swipeToLoadLayout);
        rvSwipeTarget = (RecyclerView) v.findViewById(R.id.swipe_target);
        beanList = new ArrayList<SearchBean>();
        //加布局管理
        linearManager = new LinearLayoutManager(getActivity());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSwipeTarget.setLayoutManager(linearManager);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        rvSwipeTarget.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                String url = Constants.API.SEARCH_KEYWORD + mParam1 + "&p=0";

                beanList = null; //再次请求时，清空数据源,
                requestData(url);

                swipeToLoadLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = Constants.API.SEARCH_KEYWORD + mParam1 + "&p=" + beanList.size();

                requestData(url);

                swipeToLoadLayout.setLoadingMore(false);
            }
        }, 1000);
    }

    public void requestData(String URL) {
        OkHttpClient client = new OkHttpClient();
        request = new Request.Builder()
                .url(URL)
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String data = response.body().string();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String regEx = "[\"`~!@#$%^&*()+=|{}';',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                            Pattern p = Pattern.compile(regEx);
                            Matcher m = p.matcher(data);
                            String s = m.replaceAll("").trim();
                            System.out.println("-------------true" + s.length());

                            if (s.equals("null")||s.equals("none")|| s.length() == 4) {
                                Toast.makeText(getActivity(), "没有相关数据", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            gsonParseJson(data);

                            if (beanList.size() > 10) {
                                if (beanList.size() % 10 == 0) {
                                    linearManager.scrollToPositionWithOffset(beanList.size() - 10, 0);
                                } else {
                                    linearManager.scrollToPositionWithOffset(beanList.size() - beanList.size() % 10, 0);
                                }
                                linearManager.setStackFromEnd(true);//从列表底部展示
                            }

                            mAdapter = new SearchAdapter(getActivity(), beanList);
                            rvSwipeTarget.setAdapter(mAdapter);

                            mAdapter.setOnItemClickListener(new SearchAdapter.OnRecyclerViewItemClickListener() {
                                @Override
                                public void onItemClick(View view) {
                                    int i = rvSwipeTarget.getChildAdapterPosition(view);
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri content_url = Uri.parse(beanList.get(i).getLinkHref());
                                    intent.setData(content_url);
                                    startActivity(intent);
                                }

                                @Override
                                public void onItemLongClick(View view) {
                                    int i = rvSwipeTarget.getChildAdapterPosition(view);
                                }
                            });
                        }
                    });
                }else{
                    return;
                }

            }
        });
    }

    public void gsonParseJson(String data) {
        Gson gson = new Gson();
        JSONObject jsonObject;
        String jsonData = null;
        try {
            jsonObject = new JSONObject(data);
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
        }
    }

}
