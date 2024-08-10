package com.qsa.lock.mvp.contract;

import android.content.Context;

import com.qsa.lock.base.BasePresenter;
import com.qsa.lock.base.BaseView;
import com.qsa.lock.model.CommLockInfo;
import com.qsa.lock.mvp.p.LockMainPresenter;

import java.util.List;



public interface LockMainContract {
    interface View extends BaseView<Presenter> {

        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context);

        void searchAppInfo(String search, LockMainPresenter.ISearchResultListener listener);

        void onDestroy();
    }
}
