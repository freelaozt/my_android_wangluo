package com.fxd.wangluo;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SearchBean> mData;
    private LinearLayout mLinear;
    private List<String> titleList;//放置标题的集合

    public SearchAdapter(Context context, List<SearchBean> data) {
        notifyDataSetChanged();
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
        View view = mInflater.from(mContext).inflate(R.layout.item_search, parent, false);

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
            ((MyViewHolder) holder).tvHref.setText(mData.get(i).getLinkHref());
            ((MyViewHolder) holder).tvDescribe.setText(mData.get(i).getLinkDescribe());
            ((MyViewHolder) holder).tvTime.setText(mData.get(i).getLinkTime());
            ((MyViewHolder) holder).tvLabel.setText(mData.get(i).getLinkLabel());
//            ((MyViewHolder) holder).tvCompany.setText(mData.get(i).getCompany());

            titleList = new ArrayList<>();
            //处理数据,可以是服务器请求过来的,也可以是本地的
            String[] array = mData.get(i).getLinkLabel().split(";");
            for (int p = 0; p < array.length; p++) {
//                Log.d("initData: ", "initData: " + array[p]);
                titleList.add(array[p]);
            }

            //数据拿到之后去根据数据去动态添加View
            //ivList集合有几个元素就添加几个
            for (int j = 0; j < titleList.size(); j++) {
                //首先引入要添加的View
                View view = View.inflate(mContext, R.layout.item_label, null);
                //找到里面需要动态改变的控件
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                //给控件赋值
                tvTitle.setText(titleList.get(j));
                //把所有动态创建的view都添加到容器里面
                view.bringToFront();
                mLinear.addView(view);
            }
//            ((MyViewHolder) holder).tvCompanyType.setText(mData.get(i).getCompanyType());
//            ((MyViewHolder) holder).tvPublisher.setText(mData.get(i).getPublisher());
//            ((MyViewHolder) holder).tvCompanyWelfare.setText(mData.get(i).getCompanyWelfare());
//            Glide.with(mContext).load(bean.getCompanyLogo()).into(((MyViewHolder) holder).ivCompanyLogo);
            mLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LabelInfoActivity.class);
                    intent.putExtra("keyLabel", mData.get(i).getLinkLabel());
//                     Log.d("initData: ", "initData: " + mData.get(i).getLinkLabel());
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

    /**
     * 自定义ViewHolder
     * 初始化控件
     */
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvTitle, tvHref, tvDescribe, tvTime, tvLabel,
                tvMore, tvCompanyType,
                tvPublisher, tvCompanyWelfare;
        public ImageView ivCompanyLogo;
        private IconManager iconManager;
        private String iconPath = "fonts/ionicons.ttf";

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.post_item_home_tv);
            tvHref = (TextView) itemView.findViewById(R.id.pay_item_home_tv);
            tvDescribe = (TextView) itemView.findViewById(R.id.way_item_home_tv);
            tvTime = (TextView) itemView.findViewById(R.id.experience_item_home_tv);
            tvMore = (TextView) itemView.findViewById(R.id.tv_search_more);

//            tvCompany = (TextView) itemView.findViewById(R.id.company_item_home_tv);
//            tvCompanyType = (TextView) itemView.findViewById(R.id.type_item_home_tv);
            tvLabel = (TextView) itemView.findViewById(R.id.publisher_item_home_tv);
//            tvCompanyWelfare = (TextView) itemView.findViewById(R.id.welfare_item_home_tv);
//            ivCompanyLogo = (ImageView) itemView.findViewById(R.id.logo_item_home_iv);
            mLinear = (LinearLayout) itemView.findViewById(R.id.linear_add_view);

            tvMore.setOnClickListener(this);
            tvTime.setOnClickListener(this);

            ((TextView) itemView.findViewById(R.id.tv_icon_comment))
                    .setTypeface(iconManager.getIcons(iconPath, mContext));
            ((TextView) itemView.findViewById(R.id.tv_icon_more))
                    .setTypeface(iconManager.getIcons(iconPath, mContext));
            ((TextView) itemView.findViewById(R.id.tv_icon_time))
                    .setTypeface(iconManager.getIcons(iconPath, mContext));
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.experience_item_home_tv:
                    Toast.makeText(mContext, "123", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_search_more:
                    PopupMenu popup = new PopupMenu(mContext, tvMore);
                    popup.getMenuInflater().inflate(R.menu.menu_pop, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.item_popup_black:
                                    Toast.makeText(mContext, "你点了大猪~",
                                            Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
            }
        }
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);

        void onItemLongClick(View view);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * 短时间点击
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view);
        }
    }

    /**
     * 长时间点击
     *
     * @param view
     * @return
     */
    @Override
    public boolean onLongClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemLongClick(view);
        }
        return false;
    }

}
