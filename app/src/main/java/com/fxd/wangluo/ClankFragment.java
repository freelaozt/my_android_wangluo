package com.fxd.wangluo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ClankFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";
    private String mParam2;
    private TextView fragmentTv2;
    private LinearLayout mLinearAddView;
    private List<String> titleList;//放置标题的集合
    private String orStr = "1;DVD;网盘;1080P;无字幕;生肉;生田雄;七大罪;居家飲酒趣;皇帝圣印战记;魔法使的新娘;桥本奈奈美;";

    private MyListener myListener;//②作为属性定义
    //①定义回调接口
    public interface MyListener{
        public void sendContent(String info);
    }

    public static ClankFragment newInstance(String param2) {
        ClankFragment fragment = new ClankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam2 = getArguments().getString(ARG_PARAM2);
            Log.d("onCreate: ", "mParam2: " + mParam2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clank, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentTv2 = (TextView) view.findViewById(R.id.fragment_tv2);

        fragmentTv2.setText(mParam2);

        mLinearAddView = (LinearLayout) view.findViewById(R.id.linear_add_view);
        //要添加view的容器
        titleList = new ArrayList<>();

        initData();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myListener = (MyListener) getActivity();
    }


    /**
     * 处理数据,可以是服务器请求过来的,也可以是本地的
     */
    private void initData() {
        String[] array = orStr.split(";");
        for (int i = 0; i < array.length; i++) {
            Log.d("initData: ", "initData: " + array[i]);
            titleList.add(array[i]);
        }
        //数据拿到之后去根据数据去动态添加View
        addView();
    }

    /**
     * 动态添加的具体实现
     */
    private void addView() {
        //ivList集合有几个元素就添加几个
        for (int i = 0; i < titleList.size(); i++) {
            //首先引入要添加的View
            View view = View.inflate(getActivity(), R.layout.item_label_1, null);
            //找到里面需要动态改变的控件
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            //给控件赋值
            tvTitle.setText(titleList.get(i));
            //设置每个View的点击事件
            final int finalI = i;//由于OnClick里面拿不到i,所以需要重新赋值给一个final对象
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), LabelInfoActivity.class);
//                    intent.putExtra("keyLabel", titleList.get(finalI));
//                    startActivity(intent);
                    myListener.sendContent(titleList.get(finalI));//将内容进行回传
                    Toast.makeText(getActivity(), "点击了" + titleList.get(finalI), Toast.LENGTH_SHORT).show();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.layFrame, BlankFragment.newInstance(titleList.get(finalI)));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            //把所有动态创建的view都添加到容器里面
            mLinearAddView.addView(view);
        }
    }

    //设置默认的
//    private void setSecondFragment() {
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.replace(R.id.layFrame, BlankFragment.newInstance(keyWord));
//        transaction.commit();
//    }
}
