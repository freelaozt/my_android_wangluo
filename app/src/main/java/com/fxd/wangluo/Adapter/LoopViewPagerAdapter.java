package com.fxd.wangluo.Adapter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxd.wangluo.Bean.Character;
import com.fxd.wangluo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LoopViewPagerAdapter extends BaseLoopPagerAdapter {

    private final List<Character> mHeroes;

    private final ViewGroup mIndicators;

    private int mLastPosition;

    public LoopViewPagerAdapter(ViewPager viewPager, ViewGroup indicators) {
        super(viewPager);
        mIndicators = indicators;
        mHeroes = new ArrayList<>();
    }

    public void setList(List<Character> heroes) {
        mHeroes.clear();
        mHeroes.addAll(heroes);
        notifyDataSetChanged();
    }

    /**
     * oh shit! An indicator view is badly needed!
     * this shit have no animation at all.
     */
    private void initIndicators() {
        if (mIndicators.getChildCount() != mHeroes.size() && mHeroes.size() > 1) {
            mIndicators.removeAllViews();
            Resources res = mIndicators.getResources();
            int size = res.getDimensionPixelOffset(R.dimen.indicator_size);
            int margin = res.getDimensionPixelOffset(R.dimen.indicator_margin);
            for (int i = 0; i < getPagerCount(); i++) {
                ImageView indicator = new ImageView(mIndicators.getContext());
                indicator.setAlpha(180);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                lp.setMargins(margin, 0, 0, 0);
                lp.gravity = Gravity.CENTER;
                indicator.setLayoutParams(lp);
                Drawable drawable = res.getDrawable(R.drawable.selector_indicator);
                indicator.setImageDrawable(drawable);
                mIndicators.addView(indicator);
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        initIndicators();
        super.notifyDataSetChanged();
    }

    @Override
    public int getPagerCount() {
        return mHeroes.size();
    }

    @Override
    public Character getItem(int position) {
        return mHeroes.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager, parent, false);
            holder = new ViewHolder();
            holder.ivBanner = (ImageView) convertView.findViewById(R.id.ivBanner);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_item_link_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), mHeroes.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri contentUrl = Uri.parse(mHeroes.get(position).getHref());
                intent.setData(contentUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });
        Character character = mHeroes.get(position);

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        holder.tvName.setText(character.getTitle());
//        holder.tvName.setText(character.getTitle().replace(" ", System.getProperty("line.separator")));
        holder.tvName.setTextColor(color);
        holder.ivBanner.setVisibility(View.GONE);

//        Picasso.with(parent.getContext()).load(character.getAvatar()).into(holder.ivBanner);

        return convertView;
    }

    @Override
    public void onPageItemSelected(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mIndicators.getChildAt(mLastPosition).setActivated(false);
            mIndicators.getChildAt(position).setActivated(true);
        }
        mLastPosition = position;
    }

    public static class ViewHolder {
        ImageView ivBanner;
        TextView tvName;
    }
}
