package com.fxd.wangluo.Adapter;

import android.view.View;

/**
 * Created by aspsine on 16/8/9.
 */

public interface OnGroupItemLongClickListener<G> {
    boolean onGroupLongClick(int groupPosition, G g, View view);
}
