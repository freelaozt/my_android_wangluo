package com.fxd.wangluo.trash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fxd.wangluo.utils.IconManager;
import com.fxd.wangluo.R;
import com.fxd.wangluo.SearchActivity;
import com.fxd.wangluo.View.LabelInfoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LaoZhang on 2018/2/14.
 */
public class FragmentA extends Fragment implements View.OnClickListener {

    private IconManager iconManager;
    private String iconPath = "fonts/ionicons.ttf";
    private LinearLayout mLinearSearchBox;
    private TextView tvSearchBox;
    private LinearLayout mLinearAddView;
    private List<String> titleList;//放置标题的集合
    private String orStr = "DVD;网盘;1080P;无字幕;生肉;乃木坂;生田雄;没有心跳的少女;一人之下;刀使的巫女;七大罪;居家飲酒趣;皇帝圣印战记;魔法使的新娘;桥本奈奈美;";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iconManager = new IconManager();
        ((TextView) view.findViewById(R.id.tv_icon_search))
                .setTypeface(iconManager.getIcons(iconPath, getContext()));

        mLinearSearchBox = (LinearLayout) view.findViewById(R.id.linear_search_box);
        tvSearchBox = (TextView) view.findViewById(R.id.tv_search_box);
        mLinearAddView = (LinearLayout) view.findViewById(R.id.linear_add_view);
        //要添加view的容器
        titleList = new ArrayList<>();
        
        mLinearSearchBox.setOnClickListener(this);
        tvSearchBox.setOnClickListener(this);
       
        initData();
    }

    /**
     * 处理数据,可以是服务器请求过来的,也可以是本地的
     */
    private void initData() {
        String[] array = orStr.split(";");
        for (int i = 0; i < array.length; i++) {
//            Log.d("initData: ", "initData: " + array[i]);
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
                    Intent intent = new Intent(getActivity(), LabelInfoActivity.class);
                    intent.putExtra("keyLabel", titleList.get(finalI));
                    startActivity(intent);
                }
            });
            //把所有动态创建的view都添加到容器里面
            mLinearAddView.addView(view);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_search_box:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
            case R.id.tv_search_box:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
        }
    }
    
}