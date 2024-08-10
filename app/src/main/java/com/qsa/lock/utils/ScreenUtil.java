package com.qsa.lock.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;


public class ScreenUtil {
    /**
     * 
     *
     * @param context
     * @return
     */
    public static int getPhoneHeight(@NonNull Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getPhoneWidth(@NonNull Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的分辨率
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    /**
     * 获取屏幕旋转方向
     * @param context 上下文
     * @return 屏幕方向 ORIENTATION_LANDSCAPE, ORIENTATION_PORTRAIT.
     */
    public static int getDisplayOrient (Context context) {
        return context.getResources().getConfiguration().orientation;
    }

}
