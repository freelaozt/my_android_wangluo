package com.fxd.wangluo.Adapter;


import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ddiehl.timesincetextview.TimeSinceTextView;
import com.fxd.wangluo.Bean.SearchBean;
import com.fxd.wangluo.Constants;
import com.fxd.wangluo.utils.IconManager;
import com.fxd.wangluo.R;
import com.fxd.wangluo.View.CommentActivity;
import com.fxd.wangluo.View.LabelInfoActivity;
import com.fxd.wangluo.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SearchBean> mData;
    private LinearLayout mLinear;
    private List<String> titleList;//放置标题的集合
    private static final String TAG = "SearchAdapter";

    public SearchAdapter(Context context, List<SearchBean> data) {
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
            ((MyViewHolder) holder).tvTime.setDate(Integer.parseInt(mData.get(i).getLinkTime()));
//            ((MyViewHolder) holder).tvLabel.setText(mData.get(i).getLinkLabel());

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

    // 自定义ViewHolder初始化控件
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvTitle, tvHref, tvDescribe, tvLabel;
        private TimeSinceTextView tvTime;
        private LinearLayout linearItemView, linearMore, linearComment;
        private IconManager iconManager;
        private String iconPath = "fonts/ionicons.ttf";

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_link_title);
            tvDescribe = (TextView) itemView.findViewById(R.id.tv_item_link_des);
            tvHref = (TextView) itemView.findViewById(R.id.tv_item_link_href);
            tvTime = (TimeSinceTextView) itemView.findViewById(R.id.tv_item_link_time);
            linearMore = (LinearLayout) itemView.findViewById(R.id.linear_item_search_more);
            linearComment = (LinearLayout) itemView.findViewById(R.id.linear_item_search_comment);
//            tvLabel = (TextView) itemView.findViewById(R.id.publisher_item_home_tv);
            mLinear = (LinearLayout) itemView.findViewById(R.id.linear_add_view);


            linearMore.setOnClickListener(this);
            linearComment.setOnClickListener(this);

            ((TextView) itemView.findViewById(R.id.tv_icon_comment))
                    .setTypeface(iconManager.getIcons(iconPath, mContext));
            ((TextView) itemView.findViewById(R.id.tv_icon_more))
                    .setTypeface(iconManager.getIcons(iconPath, mContext));
            ((TextView) itemView.findViewById(R.id.tv_icon_time))
                    .setTypeface(iconManager.getIcons(iconPath, mContext));
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.linear_item_search_comment:
                    Toast.makeText(mContext, "评论", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.putExtra("netId", String.valueOf(mData.get(getAdapterPosition()).getLinkId()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    mContext.startActivity(intent);
                    break;
                case R.id.linear_item_search_more:
                    PopupMenu popup = new PopupMenu(mContext, linearMore);
                    popup.getMenuInflater().inflate(R.menu.menu_pop, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.item_copy_link:
                                    ClipboardManager clipManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("已复制", mData.get(getAdapterPosition()).getLinkHref());
                                    clipManager.setPrimaryClip(clip);
                                    Toast.makeText(mContext, "已复制 喔<", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.item_popup_black:
                                    initPopWindow(view);
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
            }
        }

        private void initPopWindow(View v) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_popup, null, false);
            LinearLayout mLinearAddView = (LinearLayout) view.findViewById(R.id.linear_add_view);
            //1.构造一个PopupWindow，参数依次是加载的View，宽高
            final PopupWindow popWindow = new PopupWindow(view,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//            popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

            //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
            //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
            //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
            popWindow.setTouchable(true);
            popWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                    // 这里如果返回true的话，touch事件将被拦截
                    // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                }
            });
            //要为popWindow设置一个背景才有效
            popWindow.setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
            popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止被底部虚拟键挡住
            popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);//showAtLocation(相对于组件parent屏幕)
            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWindow.dismiss();
                }
            });

            //设置popupWindow里的按钮的事件
            String numStr = "2;3;4";
            String titleStr = "内容已过期;内容不适宜;内容不符合";
            final List<String> tList = new ArrayList<>();//放置标题的集合
            final List<String> nList = new ArrayList<>();//放置标题的集合
            String[] arrTitle = titleStr.split(";");
            String[] arrNum = numStr.split(";");
            for (int i = 0; i < arrTitle.length; i++) {
                tList.add(arrTitle[i]);
                nList.add(arrNum[i]);
            }
            //数据拿到之后去根据数据去动态添加View
            //ivList集合有几个元素就添加几个
            for (int i = 0; i < tList.size(); i++) {
                //首先引入要添加的View
                View vLabel = View.inflate(mContext, R.layout.item_btn, null);
                //找到里面需要动态改变的控件
                Button btnBase = (Button) vLabel.findViewById(R.id.btn_base);
//                btnBase.setBackgroundResource(R.drawable.btn_bottom_confirm_bg);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnBase.getLayoutParams();
                params.setMargins(0, 5, 0, 5); //left, top, right, bottom
                btnBase.setLayoutParams(params);
                //给控件赋值
                btnBase.setText(tList.get(i));
                //设置每个View的点击事件
                final int finalI = i;//由于OnClick里面拿不到i,所以需要重新赋值给一个final对象
                btnBase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkUtils.isConnected(mContext)) {
                            String linkId = String.valueOf(mData.get(getAdapterPosition()).getLinkId());
                            String status = nList.get(finalI);
                            String url = Constants.API.UPDATE_STATUS + "?a=ureport&ld=" + linkId + "&s=" + status;
                            Log.d("SearchActivity", "ID:" + nList.get(finalI) + "标题：" + tList.get(finalI));
                            submitData(url);
                            popWindow.dismiss();
                        } else {
                            Toast.makeText(mContext, "没网 可不能举报哦", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //把所有动态创建的view都添加到容器里面
                mLinearAddView.addView(vLabel);
            }
        }

        public void submitData(String URL) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .get().build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onResponse: " + "网络错误");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string();
                    //处理完成后给handler发送消息
                    Message msg = new Message();
                    if (data.equals("success")) {
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }else{
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }

                    Log.d(TAG, "onResponse: " + data);
                }
            });
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(mContext, "您的反馈 稍后处理", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == 2) {
                Toast.makeText(mContext, "您的反馈 失败 可能已被举报 重试", Toast.LENGTH_SHORT).show();
            }
        }
    };

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
