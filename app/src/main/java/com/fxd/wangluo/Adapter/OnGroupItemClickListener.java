package com.fxd.wangluo.Adapter;

import android.view.View;

/**
 * Created by aspsine on 16/8/9.
 */

public interface OnGroupItemClickListener<G> {
    void onGroupItemClick(int groupPosition, G g, View view);
}
