package com.fxd.wangluo.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fxd.wangluo.Adapter.LabelAdapter2;
import com.fxd.wangluo.Bean.LabelBean;
import com.fxd.wangluo.Bean.UserBean;
import com.fxd.wangluo.R;
import com.fxd.wangluo.SQLite.DataBaseHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserLabelActivity extends AppCompatActivity {
    private RecyclerView rvLabel;
    private List<LabelBean> labelBeanList;
    private LabelAdapter2 mLabelAdapter;
    private String labelStr = "数码宝贝系列;数码暴龙系列;剧场版;原盘;BD;DVD;在线;网盘;Digital Monster;三语;生肉;熟肉;无字幕;有字幕";
    private DataBaseHandler handlerDB;
    private List<UserBean> checkUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_label);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//设置返回箭头显示
        actionBar.setTitle("标签");

        handlerDB = new DataBaseHandler(getApplicationContext());
        checkUserList = handlerDB.checkUserTable();
        if (checkUserList == null) {
            //未登录
            findViewById(R.id.include_no).setVisibility(View.VISIBLE);
            TextView tv = (TextView) findViewById(R.id.tv_found);
            rvLabel.setVisibility(View.GONE);
            tv.setText("还没登录 没什么展示的");
            return;
        } else {
            //已登录
            final UserBean userInfoDB = handlerDB.queryData();
            Log.d("UserLabelActivity", "userInfoDB.getNetLabel(): " + userInfoDB.getNetLabel());
            Log.d("UserLabelActivity", "userInfoDB.getNetLabel(): " + userInfoDB.getNetLabel().length());
            rvLabel = (RecyclerView) findViewById(R.id.swipe_target);

            if (userInfoDB.getNetLabel().length() == 0 || userInfoDB.getNetLabel().equals("none")) {
                findViewById(R.id.include_no).setVisibility(View.VISIBLE);
                TextView tv = (TextView) findViewById(R.id.tv_found);
                rvLabel.setVisibility(View.GONE);
                tv.setText("好像没有唉");
            } else {
                String[] array = userInfoDB.getNetLabel().split(";");
                String json = "";
                for (int i = 0; i < array.length; i++) {
                    json += "{\"label_id\": \"" + i + "\", \"label_title\": \"" + array[i] + "\"},";
                }
                String jsonData = "{\"data\":[" + json.substring(0, json.length() - 1) + "]}";

                labelBeanList = new ArrayList<LabelBean>();
                //加布局管理
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
                rvLabel.setLayoutManager(staggeredGridLayoutManager);

                gsonJson2(jsonData);

                mLabelAdapter = new LabelAdapter2(getApplicationContext(), labelBeanList);
                rvLabel.setAdapter(mLabelAdapter);

                mLabelAdapter.setmOnItemClickListener(new LabelAdapter2.OnRVItemClickListener() {
                    @Override
                    public void onItemClick(View view) {
                        int i = rvLabel.getChildAdapterPosition(view);
                        Toast.makeText(getApplicationContext(), "" + labelBeanList.get(i).getLabelTitle(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LabelInfoActivity.class);
                        intent.putExtra("keyLabel", labelBeanList.get(i).getLabelTitle());
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view) {

                    }
                });
            }
        }
    }

    public void gsonJson2(String json) {
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String jsonData = jsonObject.getString("data");
            if (labelBeanList == null || labelBeanList.size() == 0) {
                labelBeanList = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                }.getType());
            } else {
                List<LabelBean> more = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                }.getType());
                labelBeanList.addAll(more);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
