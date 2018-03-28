package com.fanfan.robot.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.novel.activity.DataNavigationActivity;
import com.fanfan.novel.activity.DataSiteActivity;
import com.fanfan.novel.activity.DataVideoActivity;
import com.fanfan.novel.activity.DataVoiceActivity;
import com.fanfan.novel.activity.FaceDataActivity;
import com.fanfan.novel.activity.SelectCtiyActivity;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.map.activity.AMapActivity;
import com.fanfan.novel.utils.AppUtil;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.novel.utils.UpdateUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.fragment.ImportFragment;
import com.fanfan.robot.fragment.XfFragment;
import com.fanfan.robot.train.PanoramicMapActivity;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.base.Constant;
import com.fanfan.youtu.api.hfrobot.bean.UpdateProgram;
import com.fanfan.youtu.api.hfrobot.event.DownLoadEvent;
import com.fanfan.youtu.api.hfrobot.event.UpdateProgramEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by android on 2018/1/6.
 */

public class SettingActivity extends BarBaseActivity implements UpdateUtil.OnDownloadListener {

    public static final int LOGOUT_TO_MAIN_REQUESTCODE = 102;
    public static final int LOGOUT_TO_MAIN_RESULTCODE = 202;

    public static final int ChannelId = 22;

    @BindView(R.id.add_video)
    RelativeLayout addVideo;
    @BindView(R.id.add_voice)
    RelativeLayout addVoice;
    @BindView(R.id.add_navigation)
    RelativeLayout addNavigation;
    @BindView(R.id.add_site)
    RelativeLayout addSite;
    @BindView(R.id.import_layout)
    RelativeLayout importLayout;
    @BindView(R.id.rl_face)
    RelativeLayout rlFace;
    @BindView(R.id.rl_dance)
    RelativeLayout rlDance;
    @BindView(R.id.tv_xf)
    TextView tvXf;
    @BindView(R.id.logout)
    TextView logout;
    @BindView(R.id.rl_city)
    RelativeLayout rlCity;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.set_test)
    LinearLayout setTest;
    @BindView(R.id.tv_map)
    TextView tvMap;
    @BindView(R.id.tv_vr)
    TextView tvVr;
    @BindView(R.id.tv_cur_code)
    TextView tvCurCode;

    private Youtucode youtucode;

    private UpdateProgram.UpdateProgramBean appVerBean;

    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;

    private Dialog loadingDialog;
    private View loadingView;
    private boolean loadShowing;
    private ProgressBar pb;
    private TextView tvProgress;
    private TextView tvBackstage;


    private MaterialDialog materialDialog;

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {

        youtucode = Youtucode.getSingleInstance();

        tvCity.setText(RobotInfo.getInstance().getCityName());

        if (Constants.unusual) {
            setTest.setVisibility(View.VISIBLE);
        } else {
            setTest.setVisibility(View.GONE);
        }

        tvCurCode.setText(String.format("当前版本 v %s", AppUtil.getVersionName(this)));
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @OnClick({R.id.add_video, R.id.add_voice, R.id.add_navigation, R.id.add_site, R.id.import_layout,
            R.id.rl_face, R.id.rl_dance, R.id.tv_xf, R.id.logout, R.id.rl_city, R.id.tv_vr, R.id.tv_map, R.id.rl_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_video:
                DataVideoActivity.newInstance(this);
                break;
            case R.id.add_voice:
                DataVoiceActivity.newInstance(this);
                break;
            case R.id.add_navigation:
                DataNavigationActivity.newInstance(this);
                break;
            case R.id.add_site:
                DataSiteActivity.newInstance(this);
                break;
            case R.id.import_layout:
                showLoading();
                ImportFragment importFragment = ImportFragment.newInstance();
                importFragment.show(getSupportFragmentManager(), "IMPORT");
                break;
            case R.id.rl_face:
                FaceDataActivity.newInstance(this);
                break;
            case R.id.rl_dance:
                DanceAddActivity.newInstance(this);
                break;
            case R.id.tv_xf:
                XfFragment xfFragment = XfFragment.newInstance();
                xfFragment.show(getSupportFragmentManager(), "CHANNEL");
                break;
            case R.id.logout:
                setResult(LOGOUT_TO_MAIN_RESULTCODE);
                finish();
                break;
            case R.id.rl_city:
                SelectCtiyActivity.newInstance(this);
                break;
            case R.id.tv_vr:
                PanoramicMapActivity.newInstance(this);
                break;
            case R.id.tv_map:
                AMapActivity.newInstance(this);
                break;
            case R.id.rl_update:
                youtucode.updateProgram();
                break;
        }
    }

    public void showLoading() {
        if (materialDialog == null) {
            materialDialog = new MaterialDialog.Builder(this)
                    .title("请稍等...")
                    .content("正在获取中...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .build();
        }
        materialDialog.show();
    }

    public void dismissLoading() {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
            materialDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectCtiyActivity.CITY_REQUEST_CODE) {
            if (resultCode == SelectCtiyActivity.CITY_RESULT_CODE) {
                String city = data.getStringExtra(SelectCtiyActivity.RESULT_CODE);
                tvCity.setText(city);
                RobotInfo.getInstance().setCityName(city);
            }
        }
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(UpdateProgramEvent event) {
        if (event.isOk()) {
            UpdateProgram updateProgram = event.getBean();
            Print.e(updateProgram);
            if (updateProgram.getCode() == 0) {
                appVerBean = updateProgram.getUpdateProgram();
                int curVersion = AppUtil.getVersionCode(this);
                int newversioncode = appVerBean.getVersionCode();

                if (curVersion < newversioncode) {
                    DialogUtils.showBasicNoTitleDialog(this, "版本更新", "暂不更新", "更新",
                            new DialogUtils.OnNiftyDialogListener() {
                                @Override
                                public void onClickLeft() {

                                }

                                @Override
                                public void onClickRight() {
                                    youtucode.downloadFileWithDynamicUrlSync(Constant.API_ROBOT_BASE + "files/robot.apk");
                                }
                            });
                } else {
                    showToast("暂时没有检测到新版本");
                }
            } else {
                onError(updateProgram.getCode(), updateProgram.getMsg());
            }
        } else {
            onError(event);
        }
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResultEvent(DownLoadEvent event) {
        if (event.isOk()) {
            ResponseBody body = event.getBean();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mBuilder = createNotification(SettingActivity.this);
                    mManager.notify(ChannelId, mBuilder.build());

                    loadShowing = true;
                    showLoadingDialog(0);
                }
            });

            UpdateUtil.writeResponseBodyToDisk(this, body, appVerBean.getAppName(), this);

        } else {
            onError(event);
        }
    }

    /**
     * 通知创建
     *
     * @param context
     * @return
     */
    private static NotificationCompat.Builder createNotification(Context context) {
        final String CHANNEL_ID = "0", CHANNEL_NAME = "ALLEN_NOTIFICATION";
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(notificationChannel);
        }
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setTicker(context.getString(R.string.downloading));
        builder.setContentText(String.format(context.getString(R.string.download_progress), 0));

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
        return builder;
    }

    @Override
    public void onDownloadFailed() {
        dismissAllDialog();
        showFailDialog();
        Intent intent = new Intent(this, SettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContentText(getString(R.string.download_fail));
        mBuilder.setProgress(100, 0, false);
        mManager.notify(ChannelId, mBuilder.build());
    }

    @Override
    public void onDownloadSuccess(File downFile) {
        dismissAllDialog();
        mManager.cancel(ChannelId);

        AppUtil.installNormal(this, downFile);
    }

    @Override
    public void onDownloading(int progress) {
        mBuilder.setContentIntent(null);
        mBuilder.setContentText(String.format(getString(R.string.download_progress), progress));
        mBuilder.setProgress(100, progress, false);
        mBuilder.setDefaults(0);
        mManager.notify(ChannelId, mBuilder.build());

        showLoadingDialog(progress);
    }

    /**
     * 进度条显示
     *
     * @param currentProgress
     */
    private void showLoadingDialog(int currentProgress) {
        if (!loadShowing) {
            return;
        }
        Print.e(currentProgress);
        if (loadingDialog == null) {
            loadingView = LayoutInflater.from(this).inflate(R.layout.downloading_layout, null);
            loadingDialog = new AlertDialog.Builder(this).setTitle("").setView(loadingView).create();
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
            pb = loadingView.findViewById(R.id.pb);
            tvProgress = loadingView.findViewById(R.id.tv_progress);
            tvBackstage = loadingView.findViewById(R.id.tv_backstage);
            tvBackstage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAllDialog();
                }
            });
        }

        tvProgress.setText(String.format(getString(R.string.progress), currentProgress));
        pb.setProgress(currentProgress);

        loadingDialog.show();
    }

    private void dismissAllDialog() {
        loadShowing = false;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * 下载失败提示
     */
    private void showFailDialog() {
        DialogUtils.showBasicNoTitleDialog(this, getString(R.string.download_fail_retry), "取消", "确定",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {

                    }

                    @Override
                    public void onClickRight() {
                        youtucode.downloadFileWithDynamicUrlSync(Constant.API_ROBOT_BASE + "files/robot.apk");
                    }
                });
    }


}
