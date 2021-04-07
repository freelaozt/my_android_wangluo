package com.fxd.wangluo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxd.wangluo.Bean.UserBean;
import com.fxd.wangluo.SQLite.DataBaseHandler;
import com.fxd.wangluo.View.UserLabelActivity;
import com.fxd.wangluo.utils.IconManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author S.Shahini
 * @since 10/19/16
 */

public class UserFragment extends Fragment {
    private View v;
    private IconManager iconManager;
    private String iconPath = "fonts/ionicons.ttf";
    private LinearLayout linearSettingLogout;
    private List<UserBean> checkUserList;
    private DataBaseHandler handlerDB;
    private UserBean userInfoDB;

    private View view_custom;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private String errorMessage1 = "对不起，不能为空！";
    private String errorMessage2 = "对不起，没有相关数据！";
    private static final String TAG = "UserFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        handlerDB = new DataBaseHandler(getActivity());
        checkUserList = handlerDB.checkUserTable();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (checkUserList != null) {
                    Log.d("run", "run: 不是null");
                    v = inflater.inflate(R.layout.fragment_d, container, false);
                } else {
                    Log.d("run", "run: 我是是null");
                    v = inflater.inflate(R.layout.fragment_d_2, container, false);
                }
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (checkUserList != null) {
            Log.d("view", "onViewCreated:不是null ");
            handlerDB = new DataBaseHandler(getActivity());
            userInfoDB = handlerDB.queryData();

            ((TextView) view.findViewById(R.id.icon_chevron_right_tv))
                    .setTypeface(iconManager.getIcons(iconPath, getActivity()));
            final ImageButton imageButton = (ImageButton) view.findViewById(R.id.ib_head_img);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.show();
                }
            });

            System.out.println("getLocalId=========" + userInfoDB.getLocalId());
            System.out.println("getNetUserId=========" + userInfoDB.getNetUserId());
            System.out.println("getNetHeadImg=========" + userInfoDB.getNetHeadImg().length());
            System.out.println("getNetUserName=========" + userInfoDB.getNetUserName());
            System.out.println("getNetEmail=========" + userInfoDB.getNetEmail());
            System.out.println("getNetPassWord=========" + userInfoDB.getNetPassWord());
            System.out.println("getNetLabel=========" + userInfoDB.getNetLabel());

            TextView tvUserName = (TextView) view.findViewById(R.id.tv_user_name);

            if (userInfoDB.getNetHeadImg().length() == 4||userInfoDB.getNetHeadImg().length() == 0) {
                Picasso.with(getContext()).load("http://ww1.sinaimg.cn/large/ad305317gy1fqcjzaq5kjj20jf0gq3ys.jpg")
                        .transform(new CropCircleTransformation())
                        .into(imageButton);
            } else {
                Picasso.with(getContext()).load(userInfoDB.getNetHeadImg())
                        .transform(new CropCircleTransformation())
                        .into(imageButton);
            }
            tvUserName.setText(userInfoDB.getNetUserName());

            linearSettingLogout = (LinearLayout) view.findViewById(R.id.linear_setting_logout);
            RelativeLayout lUserLabel = (RelativeLayout) view.findViewById(R.id.linear_user_label);
            lUserLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), UserLabelActivity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "我的关注", Toast.LENGTH_SHORT).show();
                }
            });

            linearSettingLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /**1.点击后，删除SQLite登录数据（空表）
                     * 2.返回到登录界面
                     */
                    handlerDB.deleteData();
                    Toast.makeText(getActivity(), "退出当前账号", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            });

            //初始化Builder
            builder = new AlertDialog.Builder(getActivity());

            //加载自定义的那个View,同时设置下
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            view_custom = inflater.inflate(R.layout.dialog_custom_view, null, false);
            builder.setView(view_custom);
            builder.setCancelable(false);
            alert = builder.create();

            TextView tvCuDialogTitle = (TextView) view_custom.findViewById(R.id.tv_custom_dialog_title);
            tvCuDialogTitle.setText("修改头像");
            EditText edCuDialogContent = (EditText) view_custom.findViewById(R.id.ed_custom_dialog_content);
            edCuDialogContent.setHint("只能粘贴图片(jpg/png)链接结尾哦");

            view_custom.findViewById(R.id.btn_custom_dialog_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText ed = (EditText) view_custom.findViewById(R.id.ed_custom_dialog_content);
                    String content = ed.getText().toString();
                    if (content.equals("")) {
                        return;
                    }
                    int size = content.lastIndexOf("."); // 变量size的值是
                    String substr = content.substring(size + 1); //获取字符串，此时substr值为lo word
                    Log.d(TAG, "onClick: " + size);
                    Log.d(TAG, "onClick: " + substr);
                    String orStr = "jpg;JPG;jpeg;JPEG;png;PNG;";
                    if (orStr.contains(substr)) {
                        handlerDB.update2Data(userInfoDB.getNetUserId(), content);
                        //本地+MYSQL
                        RequestData(Constants.API.UPDATE_LABEL + "?a=uheadimg&ud=" + userInfoDB.getNetUserId() + "&hi=" + content);

                        Picasso.with(getContext()).load(content)
                                .transform(new CropCircleTransformation())
                                .into(imageButton);

                        alert.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "不支持该链接图片类型", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            view_custom.findViewById(R.id.btn_custom_dialog_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
        } else {
            Log.d("view", "onViewCreated:是null ");

            Button btnLogin = (Button) view.findViewById(R.id.btn_user_log_in);
            Button btnRegistered = (Button) view.findViewById(R.id.btn_user_registered);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });

            btnRegistered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), RegisterActivity.class);
                    startActivity(intent);
                }
            });

        }
        /**
         * 1.先判断本地SQLite
         * 1.1有User信息 显示我的界面
         *  1.1.1 显示登录名 显示退出当前账号 我的关注（标签） 头像（支持外链接）
         *
         * 1.2没有user信息 显示登录界面
         * 2.1点击注册 检验用户名是否存在 邮箱是否存在 密码 提交
         *      2.1.1 提交success 先清空本地SQLite 填入一份user信息存入本地SQLite
         *      2.1.2 提交failure 请重输入
         * 2.2点击登录 检验邮箱 密码 提交
         *      2.2.1 提交success 先清空本地SQLite 从网络下载一份同样信息存入本地SQLite
         *      2.2.2 提交failure 请重输入
         */
        /**1.点击后，删除SQLite登录数据（空表）
         * 2.返回到登录界面
         */


        /**1.获取当前用户的登录id
         * 2.装入id请求数据
         */

    }

    private void RequestData(String URL) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get().build();
        Log.d("LabelInfoActivity", "URL:" + URL);
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
