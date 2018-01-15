package com.fanfan.novel.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.robot.R;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.base.BaseActivity;
import com.fanfan.novel.common.base.BaseHandler;
import com.fanfan.novel.im.MyTLSService;
import com.fanfan.novel.im.PushUtil;
import com.fanfan.novel.im.event.FriendshipEvent;
import com.fanfan.novel.im.event.GroupEvent;
import com.fanfan.novel.im.event.MessageEvent;
import com.fanfan.novel.im.event.RefreshEvent;
import com.fanfan.novel.im.init.InitBusiness;
import com.fanfan.novel.im.init.LoginBusiness;
import com.fanfan.novel.im.init.TlsBusiness;
import com.fanfan.novel.model.UserInfo;
import com.fanfan.novel.presenter.SplashPresenter;
import com.fanfan.novel.presenter.SplashView;
import com.fanfan.novel.service.SerialService;
import com.fanfan.novel.service.UdpService;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.novel.utils.PermissionsChecker;
import com.fanfan.novel.utils.PhoneUtil;
import com.fanfan.robot.activity.MainActivity;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;

/**
 * http://m.blog.csdn.net/guolin_blog/article/details/78582548
 * Created by android on 2017/12/25.
 */

public class SplashActivity extends BarBaseActivity implements SplashView, BaseHandler.HandleMessage, TIMCallBack {


    private SplashPresenter presenter;
    private PermissionsChecker mChecker;

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    //权限
    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    //登陆
    public static final int LOGIN_RESULT_CODE = 570;
    public static final int LOGIN_RESULTCODE = 571;
    public static final int BACK_RESULTCODE = 572;
    //handler
    private static final int REFRESH_COMPLETE = 0X153;

    //标志位确定SDK是否初始化，避免客户SDK未初始化的情况，实现可重入的init操作
    private static boolean isSDKInit = false;

    private Handler handler = new BaseHandler<>(SplashActivity.this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

        ImageView ivSplash = (ImageView) findViewById(R.id.iv_splash);

        mChecker = new PermissionsChecker(this);
        presenter = new SplashPresenter(this);

        Glide.with(this)
                .load(R.mipmap.splash_bg)
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                .transition(new DrawableTransitionOptions().crossFade(2000))
                .into(ivSplash);

        @SuppressLint("WrongConstant") WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Constants.displayWidth = wm.getDefaultDisplay().getWidth();
        Constants.displayHeight = wm.getDefaultDisplay().getHeight();
        Constants.IP = PhoneUtil.getWifiIP(this);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        Print.e("屏幕宽度 width : " + width);
        Print.e("屏幕高度 height : " + height);
        Print.e("屏幕密度 density : " + density);
        Print.e("屏幕密度DPI densityDpi : " + densityDpi);

        MediaScannerConnection.scanFile(this,
                new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()},
                null, new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        Print.e("onMediaScannerConnected");
                    }

                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                        Print.e("onScanCompleted : " + s);
                    }
                });
    }

//    ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator() {
//        @Override
//        public void animate(View view) {
//            view.setAlpha(0f);
//
//            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
//            fadeAnim.setDuration(2000);
//            fadeAnim.start();
//        }
//    };

    @Override
    protected void initData() {
        if (mChecker.lacksPermissions(PERMISSIONS)) {

            if (mChecker.lacksPermissions(PERMISSIONS)) {
                //请求权限
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

            } else {
                initIM(); // 全部权限都已获取
            }
        } else {
            initIM();
        }
    }

    @Override
    protected void setListener() {

    }

    private void initIM() {
        init();

        presenter = new SplashPresenter(this);
        presenter.start();
    }

    private void init() {
        if (isSDKInit)
            return;
        //初始化IMSDK
        InitBusiness.start(getApplicationContext());
        //初始化TLS
        TlsBusiness.init(getApplicationContext());

        ILiveSDK.getInstance().initSdk(getApplicationContext(), Constants.IMSDK_APPID, Constants.IMSDK_ACCOUNT_TYPE);

        isSDKInit = true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            initIM();
        } else {

            showMissingPermissionDialog();
        }
    }

    //含有全部的权限

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
            }
        });

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RESULT_CODE) {
            if (resultCode == LOGIN_RESULTCODE) {//判断登陆是否成功
                if (0 == MyTLSService.getInstance().getLastErrno()) {
                    navToHome();
                } else if (resultCode == RESULT_CANCELED) {
                    handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
                }
            } else if (resultCode == BACK_RESULTCODE) {
                handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
            }
        }
    }


    @Override
    public void navToHome() {
//        TIMUserConfig userConfig = TIMUserConfigSetting.setTIMUserConfig(this);
//        TIMManager.getInstance().setUserConfig(userConfig);
        RefreshEvent.getInstance().init();
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
        RefreshEvent.getInstance().init();

        LoginBusiness.loginIm(UserInfo.getInstance().getIdentifier(), UserInfo.getInstance().getUserSig(), this);
    }


    @Override
    public void navToLogin() {
        LoginActivity.newInstance(this, LOGIN_RESULT_CODE);
    }

    @Override
    public boolean isUserLogin() {
        String id = UserInfo.getInstance().getIdentifier();

        boolean isNeed = MyTLSService.getInstance().needLogin(id);

        boolean islogin = id != null && (!isNeed);
        return islogin;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case REFRESH_COMPLETE:
                finish();
                break;
        }
    }


    @Override
    public void onError(int i, String s) {
        Print.e("login error : code " + i + " " + s);
        switch (i) {
            case 6208:
                //离线状态下被其他终端踢下线
                showKickOutDialog(this);
                break;
            case 6200:
                Print.e("登陆失败， 当前没有网络");
                navToLogin();
                break;
            default:
                Print.e("登陆失败， 请稍后重试");
                navToLogin();
                break;
        }
    }

    @Override
    public void onSuccess() {
        loginSDK();

    }

    private void loginSDK() {
        ILiveLoginManager.getInstance().iLiveLogin(UserInfo.getInstance().getIdentifier(), UserInfo.getInstance().getUserSig(), new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Print.e("完成登陆 onSuccess");
                //初始化程序后台后消息推送
                PushUtil.getInstance();
                //初始化消息监听
                MessageEvent.getInstance();

                startService(new Intent(SplashActivity.this, UdpService.class));
                startService(new Intent(SplashActivity.this, SerialService.class));

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Print.e("login failed:" + module + "|" + errCode + "|" + errMsg);
                Print.e("登陆失败， 请稍后重试");
                navToLogin();
            }
        });
    }


    /**
     * 显示被踢下线通知
     *
     * @param context activity
     */
    private void showKickOutDialog(final Context context) {
        DialogUtils.showBasicIconDialog(SplashActivity.this, R.mipmap.ic_logo, "登陆提示",
                "您的帐号已在其它地方登陆", "退出", "重新登陆",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {
                        LoginBusiness.logout(new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {
                                if (SplashActivity.this != null) {
                                    Print.e("退出登录失败，请稍后重试");
                                }
                            }

                            @Override
                            public void onSuccess() {
                                sendBroadcast(new Intent(Constants.NET_LOONGGG_EXITAPP));
                            }
                        });
                    }

                    @Override
                    public void onClickRight() {
                        navToHome();
                    }
                });
    }

}
