package com.qsa.lock.activities.main;

import static com.qsa.lock.LockApplication.context;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.qsa.lock.R;
import com.qsa.lock.activities.lock.GestureSelfUnlockActivity;
import com.qsa.lock.activities.pwd.CreatePwdActivity;
import com.qsa.lock.base.AppConstants;
import com.qsa.lock.base.BaseActivity;
import com.qsa.lock.services.BackgroundManager;
import com.qsa.lock.services.LoadAppListService;
import com.qsa.lock.services.LockService;
import com.qsa.lock.utils.AppUtils;
import com.qsa.lock.utils.LockUtil;
import com.qsa.lock.utils.SpUtil;
import com.qsa.lock.utils.ToastUtil;
import com.qsa.lock.widget.DialogPermission;


public class SplashActivity extends BaseActivity {
    private static final int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;
    private static final int RESULT_ACTION_ACCESSIBILITY_SETTINGS = 3;
    private static final int STORAGE_PERMISSION_CODE = 1;
    private static final int RESULT_ACTION_MANAGE_OVERLAY_PERMISSION=20;
    private ImageView mImgSplash;
    @Nullable
    private ObjectAnimator animator;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        AppUtils.hideStatusBar(getWindow(), true);
        mImgSplash = findViewById(R.id.img_splash);
    }

    @Override
    protected void initData() {

        BackgroundManager.getInstance().init(this).startService(LoadAppListService.class);

        //start lock services if  everything is already  setup
        if (SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE, false)) {
            BackgroundManager.getInstance().init(this).startService(LockService.class);
        }

        animator = ObjectAnimator.ofFloat(mImgSplash, "alpha", 0.5f, 1);
        animator.setDuration(1500);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                boolean isFirstLock = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_FIRST_LOCK, true);
                if (isFirstLock) {
                    showDialog();
                    initOPPO();
                } else {
                    Intent intent = new Intent(SplashActivity.this, GestureSelfUnlockActivity.class);
                    intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, AppConstants.APP_PACKAGE_NAME);
                    intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

    }

    private void showDialog() {
        // If you do not have access to view usage rights and the phone exists to view usage this interface
        if (!LockUtil.isStatAccessPermissionSet(SplashActivity.this) && LockUtil.isNoOption(SplashActivity.this)) {
            DialogPermission dialog = new DialogPermission(SplashActivity.this);
            dialog.show();
            dialog.setOnClickListener(new DialogPermission.onClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = null;
                        intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
                    }
                    if (!Settings.canDrawOverlays(SplashActivity.this)){
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), 20);
                        Toast.makeText(SplashActivity.this,"Allow Overlay Permission",Toast.LENGTH_SHORT).show();

                    }
                }

            });
        } else {
            gotoCreatePwdActivity();
        }

    }

//    private void setPermission() {
//        // If you do not have access to view usage rights and the phone exists to view usage this interface
//        if (!LockUtil.isStatAccessPermissionSet(SplashActivity.this) && LockUtil.isNoOption(SplashActivity.this)) {
//            DialogPermission dialog = new DialogPermission(SplashActivity.this);
//            dialog.show();
//            dialog.setOnClickListener(new DialogPermission.onClickListener() {
//                @RequiresApi(api = Build.VERSION_CODES.M)
//                @Override
//                public void onClick() {
//                    if (!Settings.canDrawOverlays(SplashActivity.this)) {
//                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), 20);
//                    }
//                }
//            });
//        } else {
//            gotoCreatePwdActivity();
//        }
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (Settings.canDrawOverlays(this)) {
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show(); }
        if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {

            if (LockUtil.isStatAccessPermissionSet(SplashActivity.this)) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                gotoCreatePwdActivity();

            } else {
             //   ToastUtil.showToast("Permission denied");
                 // finish();
                new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("Grant permission")
                        .setMessage("Allow All permission to use AppLock")
                        .setCancelable(false)
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showDialog();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        }
        if (requestCode == RESULT_ACTION_ACCESSIBILITY_SETTINGS ) {
            gotoCreatePwdActivity();
        }
    }
    private void initOPPO() {

        try {

            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity"));
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            try {

                Intent intent = new Intent("action.coloros.safecenter.FloatWindowListActivity");
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity"));
                startActivity(intent);
            } catch (Exception ee) {

                ee.printStackTrace();
                try{

                    Intent i = new Intent("com.coloros.safecenter");
                    i.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity"));
                    startActivity(i);
                }catch (Exception e1){

                    e1.printStackTrace();
                }
            }

        }

        if(Build.BRAND.equalsIgnoreCase("xiaomi") ){

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);


        }else if(Build.BRAND.equalsIgnoreCase("Letv")){

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            startActivity(intent);

        }
        else if(Build.BRAND.equalsIgnoreCase("Honor")){

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            startActivity(intent);

        }


        if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
            try {
                Intent intent = new Intent();
                intent.setClassName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity");
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.oppo.safe",
                            "com.oppo.safe.permission.startup.StartupAppListActivity");
                    startActivity(intent);

                } catch (Exception ex) {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.coloros.safecenter",
                                "com.coloros.safecenter.startupapp.StartupAppListActivity");
                        startActivity(intent);
                    } catch (Exception exx) {

                    }
                }
            }
        }
    }

    public boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE = "io.github.subhamtyagi.privacyapplock/com.lzx.lock.service.LockAccessibilityService";
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            //setting not found so your phone is not supported
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessabilityService = mStringColonSplitter.next();
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void gotoCreatePwdActivity() {
        Intent intent2 = new Intent(SplashActivity.this, CreatePwdActivity.class);
        startActivity(intent2);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animator = null;
    }


}
