package com.qsa.lock.base;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.qsa.lock.LockApplication;
import com.qsa.lock.R;
import com.qsa.lock.utils.SystemBarHelper;



public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mCustomTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LockApplication.getInstance().doForCreate(this);
        setContentView(getLayoutId());
        initViews(savedInstanceState);
        initToolBar();
        initData();
        initAction();
    }

    public abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void initToolBar() {
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
            SystemBarHelper.immersiveStatusBar(this);
            SystemBarHelper.setHeightAndPadding(this, mToolbar);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            resetToolbar();
            getSupportActionBar().setElevation(0);
        }
    }

    public void resetToolbar() {
        if (mCustomTitleTextView == null) {
            mCustomTitleTextView = (TextView) getLayoutInflater().inflate(R.layout.layout_toolbar_title, null);
        }
      getSupportActionBar().setCustomView(mCustomTitleTextView, new ActionBar.LayoutParams(Gravity.CENTER));
        if (getTitle() != null) {
            mCustomTitleTextView = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
            mCustomTitleTextView.setText(getTitle());
        }
    }

    public void hiddenActionBar() {
        getSupportActionBar().hide();
    }

    protected abstract void initData();

    protected abstract void initAction();






    @Override
    protected void onDestroy() {
        super.onDestroy();
        LockApplication.getInstance().doForFinish(this);
    }


    public final void clear() {
        super.finish();
    }
}
