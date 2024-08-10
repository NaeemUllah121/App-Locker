package com.qsa.lock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.qsa.lock.base.AppConstants;
import com.qsa.lock.services.BackgroundManager;
import com.qsa.lock.services.LoadAppListService;
import com.qsa.lock.services.LockService;
import com.qsa.lock.utils.LogUtil;
import com.qsa.lock.utils.SpUtil;


public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        LogUtil.i("Boot service....");
        //TODO: pie compatable done
       // context.startService(new Intent(context, LoadAppListService.class));
        BackgroundManager.getInstance().init(context).startService(LoadAppListService.class);
        if (SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE, false)) {
            BackgroundManager.getInstance().init(context).startService(LockService.class);
           // BackgroundManager.getInstance().init(context).startAlarmManager();
        }
    }
}
