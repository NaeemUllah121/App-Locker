package com.qsa.lock.activities.setting;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.qsa.lock.R;
import com.qsa.lock.activities.about.AboutMeActivity;
import com.qsa.lock.activities.lock.GestureCreateActivity;
import com.qsa.lock.activities.main.MainActivity;
import com.qsa.lock.base.AppConstants;
import com.qsa.lock.base.BaseActivity;
import com.qsa.lock.base.BaseFragment;
import com.qsa.lock.model.LockAutoTime;
import com.qsa.lock.services.BackgroundManager;
import com.qsa.lock.services.LockService;
import com.qsa.lock.utils.SpUtil;
import com.qsa.lock.utils.SystemBarHelper;
import com.qsa.lock.utils.ToastUtil;
import com.qsa.lock.widget.SelectLockTimeDialog;


public class LockSettingActivity extends BaseActivity implements View.OnClickListener
        , DialogInterface.OnDismissListener, CompoundButton.OnCheckedChangeListener {

    public static final String ON_ITEM_CLICK_ACTION = "on_item_click_action";
    private static final int REQUEST_CHANGE_PWD = 3;

    private CheckBox cbLockSwitch;
    private CheckBox cbLockScreen;
    private CheckBox cbIntruderSelfie;
    private CheckBox cbHidePattern;
    private CheckBox cbVibration;

    private TextView tvAbout,
            tvLockTime,
            tvChangePwd;
    private ImageView backBtn;
    TextView changeTheme;

    private LockSettingReceiver mLockSettingReceiver;
    private SelectLockTimeDialog dialog;
    private RelativeLayout mTopLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        cbLockSwitch=findViewById(R.id.checkbox_app_lock_on_off);
        cbLockScreen=findViewById(R.id.checkbox_lock_screen_switch_on_phone_lock);
       // cbIntruderSelfie=findViewById(R.id.checkbox_intruder_selfie);
        cbHidePattern=findViewById(R.id.checkbox_show_hide_pattern);
      //  cbVibration=findViewById(R.id.checkbox_vibrate);

        tvChangePwd = findViewById(R.id.btn_change_pwd);
        tvAbout = findViewById(R.id.about_me);
        backBtn=findViewById(R.id.btn_back);
        changeTheme=findViewById(R.id.change_theme);

        //
        mTopLayout = findViewById(R.id.top_layout);
        mTopLayout.setPadding(0, SystemBarHelper.getStatusBarHeight(this), 0, 0);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LockSettingActivity.this, MainActivity.class));
                finish();
            }
        });

        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LockSettingActivity.this,ChangeThemeActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        mLockSettingReceiver = new LockSettingReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ON_ITEM_CLICK_ACTION);
        registerReceiver(mLockSettingReceiver, filter);
        dialog = new SelectLockTimeDialog(this, "");
        dialog.setOnDismissListener(this);
        boolean isLockOpen = SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE);
        cbLockSwitch.setChecked(isLockOpen);

        boolean isLockAutoScreen = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN, false);
        cbLockScreen.setChecked(isLockAutoScreen);
        boolean isHidePattern=SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_HIDE_LINE,false);
        cbHidePattern.setChecked(isHidePattern);



        boolean isTakePic = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_RECORD_PIC, false);

    }

    @Override
    protected void initAction() {

        cbLockSwitch.setOnCheckedChangeListener(this);
        cbLockScreen.setOnCheckedChangeListener(this);
//        cbIntruderSelfie.setOnCheckedChangeListener(this);
        cbHidePattern.setOnCheckedChangeListener(this);
//        cbVibration.setOnCheckedChangeListener(this);

//        tvLockTime.setOnClickListener(this);
        tvChangePwd.setOnClickListener(this);
        tvAbout.setOnClickListener(this);

    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_change_pwd:
                Intent intent = new Intent(LockSettingActivity.this, GestureCreateActivity.class);
                startActivityForResult(intent, REQUEST_CHANGE_PWD);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.about_me:
                intent = new Intent(LockSettingActivity.this, AboutMeActivity.class);
                startActivity(intent);
                break;

        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean b) {
        switch (buttonView.getId()) {
            case R.id.checkbox_app_lock_on_off:
                SpUtil.getInstance().putBoolean(AppConstants.LOCK_STATE, b);
                if (b) {
                    BackgroundManager.getInstance().init(LockSettingActivity.this).stopService(LockService.class);
                    BackgroundManager.getInstance().init(LockSettingActivity.this).startService(LockService.class);

                    BackgroundManager.getInstance().init(LockSettingActivity.this).startAlarmManager();

                } else {
                    BackgroundManager.getInstance().init(LockSettingActivity.this).stopService(LockService.class);
                    BackgroundManager.getInstance().init(LockSettingActivity.this).stopAlarmManager();
                }
                break;
            case R.id.checkbox_lock_screen_switch_on_phone_lock:
                SpUtil.getInstance().putBoolean(AppConstants.LOCK_AUTO_SCREEN, b);
                break;

            case R.id.checkbox_show_hide_pattern:
                SpUtil.getInstance().putBoolean(AppConstants.LOCK_IS_HIDE_LINE, b);
                break;


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CHANGE_PWD:
                    ToastUtil.showToast("Password reset succeeded");
                    break;
            }
        }
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLockSettingReceiver);
    }

    private class LockSettingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action.equals(ON_ITEM_CLICK_ACTION)) {
                LockAutoTime info = intent.getParcelableExtra("info");
                boolean isLast = intent.getBooleanExtra("isLast", true);
                if (isLast) {
                    tvLockTime.setText(info.getTitle());
                    SpUtil.getInstance().putString(AppConstants.LOCK_APART_TITLE, info.getTitle());
                    SpUtil.getInstance().putLong(AppConstants.LOCK_APART_MILLISECONDS, 0L);
                    SpUtil.getInstance().putBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, false);
                } else {
                    tvLockTime.setText(info.getTitle());
                    SpUtil.getInstance().putString(AppConstants.LOCK_APART_TITLE, info.getTitle());
                    SpUtil.getInstance().putLong(AppConstants.LOCK_APART_MILLISECONDS, info.getTime());
                    SpUtil.getInstance().putBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, true);
                }
                dialog.dismiss();
            }
        }
    }

}
