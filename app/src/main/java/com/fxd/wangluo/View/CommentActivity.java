package com.fxd.wangluo.View;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.fxd.wangluo.Adapter.CommentAdapter;
import com.fxd.wangluo.Bean.CommentBean;
import com.fxd.wangluo.Bean.UserBean;
import com.fxd.wangluo.Constants;
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

public class CommentActivity extends AppCompatActivity implements OnRefreshListener, OnLoadMoreListener {

    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView rvSwipeTarget;
    private View view_custom;
    private LinearLayoutManager linearManager;
    private List<CommentBean> beanList;
    private CommentAdapter mAdapter;
    private String linkId;
    private DataBaseHandler handlerDB;
    private List<UserBean> checkUserList;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private String errorMessage1 = "对不起，不能为空！";
    private String errorMessage2 = "对不起，没有相关数据！";
    private static final String TAG = "CommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        // Navigation Icon 要設定在 setSupportActionBar 才有作用
        actionBar.setDisplayHomeAsUpEnabled(true);//设置返回箭头显示
        actionBar.setTitle("评论");//不显示应用程序名
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        linkId = intent.getStringExtra("netId");

        handlerDB = new DataBaseHandler(getApplicationContext());
        final UserBean userInfoDB = handlerDB.queryData();

        System.out.println("netId：" + linkId);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        rvSwipeTarget = (RecyclerView) findViewById(R.id.swipe_target);

        //加布局管理
        linearManager = new LinearLayoutManager(getApplicationContext());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSwipeTarget.setLayoutManager(linearManager);
        beanList = new ArrayList<CommentBean>();
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

        //初始化Builder
        builder = new AlertDialog.Builder(this);

        //加载自定义的那个View,同时设置下
        final LayoutInflater inflater = this.getLayoutInflater();
        view_custom = inflater.inflate(R.layout.dialog_custom_view, null, false);
        builder.setView(view_custom);
        builder.setCancelable(false);
        alert = builder.create();

        TextView tvCuDialogTitle = (TextView) view_custom.findViewById(R.id.tv_custom_dialog_title);
        tvCuDialogTitle.setText("添加评论");
        EditText edCuDialogContent = (EditText) view_custom.findViewById(R.id.ed_custom_dialog_content);
        edCuDialogContent.setHint("留下一枝独秀，洒上一味香秀");
        edCuDialogContent.setHeight(150);
        edCuDialogContent.setBackgroundResource(R.drawable.edit_bg_textarea);
        edCuDialogContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});

        view_custom.findViewById(R.id.btn_custom_dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + linkId);
                final EditText ed = (EditText) view_custom.findViewById(R.id.ed_custom_dialog_content);
                String commentContent = ed.getText().toString();
                if (commentContent.equals("")) {
                    Toast.makeText(CommentActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    //先判断用户是否登录
                    checkUserList = handlerDB.checkUserTable();
                    int userId;
                    if (checkUserList != null) {
                        userId = userInfoDB.getNetUserId();
                    } else {
                        userId = 0;
                    }
                    Log.d("on", "on开: " + "ld=" + linkId + "&c=" + commentContent + "&ud=" + userId);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Constants.API.INSERT_COMMENT + "?a=icomment&ld=" + linkId + "&c=" + commentContent + "&ud=" + userId)
                            .get().build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CommentActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String data = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CommentActivity.this, data, Toast.LENGTH_SHORT).show();
                                    swipeToLoadLayout.setRefreshing(true);
                                    alert.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });

        view_custom.findViewById(R.id.btn_custom_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "对话框已关闭", Toast.LENGTH_SHORT).show();
                alert.dismiss();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
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
                String url = Constants.API.QUERY_COMMENT + "?a=qcomment&ld=" + linkId + "&p=0";
                //再次请求时，清空数据源,
                beanList = null;
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
                String url = Constants.API.QUERY_COMMENT + "?a=qcomment&ld=" + linkId + "&p=" + beanList.size();
                requestData(url);
                swipeToLoadLayout.setLoadingMore(false);
            }
        }, 1000);
    }

    public void requestData(String URL) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
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

                        gsonJson(json);

                        if (beanList.size() > 10) {
                            if (beanList.size() % 10 == 0) {
                                linearManager.scrollToPositionWithOffset(beanList.size() - 10, 0);
                            } else {
                                linearManager.scrollToPositionWithOffset(beanList.size() - beanList.size() % 10, 0);
                            }
                            linearManager.setStackFromEnd(true);//从列表底部展示
                        }

                        mAdapter = new CommentAdapter(getApplicationContext(), beanList);
                        rvSwipeTarget.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(new CommentAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                int i = rvSwipeTarget.getChildAdapterPosition(view);
                            }

                            @Override
                            public void onItemLongClick(View view) {
                                int i = rvSwipeTarget.getChildAdapterPosition(view);
                                // 得到剪贴板管理器
                                ClipboardManager clipManager = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("已复制",beanList.get(i).getComContent());
                                clipManager.setPrimaryClip(clip);
                                Toast.makeText(CommentActivity.this, "已复制", Toast.LENGTH_SHORT).show();
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
        if (beanList == null || beanList.size() == 0) {
            beanList = gson.fromJson(jsonData, new TypeToken<List<CommentBean>>() {
            }.getType());
        } else {
            List<CommentBean> more = gson.fromJson(jsonData, new TypeToken<List<CommentBean>>() {
            }.getType());
            beanList.addAll(more);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void customAlert(final String errorMessage) {

        alert = null;
        builder = new AlertDialog.Builder(this);
        alert = builder.setIcon(R.drawable.ic_chat)
                .setTitle("系统提示：")
                .setMessage(errorMessage)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CommentActivity.this, "取消了", Toast.LENGTH_SHORT).show();
                    }
                }).create();             //创建AlertDialog对象
        alert.show();                    //显示对话框             //显示对话框

    }
}
