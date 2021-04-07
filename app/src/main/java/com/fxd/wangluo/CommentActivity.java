package com.fxd.wangluo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {

    private View view_custom;
    private String netId;
    private DataBaseHandler handler;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        // Navigation Icon 要設定在 setSupportActionBar 才有作用
        actionBar.setDisplayHomeAsUpEnabled(true);//设置返回箭头显示
        actionBar.setTitle("");//不显示应用程序名
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String linkId = intent.getStringExtra("netId");

        handler = new DataBaseHandler(getApplicationContext());
        final UserBean userInfoDB = handler.queryData();

        System.out.println("netId：" + linkId);
        TextView tvTest = (TextView) findViewById(R.id.link_test_tv);
        tvTest.setText("linkId:"+linkId);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //初始化Builder
        builder = new AlertDialog.Builder(this);

        //加载自定义的那个View,同时设置下
        final LayoutInflater inflater = this.getLayoutInflater();
        view_custom = inflater.inflate(R.layout.view_dialog_custom, null, false);
        builder.setView(view_custom);
        builder.setCancelable(false);
        alert = builder.create();

        view_custom.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        view_custom.findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "访问博客" + linkId, Toast.LENGTH_SHORT).show();
                final EditText ed = (EditText) view_custom.findViewById(R.id.ed_include_comment);
                String commentContent = ed.getText().toString();
                if (commentContent==""||commentContent==null){
                    return;
                }
                int userId = userInfoDB.getNetUserId();
                Log.d("on", "on开: "+"ld=" + linkId + "&c=" + commentContent + "&ud=" + userId);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Constants.API.INSERT_COMMENT + "?a=icomment&ld=" + linkId + "&c=" + commentContent + "&ud=" + userId)
                        .get().build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        System.out.print("==============" + data);
                        alert.dismiss();
                    }
                });
            }
        });

        view_custom.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "对话框已关闭~", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
