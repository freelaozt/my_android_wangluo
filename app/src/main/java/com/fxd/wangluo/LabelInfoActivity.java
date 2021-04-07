package com.fxd.wangluo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class LabelInfoActivity extends AppCompatActivity {

    private RecyclerView rvKeyInfo;
    private List<SearchBean> beanList;
    private SearchAdapter mAdapter;
    private LinearLayoutManager linearManager;
    private Request request;
    private ProgressDialog progressDialog;
    private String[] array;
    private DataBaseHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_info);
        progressDialog = new ProgressDialog(LabelInfoActivity.this);
        progressDialog.setMessage("加载数据中...");
        progressDialog.show();
        handler = new DataBaseHandler(getApplicationContext());
        final UserBean userInfoDB = handler.queryData();
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
        String labelStr = userInfoDB.getNetLabel();
        Log.d("+", "=====: " + labelStr + "=====" + array[0]);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (labelStr.contains(array[0])) {
            fab.setImageResource(R.drawable.ic_favorite);
            Log.d("labelStr", "contains: you");
            fab.setTag("" + R.drawable.ic_favorite);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_border);
            Log.d("labelStr", "contains: mei");
            fab.setTag("" + R.drawable.ic_favorite_border);
        }

        final Object tag = fab.getTag();
        final String rTag = (String) tag;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断类型
                if (rTag.equals("" + R.drawable.ic_favorite)) {
                    Log.d("labelStr", "contains: ic_favorite");
                    fab.setImageResource(R.drawable.ic_favorite_border);

                    String labelStr = userInfoDB.getNetLabel().replace(array[0] + ";", "");
                    //更新本地SQLite + 服务器MySQL
                    Log.d("labelStr", "contains: ic_favorite_border" + labelStr);
                    Snackbar.make(view, "取消关注", Snackbar.LENGTH_LONG)
                            .show();
                } else if (rTag.equals("" + R.drawable.ic_favorite_border)) {
                    Log.d("labelStr", "contains: ic_favorite_border");
                    fab.setImageResource(R.drawable.ic_favorite);
                    String labelStr = array[0] + ";" + userInfoDB.getNetLabel();
                    //更新本地SQLite + 服务器MySQL
                    Log.d("labelStr", "contains: ic_favorite_border" + labelStr);
                    Snackbar.make(view, "已关注", Snackbar.LENGTH_LONG)
                            .show();
                }


            }
        });

        //根据参数 加载数据
        initData(array[0]);
    }

    private void initData(String k) {

        beanList = new ArrayList<SearchBean>();

        String keyWord = k;
        OkHttpClient client = new OkHttpClient();
        request = new Request.Builder()
                .url(Constants.API.SEARCH_KEYWORD + keyWord + "&p=" + beanList.size())
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gsonJson(json);
                        mAdapter = new SearchAdapter(getApplicationContext(), beanList);
                        rvKeyInfo.setAdapter(mAdapter);

                        mAdapter.setOnItemClickListener(new SearchAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                int i = rvKeyInfo.getChildAdapterPosition(view);
                                Toast.makeText(getApplicationContext(), "Info" + beanList.get(i).getLinkHref(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(beanList.get(i).getLinkHref());
                                intent.setData(content_url);
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view) {
                                int i = rvKeyInfo.getChildAdapterPosition(view);
                                Toast.makeText(getApplicationContext(), "Info" + beanList.get(i).getLinkId(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                                intent.putExtra("netId", String.valueOf(beanList.get(i).getLinkId()));
                                Log.d("run", "netId: " + beanList.get(i).getLinkId());
                                startActivity(intent);
                            }
                        });
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void initView() {
        rvKeyInfo = (RecyclerView) findViewById(R.id.swipe_target);

        beanList = new ArrayList<SearchBean>();
        //加布局管理
        linearManager = new LinearLayoutManager(getApplicationContext());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvKeyInfo.setLayoutManager(linearManager);
        rvKeyInfo.setNestedScrollingEnabled(false);//解决滑动不流畅
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
            beanList = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
            }.getType());
        } else {
            List<SearchBean> more = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
            }.getType());
            beanList.addAll(more);
//            Log.i("lao", "1:" + beanList.size());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            //点击返回箭头结束activity
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
