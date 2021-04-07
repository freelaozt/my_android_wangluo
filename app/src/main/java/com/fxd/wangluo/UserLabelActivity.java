package com.fxd.wangluo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class UserLabelActivity extends AppCompatActivity {
    private RecyclerView rvLabel;
    private List<LabelBean> beanList;
    private LabelAdapter mAdapter;
    private String labelStr = "数码宝贝系列;数码暴龙系列;剧场版;原盘;BD;DVD;在线;网盘;Digital Monster;三语;生肉;熟肉;无字幕;有字幕";
    private DataBaseHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_label);
        handler = new DataBaseHandler(getApplicationContext());
        final UserBean userInfoDB = handler.queryData();

        if (userInfoDB.getNetLabel().length() == 4) {
            TextView tv = (TextView) findViewById(R.id.tv_user_label);
            tv.setVisibility(View.VISIBLE);
        } else {
            String[] array = userInfoDB.getNetLabel().split(";");
            String json = "";
            for (int i = 0; i < array.length; i++) {
                json += "{\"label_id\": \"" + i + "\", \"label_title\": \"" + array[i] + "\"},";
            }
            String jsonData = "[" + json.substring(0, json.length() - 1) + "]";

            rvLabel = (RecyclerView) findViewById(R.id.swipe_target);

            beanList = new ArrayList<LabelBean>();
            //加布局管理
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
            rvLabel.setLayoutManager(staggeredGridLayoutManager);

            Gson gson = new Gson();
            if (beanList == null || beanList.size() == 0) {
                beanList = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                }.getType());
            } else {
                List<LabelBean> more = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                }.getType());
                beanList.addAll(more);
            }

            mAdapter = new LabelAdapter(getApplicationContext(), beanList);
            rvLabel.setAdapter(mAdapter);

            mAdapter.setmOnItemClickListener(new LabelAdapter.OnRVItemClickListener() {
                @Override
                public void onItemClick(View view) {
                    int i = rvLabel.getChildAdapterPosition(view);
                    Toast.makeText(getApplicationContext(), "" + beanList.get(i).getLabelTitle(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), LabelInfoActivity.class);
                    intent.putExtra("keyLabel", beanList.get(i).getLabelTitle());
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view) {

                }
            });
        }

    }

}
