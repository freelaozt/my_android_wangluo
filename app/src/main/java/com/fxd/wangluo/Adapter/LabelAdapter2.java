package com.fxd.wangluo.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fxd.wangluo.Bean.LabelBean;
import com.fxd.wangluo.R;

import java.util.List;
import java.util.Random;

/**
 * Created by LaoZhang on 2018/2/16.
 */
public class LabelAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<LabelBean> mData;
    private int mType;

    public LabelAdapter2(Context context, List<LabelBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.from(mContext).inflate(R.layout.item_type, parent, false);
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
        ((MyViewHolder) holder).tvTitle.setText(mData.get(position).getLabelTitle());
        ((MyViewHolder) holder).tvTitle.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        private CardView itemCardViewType;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemCardViewType = (CardView) itemView.findViewById(R.id.cv_item_type);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_title);

        }
    }


    public static interface OnRVItemClickListener {
        void onItemClick(View view);

        void onItemLongClick(View view);
    }

    private OnRVItemClickListener mOnItemClickListener = null;

    public void setmOnItemClickListener(OnRVItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
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
