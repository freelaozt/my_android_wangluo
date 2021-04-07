package com.fxd.wangluo.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddiehl.timesincetextview.TimeSinceTextView;
import com.fxd.wangluo.Bean.CommentBean;
import com.fxd.wangluo.utils.IconManager;
import com.fxd.wangluo.R;

import java.util.List;
import java.util.Random;

/**
 * Created by LaoZhang on 2018/3/1.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<CommentBean> mData;
    private LinearLayout mLinear;

    public CommentAdapter(Context context, List<CommentBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.from(mContext).inflate(R.layout.item_type, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        ((MyViewHolder) holder).itemCardViewType.setBackgroundColor(color);
        ((MyViewHolder) holder).tvContent.setTextColor(color);
        ((MyViewHolder) holder).tvContent.setText(mData.get(position).getComContent());
        ((MyViewHolder) holder).tvTime.setDate(Integer.parseInt(mData.get(position).getComTime()));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvContent;
        private TimeSinceTextView tvTime;
        private CardView itemCardViewType;
        private IconManager iconManager;
        private String iconPath = "fonts/ionicons.ttf";

        public MyViewHolder(View itemView) {
            super(itemView);
            itemCardViewType = (CardView) itemView.findViewById(R.id.cv_item_type);
            tvContent = (TextView) itemView.findViewById(R.id.tv_item_title);
            tvTime = (TimeSinceTextView) itemView.findViewById(R.id.tv_item_time);
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

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemLongClick(view);
        }
        return false;
    }
}
