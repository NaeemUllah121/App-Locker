package com.qsa.lock.activities.lock;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.qsa.lock.R;
import com.qsa.lock.base.BaseActivity;
import com.qsa.lock.model.LockStage;
import com.qsa.lock.model.Theme;
import com.qsa.lock.mvp.contract.GestureCreateContract;
import com.qsa.lock.mvp.p.GestureCreatePresenter;
import com.qsa.lock.utils.LockPatternUtils;
import com.qsa.lock.utils.SystemBarHelper;
import com.qsa.lock.utils.ToastUtil;
import com.qsa.lock.widget.LockPatternView;
import com.qsa.lock.widget.LockPatternViewPattern;
import com.squareup.picasso.Picasso;

import java.util.List;



public class GestureCreateActivity extends BaseActivity implements View.OnClickListener, GestureCreateContract.View {

    Theme theme;
    private static final String KEY_PATTERN_CHOICE = "chosenPattern";
    private static final String KEY_UI_STAGE = "uiStage";
    @Nullable
    protected List<LockPatternView.Cell> mChosenPattern = null;
    private LockPatternView mLockPatternView;
    private TextView mLockTip;
    private LockStage mUiStage = LockStage.Introduction;
    private LockPatternUtils mLockPatternUtils;
    private LockPatternViewPattern mPatternViewPattern;
    private GestureCreatePresenter mGestureCreatePresenter;
    private RelativeLayout mTopLayout;

    @NonNull
    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_gesture_lock;
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        mLockTip = findViewById(R.id.lock_tip);
        mLockPatternView = findViewById(R.id.lock_pattern_view);
        mTopLayout = findViewById(R.id.top_layout);
        mTopLayout.setPadding(0, SystemBarHelper.getStatusBarHeight(this), 0, 0);


        mGestureCreatePresenter = new GestureCreatePresenter(this, this);
        initLockPatternView();
        if (savedInstanceState == null) {
            mGestureCreatePresenter.updateStage(LockStage.Introduction);
        } else {
            final String patternString = savedInstanceState.getString(KEY_PATTERN_CHOICE);
            if (patternString != null) {
                mChosenPattern = LockPatternUtils.stringToPattern(patternString);
            }
            mGestureCreatePresenter.updateStage(LockStage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
        }
    }


    private void initLockPatternView() {
        mLockPatternUtils = new LockPatternUtils(this);
        mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
        mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
            @Override
            public void onPatternDetected(@NonNull List<LockPatternView.Cell> pattern) {
                mGestureCreatePresenter.onPatternDetected(pattern, mChosenPattern, mUiStage);
            }
        });
        mLockPatternView.setOnPatternListener(mPatternViewPattern);
        mLockPatternView.setTactileFeedbackEnabled(true);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void updateUiStage(LockStage stage) {
        mUiStage = stage;
    }

    @Override
    public void updateChosenPattern(List<LockPatternView.Cell> mChosenPattern) {
        this.mChosenPattern = mChosenPattern;
    }

    @Override
    public void updateLockTip(String text, boolean isToast) {
        if (isToast) {
            ToastUtil.showToast(text);
        } else {
            mLockTip.setText(text);
        }
    }

    @Override
    public void setHeaderMessage(int headerMessage) {
        mLockTip.setText(headerMessage);
    }

    @Override
    public void lockPatternViewConfiguration(boolean patternEnabled, LockPatternView.DisplayMode displayMode) {
        if (patternEnabled) {
            mLockPatternView.enableInput();
        } else {
            mLockPatternView.disableInput();
        }
        mLockPatternView.setDisplayMode(displayMode);
    }

    @Override
    public void Introduction() {
        clearPattern();
    }

    @Override
    public void HelpScreen() {

    }

    @Override
    public void ChoiceTooShort() {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 1000);
    }

    @Override
    public void moveToStatusTwo() {

    }

    @Override
    public void clearPattern() {
        mLockPatternView.clearPattern();
    }

    @Override
    public void ConfirmWrong() {
        mChosenPattern = null;
        clearPattern();
    }

    @Override
    public void ChoiceConfirmed() {
        mLockPatternUtils.saveLockPattern(mChosenPattern);
        clearPattern();
        setResult(RESULT_OK);
        finish();
    }
}
