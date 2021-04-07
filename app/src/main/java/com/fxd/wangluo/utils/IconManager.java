package com.fxd.wangluo.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by LaoZhang on 2017/4/21.
 */
public class IconManager {
    private static Hashtable<String,Typeface> cachedIcons = new Hashtable<>();

    public static Typeface getIcons(String path, Context context){
        Typeface icons = cachedIcons.get(path);

        if (icons == null){

            icons = Typeface.createFromAsset(context.getAssets(),path);
            cachedIcons.put(path,icons);

        }

        return icons;
    }
}
