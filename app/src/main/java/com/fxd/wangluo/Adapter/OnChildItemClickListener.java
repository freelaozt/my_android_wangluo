package com.fxd.wangluo.Adapter;

import android.view.View;

/**
 * Created by aspsine on 16/8/9.
 */

public interface OnChildItemClickListener<C> {
    void onChildItemClick(int groupPosition, int childPosition, C c, View view);
}
