package com.fxd.wangluo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ddiehl.timesincetextview.TimeSinceTextView;
import com.fxd.wangluo.Bean.Character;
import com.fxd.wangluo.Bean.Section;
import com.fxd.wangluo.Constants;
import com.fxd.wangluo.utils.IconManager;
import com.fxd.wangluo.R;
import com.fxd.wangluo.fragment.HomeRecyclerFragment;
import com.fxd.wangluo.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RecyclerCharactersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_VIEWPAGER = 0;//表示幻灯区
    private static final int TYPE_GROUP = 1;//表示小圆点page
    private static final int TYPE_CHILD = 2;//表示列表

    private final List<Character> mHeroes;

    private final List<Section> mSections;

    private final List<Integer> mGroupPositions;

    private LoopViewPagerAdapter mPagerAdapter;

    protected OnGroupItemClickListener mOnGroupItemClickListener;
    protected OnGroupItemLongClickListener mOnGroupItemLongClickListener;

    protected OnChildItemClickListener mOnChildItemClickListener;
    protected OnChildItemLongClickListener mOnChildItemLongClickListener;

    private Context mContext;
    private final int mType;

    private String fontAwesomeBrandsPath = "fonts/fa-brands-400.ttf";
    private String fontAwesomeRegularPath = "fonts/fa-regular-400.ttf";
    private String fontAwesomeSolidPath = "fonts/fa-solid-900.ttf";
    private static final String TAG = "RecyclerCharactersAdapter";

    public RecyclerCharactersAdapter(Context context, int type) {
        this.mContext = context;
        mType = type;
        mHeroes = new ArrayList<>();
        mSections = new ArrayList<>();
        mGroupPositions = new ArrayList<>();
    }


    //嵌套json数据 将角色数组数据添加选择数组
    public void setList(List<Character> heroes, List<Section> sections) {
        mHeroes.clear();
        mSections.clear();
        mHeroes.addAll(heroes);
        append(sections);
    }

    public void append(List<Section> sections) {
        mSections.addAll(sections);
        notifyDataSetChanged();
        initGroupPositions();
    }

    public void initGroupPositions() {
        mGroupPositions.clear();
        int groupPosition = 0;
        for (int i = 0; i < getGroupCount(); i++) {
            if (i == 0) {
                groupPosition = 0;
            } else {
                groupPosition += getChildCount(i - 1) + 1;
            }
            mGroupPositions.add(groupPosition + 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_VIEWPAGER;
        } else if (mGroupPositions.contains(position)) {
            return TYPE_GROUP;
        } else {
            return TYPE_CHILD;
        }
    }

    @Override
    public int getItemCount() {
        int count = mHeroes.size() == 0 ? 0 : 1;
        for (int i = 0; i < getGroupCount(); i++) {
            count += getChildCount(i);
        }
        return count + getGroupCount();
    }

    public int getChildCount(int groupPosition) {
        List<Character> characters = mSections.get(groupPosition).getCharacters();

        return characters != null ? characters.size() : 0;
    }

    public int getGroupCount() {
        return mSections.size();
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    if (isFullSpanType(adapter.getItemViewType(position))) {
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        int type = getItemViewType(position);
        if (isFullSpanType(type)) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
    }

    private boolean isFullSpanType(int type) {
        return type == TYPE_VIEWPAGER || type == TYPE_GROUP;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int type = getItemViewType(i);
        View itemView = null;
        switch (type) {
            case TYPE_VIEWPAGER:
                //向inflate()装入R.layout.layout_viewpager初始化
                itemView = inflate(viewGroup, R.layout.layout_viewpager);
                return new ViewPagerHolder(itemView);
            case TYPE_GROUP:
                itemView = inflate(viewGroup, R.layout.item_header);
                return new GroupHolder(itemView);
            case TYPE_CHILD:
                if (mType == HomeRecyclerFragment.TYPE_LINEAR) {
                    itemView = inflate(viewGroup, R.layout.item_hero);
                } else {
                    itemView = inflate(viewGroup, R.layout.item_hero_grid);
                }
                final ChildHolder holder = new ChildHolder(itemView);

                //Home item 点击事件
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int absolutePosition = holder.getAdapterPosition();
                        int groupPosition = getGroupPosition(absolutePosition);
                        int childPosition = getChildPosition(absolutePosition);
                        Character character = mSections.get(groupPosition).getCharacters().get(childPosition);

                        initPopWindow(view, character.getHref());
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int absolutePosition = holder.getAdapterPosition();
                        int groupPosition = getGroupPosition(absolutePosition);
                        int childPosition = getChildPosition(absolutePosition);
                        Character character = mSections.get(groupPosition).getCharacters().get(childPosition);

                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri contentUrl = Uri.parse(character.getHref());
                        intent.setData(contentUrl);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        view.getContext().startActivity(intent);
                        return false;
                    }
                });

                return holder;
        }
        throw new IllegalArgumentException("Wrong type!");
    }

    private void initPopWindow(View v, String href) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_popup, null, false);
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
        popWindow.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);//showAtLocation(相对于组件parent屏幕)

        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);

        LinearLayout mLinearAddView = (LinearLayout) view.findViewById(R.id.linear_add_view);
        //首先引入要添加的View
        View layoutDialogWebView = View.inflate(mContext, R.layout.dialog_web_view, null);
        TextView tvDialogCollect = (TextView) layoutDialogWebView.findViewById(R.id.tv_dialog_collect);
        TextView tvDialogShare = (TextView) layoutDialogWebView.findViewById(R.id.tv_dialog_share);
        TextView tvDialogCopy = (TextView) layoutDialogWebView.findViewById(R.id.tv_dialog_copy);
        TextView tvDialogFullScreen = (TextView) layoutDialogWebView.findViewById(R.id.tv_dialog_full_screen);
        TextView tvDialogReport = (TextView) layoutDialogWebView.findViewById(R.id.tv_dialog_report);
        TextView tvDialogComment = (TextView) layoutDialogWebView.findViewById(R.id.tv_dialog_comment);
        TextView tvDialogCancel = (TextView) layoutDialogWebView.findViewById(R.id.tv_dialog_cancel);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), fontAwesomeSolidPath);

        tvDialogCollect.setTypeface(font);
        tvDialogShare.setTypeface(font);
        tvDialogCopy.setTypeface(font);
        tvDialogFullScreen.setTypeface(font);
        tvDialogReport.setTypeface(font);
        tvDialogComment.setTypeface(font);
        tvDialogCancel.setTypeface(font);

        tvDialogCollect.setText("\uf005");
        tvDialogShare.setText("\uf14d");
        tvDialogCopy.setText("\uf0c5");
        tvDialogFullScreen.setText("\uf0b2");
        tvDialogReport.setText("\uf256");
        tvDialogComment.setText("\uf4ad");
        tvDialogCancel.setText("\uf00d");

        tvDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
            }
        });
        WebView webView = (WebView) layoutDialogWebView.findViewById(R.id.wb_link_info_dialog);
        webView.loadUrl(href);
        webView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
        //把所有动态创建的view都添加到容器里面
        mLinearAddView.addView(layoutDialogWebView);
    }

    private View inflate(ViewGroup parent, int layoutRes) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        switch (type) {
            case TYPE_VIEWPAGER:
                onBindViewPagerHolder((ViewPagerHolder) viewHolder);
                break;
            case TYPE_GROUP:
                onBindGroupHolder((GroupHolder) viewHolder, getGroupPosition(i));
                break;
            case TYPE_CHILD:
                onBindChildHolder((ChildHolder) viewHolder, getGroupPosition(i), getChildPosition(i));
                break;
        }
    }

    /**
     * 绑定幻灯片Page数据源
     *
     * @param holder
     */
    private void onBindViewPagerHolder(ViewPagerHolder holder) {
        if (holder.viewPager.getAdapter() == null) {
            mPagerAdapter = new LoopViewPagerAdapter(holder.viewPager, holder.indicators);
            holder.viewPager.setAdapter(mPagerAdapter);
            holder.viewPager.addOnPageChangeListener(mPagerAdapter);
            String myHexColor = "#FDF5E6";
            holder.viewPager.setBackgroundColor(Color.parseColor(myHexColor));
            mPagerAdapter.setList(mHeroes);
        } else {
            mPagerAdapter.setList(mHeroes);
        }
    }

    private void onBindGroupHolder(GroupHolder holder, int parentPosition) {
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        holder.tvGroup.setTextColor(color);
        holder.tvGroup.setText(mSections.get(parentPosition).getName());
    }

    private void onBindChildHolder(ChildHolder holder, int parentPosition, int childPosition) {
        Character character = mSections.get(parentPosition).getCharacters().get(childPosition);
//        holder.tvName.setText(character.getTitle());
        holder.tvTitle.setText(character.getTitle());
        holder.tvDes.setText(character.getDescribe());
        holder.tvHref.setText(character.getHref());
        holder.tvTime.setDate(Integer.parseInt(character.getTime()));

        holder.ivCover.setVisibility(View.GONE);
//        Picasso.with(holder.itemView.getContext())
//                .load(character.getAvatar())
//                .transform(new CropCircleTransformation())
//                .into(holder.ivAvatar);
    }

    /**
     * 得到如，同一页面不同种类item样式：item01，item02
     *
     * @param position item01 幻灯片viewGroup
     * @return
     */
    int getGroupPosition(int position) {
        int groupPosition = 1;
        for (int i = mGroupPositions.size() - 1; i >= 0; i--) {
            if (position >= mGroupPositions.get(i)) {
                groupPosition = i;
                break;
            }
        }
        return groupPosition;
    }

    /**
     * 得到如，同一页面不同种类item样式：item01，item02
     *
     * @param position 得到item02
     * @return
     */
    int getChildPosition(int position) {
        int groupPosition = getGroupPosition(position);
        int absGroupPosition = mGroupPositions.get(groupPosition);
        int childPositionInGroup = position - absGroupPosition - 1;
        return childPositionInGroup;
    }

    public void start() {
        if (mPagerAdapter != null) {
            mPagerAdapter.start();
        }
    }

    public void stop() {
        if (mPagerAdapter != null) {
            mPagerAdapter.stop();
        }
    }

    /**
     * layout_viewpager.xml，幻灯区控件初始化
     */
    static class ViewPagerHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;
        ViewGroup indicators;

        public ViewPagerHolder(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.viewPager);
            indicators = (ViewGroup) itemView.findViewById(R.id.indicators);
        }
    }

    /**
     * item_header.xml标头的初始化
     */
    static class GroupHolder extends RecyclerView.ViewHolder {
        TextView tvGroup;

        public GroupHolder(View itemView) {
            super(itemView);
            tvGroup = (TextView) itemView.findViewById(R.id.tvHeader);
        }
    }

    /**
     * item_hero.xml,item_hero_grid.xml,item_viewpager控件初始化
     */
    static class ChildHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvName, tvTitle, tvDes, tvHref;
        TimeSinceTextView tvTime;
        private IconManager iconManager;
        private String iconPath = "fonts/ionicons.ttf";

        public ChildHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_link_title);
            tvDes = (TextView) itemView.findViewById(R.id.tv_item_link_des);
            tvHref = (TextView) itemView.findViewById(R.id.tv_item_link_href);
            tvTime = (TimeSinceTextView) itemView.findViewById(R.id.tv_item_link_time);
            ivCover = (ImageView) itemView.findViewById(R.id.iv_item_link_cover);

            ((TextView) itemView.findViewById(R.id.tv_icon_time))
                    .setTypeface(iconManager.getIcons(iconPath, itemView.getContext()));
//            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }

}
