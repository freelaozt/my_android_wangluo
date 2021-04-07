package com.fxd.wangluo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fxd.wangluo.Bean.UserBean;
import com.fxd.wangluo.SQLite.DataBaseHandler;
import com.fxd.wangluo.utils.IconManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IncludeActivity extends AppCompatActivity implements View.OnClickListener {

    private IconManager iconManager;
    private String iconPath = "fonts/ionicons.ttf";
    private EditText edIncludeTitle, edIncludeHref, edIncludeDes, edIncludeLabel;
    private Button btnSubmitInclude;
    private DataBaseHandler handlerDB;
    private UserBean userInfoDB;
    private List<UserBean> checkUserList;
    private Context context;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private String errorMessage1 = "对不起，不能为空！";
    private String errorMessage2 = "对不起，没有相关数据！";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_include);
        context = IncludeActivity.this;
        handlerDB = new DataBaseHandler(context);

        iconManager = new IconManager();

        ((TextView) findViewById(R.id.icon_throw_tv))
                .setTypeface(iconManager.getIcons(iconPath, context));

        initView();
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//设置返回箭头显示
        actionBar.setTitle("收录");
        edIncludeTitle = (EditText) findViewById(R.id.ed_include_title);
        edIncludeHref = (EditText) findViewById(R.id.ed_include_href);
        edIncludeDes = (EditText) findViewById(R.id.ed_include_des);
        edIncludeLabel = (EditText) findViewById(R.id.ed_include_label);
        btnSubmitInclude = (Button) findViewById(R.id.btn_submit_include);

        btnSubmitInclude.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        int userId;
        //先判断用户是否登录
        checkUserList = handlerDB.checkUserTable();
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        if (checkUserList != null) {
            Log.d("run", "run: 不是null");
            userInfoDB = handlerDB.queryData();
            userId = userInfoDB.getNetUserId();
        } else {
            Log.d("run", "run: 我是是null");
            userId = 0;
        }
//            }
//        });
        //否 userId=0;
        String regEx = "[\"`~!@#$%^&*()+=|{}';',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);

        String title = edIncludeTitle.getText().toString();
        String href = edIncludeHref.getText().toString();
        String des = edIncludeDes.getText().toString();
        Matcher m = p.matcher(edIncludeLabel.getText().toString());
        String label = m.replaceAll(";").trim().replace(" ", "");

        if (title.isEmpty() || href.isEmpty() || des.isEmpty() || label.isEmpty()) {
            customAlert(errorMessage1);
            return;
        }
        Log.d("a", "userId: " + userId + "title: " + title + "href: " + href + "des: " + des + "label: " + label);
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(href) && !TextUtils.isEmpty(des) && !TextUtils.isEmpty(label)) {
            String url = Constants.API.INSERT_LINK + "?a=ilink&ui=" + userId + "&t=" + title + "&h=" + href + "&d=" + des + "&l=" + label;
            requestData(url);
        } else {
            customAlert(errorMessage1);
        }
    }

    public void requestData(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(IncludeActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                cuSuccess(data);
            }
        });
    }

    public void cuSuccess(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (data.equals("添加成功")) {
                    customAlert(data);
                    edIncludeTitle.setText(null);
                    edIncludeHref.setText(null);
                    edIncludeDes.setText(null);
                    edIncludeLabel.setText(null);
                }
                if (data.equals("添加失败")) {
                    customAlert(data);
                }
            }
        });
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
                //搜索完，隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void customAlert(String errorMessage) {
        alert = null;
        builder = new AlertDialog.Builder(context);
        alert = builder.setIcon(R.drawable.ic_chat)
                .setTitle("系统提示：")
                .setMessage(errorMessage)
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "关闭", Toast.LENGTH_SHORT).show();
                    }
                }).create();             //创建AlertDialog对象
        alert.show();                    //显示对话框
    }

}
