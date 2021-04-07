package com.fxd.wangluo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * @author S.Shahini
 * @since 10/19/16
 */

public class FragmentD extends Fragment {
    private View v;
    private DataBaseHandler handler;
    private IconManager iconManager;
    private String iconPath = "fonts/ionicons.ttf";
    private LinearLayout linearSettingLogout;
    private List<UserBean> checkUserList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new DataBaseHandler(getActivity());
        checkUserList = handler.checkUserTable();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (checkUserList != null) {
                    Log.d("run", "run: 不是null");
                    Toast.makeText(getActivity(), "不是null", Toast.LENGTH_SHORT).show();
                    v = inflater.inflate(R.layout.fragment_d, container, false);
                } else {
                    Log.d("run", "run: 我是是null");
                    Toast.makeText(getActivity(), "我是是null", Toast.LENGTH_SHORT).show();
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

            ((TextView) view.findViewById(R.id.icon_chevron_right_tv))
                    .setTypeface(iconManager.getIcons(iconPath, getActivity()));
            final ImageButton imageButton = (ImageButton) view.findViewById(R.id.ib_head_img);


            TextView tvUserName = (TextView) view.findViewById(R.id.tv_user_name);

            final UserBean userInfoDB = handler.queryData();
            if (userInfoDB.getNetHeadImg().length()==4){
                Picasso.with(getContext()).load("https://tvax1.sinaimg.cn/crop.654.0.852.852.180/005YEkeXly8fl414g6pzxj31kw0o0tl3.jpg")
                        .transform(new CropCircleTransformation())
                        .into(imageButton);
            }else{
                Picasso.with(getContext()).load(userInfoDB.getNetHeadImg())
                        .transform(new CropCircleTransformation())
                        .into(imageButton);
            }
            tvUserName.setText(userInfoDB.getNetUserName());
            System.out.println("getLocalId=========" + userInfoDB.getLocalId());
            System.out.println("getNetUserId=========" + userInfoDB.getNetUserId());
            System.out.println("getNetHeadImg=========" + userInfoDB.getNetHeadImg().length());
            System.out.println("getNetUserName=========" + userInfoDB.getNetUserName());
            System.out.println("getNetEmail=========" + userInfoDB.getNetEmail());
            System.out.println("getNetPassWord=========" + userInfoDB.getNetPassWord());
            System.out.println("getNetLabel=========" + userInfoDB.getNetLabel());

            linearSettingLogout = (LinearLayout) view.findViewById(R.id.linear_setting_logout);
            RelativeLayout  lUserLabel = (RelativeLayout) view.findViewById(R.id.linear_user_label);
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
                    handler.deleteData();
                    Intent intent = new Intent(getActivity(), Login2Activity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "退出当前账号", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("view", "onViewCreated:是null ");

            Button btnLogin = (Button) view.findViewById(R.id.btn_user_log_in);
            Button btnRegistered = (Button) view.findViewById(R.id.btn_user_registered);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Login2Activity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "LOgin", Toast.LENGTH_SHORT).show();
                }
            });

            btnRegistered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Register2Activity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "REG", Toast.LENGTH_SHORT).show();
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
//        final UserBean userBean = handler.queryData();
//        int userId = userBean.getNetUserId();
//        final String collectAuthor = userBean.getNetUserName();
//        System.out.println("userName：" + collectAuthor);

//        final UserBean list = handler.queryData();
//        tvUserName.setText(list.getNetUserName());
//        System.out.println("========="+list.getLocalId());
//        System.out.println("========="+list.getNetUserId());
//        System.out.println("========="+list.getNetUserName());
//        System.out.println("========="+list.getNetPassWord());
//        System.out.println("========="+list.getNetEmail());
    }
}
