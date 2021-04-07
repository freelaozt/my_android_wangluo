package com.fxd.wangluo.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.fxd.wangluo.Adapter.SearchAdapter;
import com.fxd.wangluo.Bean.SearchBean;
import com.fxd.wangluo.Bean.UserBean;
import com.fxd.wangluo.Constants;
import com.fxd.wangluo.utils.NetworkUtils;
import com.fxd.wangluo.R;
import com.fxd.wangluo.SQLite.DataBaseHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LabelInfoActivity extends AppCompatActivity implements OnLoadMoreListener, OnRefreshListener {

    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView rvSwipeTarget;
    private List<SearchBean> searchBeanList;
    private SearchAdapter mAdapter;
    private LinearLayoutManager linearManager;
    private Request request;
    private ProgressDialog progressDialog;
    private String[] array;
    private DataBaseHandler handlerDB;
    private UserBean userInfoDB;
    private String labelStr;
    private List<UserBean> checkUserList;
    private static final String TAG = "LabelInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_info);

        if (NetworkUtils.isConnected(this)) {
            Log.d("LabelInfoActivity", "onCreate: true");
        } else {
            Log.d("LabelInfoActivity", "onCreate: false");
        }

        handlerDB = new DataBaseHandler(getApplicationContext());
        userInfoDB = handlerDB.queryData();
//        初始化控件
        initView();
        Intent intent = getIntent();
        String k = intent.getStringExtra("keyLabel");
        array = k.split(";");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//设置返回箭头显示
        actionBar.setTitle(array[0]);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        checkUserList = handlerDB.checkUserTable();
        if (checkUserList != null) {
            labelStr = userInfoDB.getNetLabel();
            Log.d("+", "=====: " + labelStr + "=====" + array[0]);

//            判断当前是否收藏
            String[] list = labelStr.split(";");
            for (int i = 0; i < list.length; i++) {
                Log.d(TAG, "1: " + list[i].length() + "2: " + array[0].length());
                if (list[i].equals(array[0])) {
                    fab.setImageResource(R.drawable.ic_favorite);
                    Log.d("labelStr", "contains: you");
                    fab.setTag("" + R.drawable.ic_favorite);
                    break;
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_border);
                    Log.d("labelStr", "contains: mei");
                    fab.setTag("" + R.drawable.ic_favorite_border);
                }
            }

            final Object tag = fab.getTag();
            final String rTag = (String) tag;

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NetworkUtils.isConnected(getApplicationContext())) {
                        // 判断类型
                        if (rTag.equals("" + R.drawable.ic_favorite)) {
                            Log.d("labelStr", "contains: ic_favorite");
                            fab.setImageResource(R.drawable.ic_favorite_border);
                            String newLabelStr = labelStr.replace(array[0] + ";", "");
                            //更新本地SQLite + 服务器MySQL
                            Log.d("labelStr", "contains: ic_favorite_border" + newLabelStr);
                            SubmitData(Constants.API.UPDATE_LABEL + "?a=ulabel&ud=" + userInfoDB.getNetUserId() + "&ul=" + newLabelStr);
                            handlerDB.updateData(userInfoDB.getNetUserId(), newLabelStr);
                            Snackbar.make(view, "取消关注", Snackbar.LENGTH_LONG)
                                    .show();
                        } else if (rTag.equals("" + R.drawable.ic_favorite_border)) {
                            Log.d("labelStr", "contains: ic_favorite_border");
                            fab.setImageResource(R.drawable.ic_favorite);
                            String newLabelStr = array[0] + ";" + labelStr.replace("none", "");
                            //更新本地SQLite + 服务器MySQL
                            Log.d("labelStr", "contains: ic_favorite_border" + newLabelStr);
                            SubmitData(Constants.API.UPDATE_LABEL + "?a=ulabel&ud=" + userInfoDB.getNetUserId() + "&ul=" + newLabelStr);
                            handlerDB.updateData(userInfoDB.getNetUserId(), newLabelStr);
                            Snackbar.make(view, "已关注", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(LabelInfoActivity.this, "无网络", Toast.LENGTH_SHORT).show();
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(view, "没有网络，不可更改", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                }
            });
        } else {
            fab.setImageResource(R.drawable.ic_favorite_border);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "还没登录，不可关注", Snackbar.LENGTH_LONG)
                            .show();
                }
            });
        }

    }

    //    提交爱心
    private void SubmitData(String URL) {
        OkHttpClient client = new OkHttpClient();
        request = new Request.Builder()
                .url(URL)
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LabelInfoActivity.this, data, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        rvSwipeTarget = (RecyclerView) findViewById(R.id.swipe_target);

        searchBeanList = new ArrayList<SearchBean>();
        //加布局管理
        linearManager = new LinearLayoutManager(getApplicationContext());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSwipeTarget.setLayoutManager(linearManager);
        rvSwipeTarget.setNestedScrollingEnabled(false);//解决滑动不流畅

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
    protected void onStart() {
        super.onStart();
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
    public void onRefresh() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                //再次请求时，清空数据源,
                searchBeanList = null;
                //根据参数 加载数据
                String URL = Constants.API.SEARCH_KEYWORD + array[0] + "&p=0";
                initData(URL);
                swipeToLoadLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.API.SEARCH_KEYWORD + array[0] + "&p=" + searchBeanList.size();
                initData(URL);
                swipeToLoadLayout.setLoadingMore(false);
            }
        }, 1000);
    }

    private void initData(String URL) {

        OkHttpClient client = new OkHttpClient();
        request = new Request.Builder()
                .url(URL)
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (json.equals("none") || json.length() == 4 || json.equals("null")) {
                            Toast.makeText(getApplicationContext(), "没有相关数据", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (searchBeanList != null) {
                            if (searchBeanList.size() >= 10) {
                                if (searchBeanList.size() % 10 == 0) {
                                    linearManager.scrollToPositionWithOffset(searchBeanList.size(), 0);
                                    Log.d(TAG, "run: ---------" + searchBeanList.size());
                                } else {
                                    linearManager.scrollToPositionWithOffset(searchBeanList.size() - searchBeanList.size() % 10, 0);
                                    Log.d(TAG, "run: ---------" + (searchBeanList.size() - searchBeanList.size() % 10));
                                }
                                linearManager.setStackFromEnd(true);//从列表底部展示
                            }
                        }

                        gsonJson(json);
                        mAdapter = new SearchAdapter(getApplicationContext(), searchBeanList);
                        rvSwipeTarget.setAdapter(mAdapter);

                        mAdapter.setOnItemClickListener(new SearchAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                int i = rvSwipeTarget.getChildAdapterPosition(view);
                                Toast.makeText(getApplicationContext(), "Info" + searchBeanList.get(i).getLinkHref(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(searchBeanList.get(i).getLinkHref());
                                intent.setData(content_url);
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view) {
                                int i = rvSwipeTarget.getChildAdapterPosition(view);
                                Toast.makeText(getApplicationContext(), "Info" + searchBeanList.get(i).getLinkId(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                                intent.putExtra("netId", String.valueOf(searchBeanList.get(i).getLinkId()));
                                Log.d("run", "netId: " + searchBeanList.get(i).getLinkId());
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
    }

    public void gsonJson(String json) {
        Gson gson = new Gson();
        JSONObject jsonObject;
        String jsonData = null;
        try {
            jsonObject = new JSONObject(json);
            jsonData = jsonObject.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (searchBeanList == null || searchBeanList.size() == 0) {
            searchBeanList = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
            }.getType());
        } else {
            List<SearchBean> more = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
            }.getType());
            searchBeanList.addAll(more);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //点击返回箭头结束activity
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
