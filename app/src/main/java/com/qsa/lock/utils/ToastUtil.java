package com.qsa.lock.utils;

import android.widget.Toast;

import androidx.annotation.Nullable;

import com.qsa.lock.LockApplication;


public class ToastUtil {
    @Nullable
    private static Toast mToast = null;

    public static void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(LockApplication.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void showLoginToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(LockApplication.getInstance(), text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

}
