package com.qsa.lock.activities.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.andrognito.pinlockview.ResourceUtils;
import com.balsikandar.crashreporter.CrashReporter;
import com.qsa.lock.R;
import com.qsa.lock.activities.main.MainActivity;
import com.qsa.lock.base.AppConstants;
import com.qsa.lock.base.BaseActivity;
import com.qsa.lock.db.CommLockInfoManager;
import com.qsa.lock.model.Theme;
import com.qsa.lock.services.LockService;
import com.qsa.lock.utils.LockPatternUtils;
import com.qsa.lock.utils.LockUtil;
import com.qsa.lock.utils.PrefsManager;
import com.qsa.lock.utils.SpUtil;
import com.qsa.lock.utils.StatusBarUtil;
import com.qsa.lock.widget.LockPatternView;
import com.qsa.lock.widget.LockPatternViewPattern;
import com.qsa.lock.widget.UnLockMenuPopWindow;
import com.squareup.picasso.Picasso;

import java.util.List;




public class GestureUnlockActivity extends BaseActivity implements View.OnClickListener {

    public static final String FINISH_UNLOCK_THIS_APP = "finish_unlock_this_app";
    private ImageView mIconMore;
    private LockPatternView mLockPatternView;
    private ImageView mUnLockIcon, mBgLayout, mAppLogo;
    private TextView mUnLockText, mUnlockFailTip, mAppLabel;
    private RelativeLayout mUnLockLayout;
    private PackageManager packageManager;
    private String pkgName;
    private String actionFrom;
    private LockPatternUtils mLockPatternUtils;
    private int mFailedPatternAttemptsSinceLastTimeout = 0;
    private CommLockInfoManager mLockInfoManager;
    private UnLockMenuPopWindow mPopWindow;
    private LockPatternViewPattern mPatternViewPattern;
    private GestureUnlockReceiver mGestureUnlockReceiver;
    private ApplicationInfo appInfo;
    private Drawable iconDrawable;
    private String appLabel;
    ImageView imageView;
    Theme theme;
    @NonNull
    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_gesture_unlock;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        mUnLockLayout = findViewById(R.id.unlock_layout);
        mIconMore = findViewById(R.id.btn_more);
        mLockPatternView = findViewById(R.id.unlock_lock_view);
        mUnLockIcon = findViewById(R.id.unlock_icon);
        mBgLayout = findViewById(R.id.bg_layout);
        mUnLockText = findViewById(R.id.unlock_text);
        mUnlockFailTip = findViewById(R.id.unlock_fail_tip);
        mAppLogo = findViewById(R.id.app_logo);
        mAppLabel = findViewById(R.id.app_label);
        imageView = findViewById(R.id.app_bg_theme);

        theme = PrefsManager.getBackGroundImage();
        if (theme == null) {
            imageView.setImageResource(R.drawable.walll);
        } else {
            Picasso.get().load(theme.getBackGround()).into(imageView);


        }

    }

    @Override
    protected void initData() {
        pkgName = getIntent().getStringExtra(AppConstants.LOCK_PACKAGE_NAME);
        actionFrom = getIntent().getStringExtra(AppConstants.LOCK_FROM);
        packageManager = getPackageManager();
        mLockInfoManager = new CommLockInfoManager(this);
        mPopWindow = new UnLockMenuPopWindow(this, pkgName, true);



        initLayoutBackground();
        initLockPatternView();


        mGestureUnlockReceiver = new GestureUnlockReceiver();
        IntentFilter filter = new IntentFilter();
        //  filter.addAction(UnLockMenuPopWindow.UPDATE_LOCK_VIEW);
        filter.addAction(FINISH_UNLOCK_THIS_APP);
        registerReceiver(mGestureUnlockReceiver, filter);

    }

    private void initLayoutBackground() {
        try {
            appInfo = packageManager.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (appInfo != null) {
                iconDrawable = packageManager.getApplicationIcon(appInfo);
                appLabel = packageManager.getApplicationLabel(appInfo).toString();
                mUnLockIcon.setImageDrawable(iconDrawable);
                mUnLockText.setText(appLabel);
                mUnlockFailTip.setText(getString(R.string.password_gestrue_tips));
                final Drawable icon = packageManager.getApplicationIcon(appInfo);
                mUnLockLayout.setBackgroundDrawable(icon);
                mUnLockLayout.getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                mUnLockLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                                mUnLockLayout.buildDrawingCache();
                                int width = mUnLockLayout.getWidth(), height = mUnLockLayout.getHeight();
                                if (width == 0 || height == 0) {
                                    Display display = getWindowManager().getDefaultDisplay();
                                    Point size = new Point();
                                    display.getSize(size);
                                    width = size.x;
                                    height = size.y;
                                }
                                Bitmap bmp = LockUtil.drawableToBitmap(icon, width, height);
                                try {
                                    LockUtil.blur(GestureUnlockActivity.this, LockUtil.big(bmp), mUnLockLayout, width, height);
                                } catch (IllegalArgumentException ignore) {
                                    CrashReporter.logException(ignore);
                                }
                                return true;
                            }
                        });
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initLockPatternView() {


        mLockPatternView.setLineColorRight(0x8098C7F3);
        mLockPatternUtils = new LockPatternUtils(this);
        mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
        mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
            @Override
            public void onPatternDetected(@NonNull List<LockPatternView.Cell> pattern) {
                if (mLockPatternUtils.checkPattern(pattern)) { //
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
                        startActivity(new Intent(GestureUnlockActivity.this, MainActivity.class));
                        finish();
                    } else {
                        SpUtil.getInstance().putLong(AppConstants.LOCK_CURR_MILLISECONDS, System.currentTimeMillis());
                        SpUtil.getInstance().putString(AppConstants.LOCK_LAST_LOAD_PKG_NAME, pkgName);

                        //Send the last unlocked time to the app lock service
                        Intent intent = new Intent(LockService.UNLOCK_ACTION);
                        intent.putExtra(LockService.LOCK_SERVICE_LASTTIME, System.currentTimeMillis());
                        intent.putExtra(LockService.LOCK_SERVICE_LASTAPP, pkgName);
                        sendBroadcast(intent);

                        mLockInfoManager.unlockCommApplication(pkgName);
                        finish();
                    }
                } else {
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
                        mFailedPatternAttemptsSinceLastTimeout++;
                        int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - mFailedPatternAttemptsSinceLastTimeout;
                        if (retry >= 0) {
                            String format = getResources().getString(R.string.password_error_count);
                            mUnlockFailTip.setText(format);
                            //TODO: click a pic of intruder
                        }
                    } else {

                        //ToastUtil.showShort(getString(R.string.password_short));
                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= 3) {
                        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) { // The number of failures is greater than the maximum number of error attempts before blocking the user
                        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
                    } else {
                        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
                    }
                }
            }
        });
        mLockPatternView.setOnPatternListener(mPatternViewPattern);
        mLockPatternView.setTactileFeedbackEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (actionFrom.equals(AppConstants.LOCK_FROM_FINISH)) {
            LockUtil.goHome(this);
        } else if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
            finish();
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void initAction() {
        mIconMore.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_more:
                mPopWindow.showAsDropDown(mIconMore);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGestureUnlockReceiver);
    }

    private class GestureUnlockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
//            if (action.equals(UnLockMenuPopWindow.UPDATE_LOCK_VIEW)) {
//                mLockPatternView.initRes();
//            } else
            if (action.equals(FINISH_UNLOCK_THIS_APP)) {
                finish();
            }
        }
    }
}
