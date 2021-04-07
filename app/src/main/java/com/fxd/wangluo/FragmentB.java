package com.fxd.wangluo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentB extends Fragment implements View.OnClickListener {
    private IconManager iconManager;
    private String iconPath = "fonts/ionicons.ttf";
    private EditText edIncludeTitle, edIncludeHref, edIncludeDes, edIncludeLabel;
    private Button btnSubmitInclude;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private String errorMessage1 = "对不起，不能为空！";
    private String errorMessage2 = "对不起，没有相关数据！";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iconManager = new IconManager();
        ((TextView) view.findViewById(R.id.icon_throw_tv))
                .setTypeface(iconManager.getIcons(iconPath, getContext()));

        initView(view);

    }

    public void initView(View v) {

        edIncludeTitle = (EditText) v.findViewById(R.id.ed_include_title);
        edIncludeHref = (EditText) v.findViewById(R.id.ed_include_href);
        edIncludeDes = (EditText) v.findViewById(R.id.ed_include_des);
        edIncludeLabel = (EditText) v.findViewById(R.id.ed_include_label);
        btnSubmitInclude = (Button) v.findViewById(R.id.btn_submit_include);

        btnSubmitInclude.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //先判断用户是否登录
        //否 userId=0;String userName = userBean.getNetUserName();

        String title = edIncludeTitle.getText().toString();
        String href = edIncludeHref.getText().toString();
        String des = edIncludeDes.getText().toString();
        String label = edIncludeLabel.getText().toString();
        int userId = 0;
        customAlert(errorMessage1);
        if (title.isEmpty() || href.isEmpty() || des.isEmpty() || label.isEmpty()) {
            customAlert(errorMessage1);
            return;
        }

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(href) && !TextUtils.isEmpty(des) && !TextUtils.isEmpty(label)) {
            String url = Constants.API.FEEDBACK + "?userId=" + userId + "&title=" + title + "&href=" + href + "&des=" + des + "&label=" + label;
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
                customAlert("请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                cuSuccess(data);
            }
        });
    }

    public void cuSuccess(final String data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void customAlert(String errorMessage) {
        alert = null;
        builder = new AlertDialog.Builder(getActivity());
        alert = builder.setIcon(R.mipmap.ic_chat)
                .setTitle("系统提示：")
                .setMessage(errorMessage)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
                    }
                }).create();             //创建AlertDialog对象
        alert.show();                    //显示对话框
    }
}
