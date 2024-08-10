package com.qsa.lock.mvp.contract;

import android.content.Context;

import com.qsa.lock.base.BasePresenter;
import com.qsa.lock.base.BaseView;
import com.qsa.lock.model.CommLockInfo;

import java.util.List;



public interface MainContract {
    interface View extends BaseView<Presenter> {
        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context, boolean isSort);

        void loadLockAppInfo(Context context);

        void onDestroy();
    }
}
