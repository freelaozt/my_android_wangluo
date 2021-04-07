package com.fxd.wangluo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddiehl.timesincetextview.TimeSinceTextView;
import com.fxd.wangluo.Bean.SearchBean;
import com.fxd.wangluo.utils.IconManager;
import com.fxd.wangluo.R;
import com.fxd.wangluo.View.LabelInfoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LaoZhang on 2018/3/11. SearchBeforeFragment
 */
public class SearchItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SearchBean> mData;
    private LinearLayout mLinear;
    private List<String> titleList;//放置标题的集合

    public SearchItemAdapter(Context context, List<SearchBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    /**
     * 创建Holder
     *
     * @param parent   理解为item的根ViewGroup，item的子控件加载在其中
     * @param viewType item类型，据viewType创建不同ViewHolder，加载不同的类型的item
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.from(mContext).inflate(R.layout.item_label_1, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    /**
     * 绑定ViewHolder
     *
     * @param holder 在onCreateViewHolder()方法中，创建的ViewHolder
     * @param i      item对应的DataList数据源集合的postion
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {
        if (holder instanceof MyViewHolder) {

            ((MyViewHolder) holder).tvTitle.setText(mData.get(i).getLinkTitle());
            String myHexColor = "#CC6666";
            ((MyViewHolder) holder).tvTitle.setTextColor(Color.parseColor(myHexColor));

            titleList = new ArrayList<>();
            //处理数据,可以是服务器请求过来的,也可以是本地的
            String[] array = mData.get(i).getLinkLabel().split(";");
            for (int p = 0; p < array.length; p++) {
                titleList.add(array[p]);
            }

            //数据拿到之后去根据数据去动态添加View
            //ivList集合有几个元素就添加几个
            for (int j = 0; j < titleList.size(); j++) {
                //首先引入要添加的View
                View view = View.inflate(mContext, R.layout.item_label, null);
                //找到里面需要动态改变的控件
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_title);
                //给控件赋值
                tvTitle.setText(titleList.get(j));
                //把所有动态创建的view都添加到容器里面
                view.bringToFront();
                mLinear.addView(view);
            }
            mLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LabelInfoActivity.class);
                    intent.putExtra("keyLabel", mData.get(i).getLinkLabel());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });

        }

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * 获取Item的数目
     *
     * @return 这方法返回值，是RecyclerView中
     * 实际item的数量。有些情况下，当增加了HeaderView或者FooterView后，需要注意考虑这个返回值
     */
    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    // 自定义ViewHolder初始化控件
    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvHref, tvDescribe, tvLabel;
        private TimeSinceTextView tvTime;
        private IconManager iconManager;
        private String iconPath = "fonts/ionicons.ttf";

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mLinear = (LinearLayout) itemView.findViewById(R.id.linear_add_view);
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);

        void onItemLongClick(View view);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    //短时间点击
    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view);
        }
    }

    //长时间点击
    @Override
    public boolean onLongClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemLongClick(view);
        }
        return false;
    }
}
