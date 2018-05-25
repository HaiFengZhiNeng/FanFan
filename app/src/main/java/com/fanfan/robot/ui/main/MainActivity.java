package com.fanfan.robot.ui.main;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.robot.app.NovelApp;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.RobotType;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.db.manager.VoiceDBManager;
import com.fanfan.novel.im.init.LoginBusiness;
import com.fanfan.robot.listener.base.recog.AlarmListener;
import com.fanfan.robot.listener.base.recog.IRecogListener;
import com.fanfan.robot.listener.base.recog.cloud.MyRecognizer;
import com.fanfan.robot.listener.base.synthesizer.EarListener;
import com.fanfan.robot.listener.base.synthesizer.ISynthListener;
import com.fanfan.robot.listener.base.synthesizer.cloud.MySynthesizer;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.RobotBean;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.model.SpeakBean;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.model.VoiceBean;
import com.fanfan.robot.model.xf.Cookbook;
import com.fanfan.robot.model.xf.News;
import com.fanfan.robot.model.xf.Poetry;
import com.fanfan.robot.model.xf.englishEveryday.EnglishEveryday;
import com.fanfan.robot.model.xf.radio.Radio;
import com.fanfan.robot.model.xf.train.Train;
import com.fanfan.novel.pointdown.db.DownloadDBDao;
import com.fanfan.novel.pointdown.model.Progress;
import com.fanfan.robot.presenter.ChatPresenter;
import com.fanfan.robot.presenter.LineSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.IChatPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ISynthesizerPresenter;
import com.fanfan.robot.service.LoadFileService;
import com.fanfan.robot.service.PlayService;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.service.SpeakService;
import com.fanfan.robot.service.UdpService;
import com.fanfan.robot.other.cache.MusicCache;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.listener.music.EventCallback;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.system.PreferencesUtils;
import com.fanfan.novel.utils.customtabs.IntentUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.dagger.componet.DaggerMainComponet;
import com.fanfan.robot.dagger.module.MainModule;
import com.fanfan.robot.db.manager.DanceDBManager;
import com.fanfan.robot.model.Dance;
import com.fanfan.robot.presenter.ipersenter.ILineSoundPresenter;
import com.fanfan.robot.ui.auxiliary.PPTActivity;
import com.fanfan.robot.ui.auxiliary.PanoramicMapActivity;
import com.fanfan.robot.ui.face.FaceRecognitionActivity;
import com.fanfan.robot.ui.face.act.DetectfaceActivity;
import com.fanfan.robot.ui.map.AMapActivity;
import com.fanfan.robot.ui.media.MultimediaActivity;
import com.fanfan.robot.ui.media.act.DanceActivity;
import com.fanfan.robot.ui.naviga.NavigationActivity;
import com.fanfan.robot.ui.setting.SettingActivity;
import com.fanfan.robot.ui.site.PublicNumberActivity;
import com.fanfan.robot.ui.video.VideoIntroductionActivity;
import com.fanfan.robot.ui.voice.ProblemConsultingActivity;
import com.fanfan.robot.view.ChatTextView;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.base.Constant;
import com.fanfan.youtu.api.hfrobot.bean.Check;
import com.fanfan.youtu.api.hfrobot.bean.RequestProblem;
import com.fanfan.youtu.api.hfrobot.bean.SetBean;
import com.fanfan.youtu.api.hfrobot.event.CheckEvent;
import com.fanfan.youtu.api.hfrobot.event.RequestProblemEvent;
import com.fanfan.youtu.api.hfrobot.event.SetEvent;
import com.fanfan.youtu.utils.GsonUtil;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.iflytek.cloud.SpeechConstant;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.TIMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BarBaseActivity implements ISynthesizerPresenter.ITtsView, IChatPresenter.IChatView,
        ISerialPresenter.ISerialView, ILineSoundPresenter.ILineSoundView {

    @BindView(R.id.iv_fanfan)
    ImageView ivFanfan;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.iv_problem)
    ImageView ivProblem;
    @BindView(R.id.iv_multi_media)
    ImageView ivMultiMedia;
    @BindView(R.id.iv_face)
    ImageView ivFace;
    @BindView(R.id.iv_seting_up)
    ImageView ivSetingUp;
    @BindView(R.id.iv_public)
    ImageView ivPublic;
    @BindView(R.id.iv_navigation)
    ImageView ivNavigation;
    @BindView(R.id.chat_content)
    ChatTextView chatContent;

    private boolean quit;

//    @Inject
//    MainManager mMainManager;

    private VoiceDBManager mVoiceDBManager;

    private ServiceConnection mPlayServiceConnection;

    private MaterialDialog materialDialog;

    private boolean isPlay;

    private Youtucode youtucode;

    private int id;

    private SpeakService.SpeakBinder mSpeakBinder;

    private ServiceConnection mSpeakConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSpeakBinder = (SpeakService.SpeakBinder) service;
            mSpeakBinder.initSpeakManager(MainActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Runnable speakRunnable = new Runnable() {
        @Override
        public void run() {
            SpeakBean speakBean = null;
            if (!mySynthesizer.isSpeaking()) {
                if (mSpeakBinder != null) {
                    speakBean = mSpeakBinder.getSpeakMorestr();
                }
            }
            if (speakBean == null) {
                mHandler.postDelayed(speakRunnable, 1000);
            } else {
                if (System.currentTimeMillis() - speakBean.getTime() < 3000) {
                    if (mySynthesizer.isSpeaking()) {
                        mSpeakBinder.dispatchSpeak(speakBean);
                    } else {
                        if (speakBean.isUrl()) {
                            doUrl(speakBean.getAnwer());
                        } else {
                            doAnswer(speakBean.getAnwer());
                            if (speakBean.isAction()) {
                                speakingAddAction(speakBean.getAnwer().length());
                            }
                        }
                    }
                } else {
                    Print.e("时间过长");
                    mHandler.postDelayed(speakRunnable, 1000);
                }
            }
        }
    };

    private MyRecognizer myRecognizer;
    private boolean isOpening;

    private MySynthesizer mySynthesizer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main1;
    }

    @Inject
    ChatPresenter mChatPresenter;
    @Inject
    SerialPresenter mSerialPresenter;
    @Inject
    LineSoundPresenter mSoundPresenter;

    @Override
    protected void initView() {
        super.initView();

        DaggerMainComponet.builder().mainModule(new MainModule(this)).build().inject(this);

        mSerialPresenter.start();
        mSoundPresenter.start();

        youtucode = Youtucode.getSingleInstance();

        youtucode.updateProgram(1);

        Intent intent = new Intent(this, SpeakService.class);
        bindService(intent, mSpeakConn, BIND_AUTO_CREATE);

        IRecogListener iRecogListener = new AlarmListener() {
            @Override
            public void onAsrFinalResult(String result) {
                super.onAsrFinalResult(result);
                aiuiForLocal(result);
                startRecognizerListener(false);
            }

            @Override
            public void onAsrOnlineNluResult(int type, String nluResult) {
                super.onAsrOnlineNluResult(type, nluResult);
                if (type == STATUS_END) {
                    startRecognizerListener(false);
                }
            }

            @Override
            public void onAsrFinishError(int errorCode, String errorMessage) {
                super.onAsrFinishError(errorCode, errorMessage);
                startRecognizerListener(false);
            }
        };
        myRecognizer = new MyRecognizer(this, iRecogListener);

        ISynthListener iSynthListener = new EarListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                onRunable();
            }
        };
        mySynthesizer = new MySynthesizer(this, iSynthListener);
    }

    @Override
    protected void initData() {
        mVoiceDBManager = new VoiceDBManager();

        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);

    }

    @Override
    protected void callStop() {
        mySynthesizer.stop();
        mSoundPresenter.stopVoice();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isOpening = true;
        Constants.isDance = false;

        RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);

        mChatPresenter.start();

        mySynthesizer.onResume();

        myRecognizer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        isOpening = false;

        mSpeakBinder.clear();

        myRecognizer.onPause();

        mySynthesizer.onPause();


        setChatView(false);
        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);

        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);

        mSoundPresenter.stopVoice();

        mChatPresenter.finish();
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        if (mPlayServiceConnection != null) {
            unbindService(mPlayServiceConnection);
        }
        stopService(new Intent(this, UdpService.class));
        stopService(new Intent(this, SerialService.class));
        if (mSpeakConn != null) {
            unbindService(mSpeakConn);
        }
        super.onDestroy();

        mSoundPresenter.finish();

        myRecognizer.release();
        mySynthesizer.release();
    }

    @Override
    public void onBackPressed() {
        if (!quit) {
            showToast("再按一次退出程序");
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    quit = false;
                }
            }, 2000);
            quit = true;
        } else {
            super.onBackPressed();
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @OnClick({R.id.iv_fanfan, R.id.iv_video, R.id.iv_problem, R.id.iv_multi_media, R.id.iv_face, R.id.iv_seting_up,
            R.id.iv_public, R.id.iv_navigation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_fanfan:
                animateSequentially(ivFanfan);
                break;
            case R.id.iv_video:
                VideoIntroductionActivity.newInstance(this);
                break;
            case R.id.iv_problem:
                ProblemConsultingActivity.newInstance(this);
                break;
            case R.id.iv_multi_media:
                bindService(false);
                break;
            case R.id.iv_face:
//                FaceRecognitionActivity.newInstance(this);
                DetectfaceActivity.newInstance(this);
                break;
            case R.id.iv_seting_up:
                clickSetting();
                break;
            case R.id.iv_public:
                PublicNumberActivity.newInstance(this);
                break;
            case R.id.iv_navigation:
                NavigationActivity.newInstance(this);
                break;
        }
    }


    public void startRecognizerListener(boolean focus) {
        myRecognizer.start(focus);
    }

    public void stopRecognizerListener() {
//        myRecognizer.stop();
    }

    public void doUrl(String url) {

        mSoundPresenter.playVoice(url);
    }

    public void doAnswer(String messageContent) {
        stopSound();
        mySynthesizer.speak(messageContent);
        onSpeakBegin(messageContent);
    }

    private String mInput;

    private void clickSetting() {
        ivSetingUp.setEnabled(false);
        youtucode.selectSet(UserInfo.getInstance().getIdentifier());
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(SetEvent event) {
        ivSetingUp.setEnabled(true);
        if (event.isOk()) {
            SetBean bean = event.getBean();
            Print.e(bean);
            if (bean.getCode() == 0) {
                showSetDialog(bean.getData());
            } else if (bean.getCode() == 1) {
                SettingActivity.newInstance(MainActivity.this, SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE);
            } else {
                onError(bean.getCode(), bean.getMsg());
            }
        } else {
            onError(event);
        }
    }

    private void showSetDialog(final SetBean.Data data) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.title_setting_pwd)
                .inputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .inputRange(6, 10)
                .alwaysCallInputCallback()
                .input(getString(R.string.input_hint_pwd), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        mInput = String.valueOf(input);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onCompleted();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (data.getSet_pwd().equals(mInput)) {
                            SettingActivity.newInstance(MainActivity.this, SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE);
                        } else {
                            showMsg("密码错误");
                        }
                    }
                })
                .build();
        materialDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return false;
            }
        });
        materialDialog.setCancelable(false);
        materialDialog.show();
    }

    private void bindService(boolean isPlay) {
        this.isPlay = isPlay;
        if (!PreferencesUtils.getBoolean(MainActivity.this, Constants.MUSIC_UPDATE, false))
            showLoading();
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        mPlayServiceConnection = new PlayServiceConnection();
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE) {
            if (resultCode == SettingActivity.LOGOUT_TO_MAIN_RESULTCODE) {
                spakeLogout();
            }
        } else if (requestCode == MultimediaActivity.MULTIMEDIA_REQUESTCODE) {
            if (resultCode == MultimediaActivity.MULTIMEDIA_RESULTCODE) {
                Print.e("断开与音乐服务的连接");
                unbindService(mPlayServiceConnection);
                mChatPresenter.start();
                myRecognizer.onResume();
                mySynthesizer.onResume();
            }
        }
    }

    //**********************************************************************************************

    private boolean isSuspendAction;
    private boolean isAutoAction;

    private void sendOrder(int type, String motion) {
        receiveMotion(type, motion);
    }

    private void sendCustom(RobotBean localVoice) {
        sendCustomMessage(localVoice);
    }

    public void sendCustomMessage(RobotBean robotBean) {
        mChatPresenter.sendCustomMessage(robotBean);
    }

    public void receiveMotion(int type, String motion) {
        mSerialPresenter.receiveMotion(type, motion);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP://19
                if (!isSuspendAction) {
                    sendMsg(keyCode);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN://20
                if (!isSuspendAction) {
                    sendMsg(keyCode);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT://21
                if (!isSuspendAction) {
                    sendMsg(keyCode);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT://20
                if (!isSuspendAction) {
                    sendMsg(keyCode);
                }
                break;
            case KeyEvent.KEYCODE_BUTTON_L1:
//                onEventLR();
                break;
            case KeyEvent.KEYCODE_BUTTON_R1:
//                onEventLR();
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                sendOrder(SerialService.DEV_BAUDRATE, "A50C80F3AA");
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                sendOrder(SerialService.DEV_BAUDRATE, "A50C80F2AA");
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                sendAutoAction();
                break;
            case KeyEvent.KEYCODE_BUTTON_A:
                stopAutoAction();
                break;
        }
        return false;
    }

    private void sendMsg(final int keyCode) {
        isSuspendAction = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP://19
                        sendOrder(SerialService.DEV_BAUDRATE, "A5038002AA");
                        Print.e("up");
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN://20
                        sendOrder(SerialService.DEV_BAUDRATE, "A5038008AA");
                        Print.e("down");
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT://21
                        sendOrder(SerialService.DEV_BAUDRATE, "A5038004AA");
                        Print.e("left");
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT://20
                        sendOrder(SerialService.DEV_BAUDRATE, "A5038006AA");
                        Print.e("right");
                        break;
                    default:
                        isSuspendAction = false;
                }

            }
        }, 500);
    }

    public void sendAutoAction() {
        if (isAutoAction) {
            stopAutoAction();
        } else {
            isAutoAction = true;
            sendOrder(SerialService.DEV_BAUDRATE, "A503800AAA");
            Print.e("自由运动(开)");
            mHandler.postDelayed(runnable, 200);
        }
    }

    public void stopAutoAction() {
        if (isAutoAction) {
            Print.e("自由运动(关)");
            mHandler.removeCallbacks(runnable);
            sendOrder(SerialService.DEV_BAUDRATE, "A5038005AA");
            isAutoAction = false;
        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoAction) {
                sendOrder(SerialService.DEV_BAUDRATE, "A503800AAA");
                mHandler.postDelayed(runnable, 200);
                Print.e("自由运动(开)");
            }
        }
    };

    //**********************************************************************************************

    private void addSpeakAnswer(String problem, String messageContent, boolean isAction, boolean isUrl) {
        mSoundPresenter.stopVoice();
        stopRecognizerListener();
//        doAnswer(messageContent);
        if (mySynthesizer.isSpeaking()) {
            SpeakBean speakBean = new SpeakBean(problem, messageContent, System.currentTimeMillis(), isAction, isUrl);
            mSpeakBinder.dispatchSpeak(speakBean);
        } else {
            if (isUrl) {
                doUrl(messageContent);
            } else {
                doAnswer(messageContent);
                if (isAction) {
                    speakingAddAction(messageContent.length());
                }
            }
        }
    }

    private void setChatContent(String messageContent) {
        chatContent.setSpanText(mHandler, messageContent, true);
    }

    private void speakingAddAction(int length) {
//        if (length <= 13) {
//            mSerialPresenter.sendOrder(SerialService.DEV_BAUDRATE, "A50C8001AA");
//        } else if (length > 13 && length <= 40) {
//            mSerialPresenter.sendOrder(SerialService.DEV_BAUDRATE, "A50C8003AA");
//        } else {
//            mSerialPresenter.sendOrder(SerialService.DEV_BAUDRATE, "A50C8021AA");
//        }
        sendOrder(SerialService.DEV_BAUDRATE, Constants.SPEAK_ACTION);
    }

    //************************anim****************************
    protected void animateSequentially(View view) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                FanFanIntroduceActivity.newInstance(MainActivity.this);
                PPTActivity.newInstance(MainActivity.this);
            }
        }, 400);
        ViewAnimator
                .animate(view)
                .scale(1f, 1.3f, 1f)
                .alpha(1, 0.3f, 1)
                .translationX(0, 200, 0)
                .translationY(0, 300, 0)
                .interpolator(new LinearInterpolator())
                .duration(1200)
                .start();
    }

    //**********************************************************************************************
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResultEvent(ReceiveEvent event) {
        if (event.isOk()) {
            DatagramPacket packet = event.getBean();
            if (!SocketManager.getInstance().isGetTcpIp) {
                SocketManager.getInstance().setUdpIp(packet.getAddress().getHostAddress(), packet.getPort());
            }
            String recvStr = new String(packet.getData(), 0, packet.getLength());
            Print.e("udp发送过来消息 ： " + recvStr);
            sendOrder(SerialService.DEV_BAUDRATE, recvStr);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ServiceToActivityEvent event) {
        if (event.isOk()) {
            SerialBean serialBean = event.getBean();
            mSerialPresenter.onDataReceiverd(serialBean);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(CheckEvent event) {
        if (event.isOk()) {
            Check check = event.getBean();
            Print.e(check);
            if (check.getCode() == 0) {
                Check.CheckBean appVerBean = check.getCheck();
                int curVersion = AppUtil.getVersionCode(this);
                int newversioncode = appVerBean.getVersionCode();

                if (curVersion < newversioncode) {

                    Progress progress = DownloadDBDao.getInstance().get(Constant.APK_URL);
                    if (progress == null) {
                        return;
                    }
                    if (progress.status != Progress.FINISH) {
                        return;
                    }
                    final File file = new File(progress.folder, progress.fileName);
                    if (!file.exists()) {
                        return;
                    }
                    long fileSize = FileUtil.getFileSize(file);
                    if (progress.totalSize != fileSize) {
                        return;
                    }
                    DialogUtils.showBasicNoTitleDialog(this, getString(R.string.download_check_finish), "取消", "安装",
                            new DialogUtils.OnNiftyDialogListener() {
                                @Override
                                public void onClickLeft() {

                                }

                                @Override
                                public void onClickRight() {
                                    stopService(new Intent(MainActivity.this, LoadFileService.class));
                                    AppUtil.installNormal(MainActivity.this, file);
                                }
                            });
                } else {
                    Print.e("暂时没有检测到新版本");
                }
            } else {
                onError(check.getCode(), check.getMsg());
            }
        } else {
            onError(event);
        }
    }

    //**********************************************************************************************
    @Override
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

    @Override
    public void dismissLoading() {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
            materialDialog = null;
        }
    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void showMsg(int msg) {
        showToast(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    //**********************************************************************************************
    @Override
    public void onSpeakBegin(String answer) {

        setChatContent(answer);
        setChatView(true);
        loadImage(R.mipmap.fanfan_lift_hand, R.mipmap.fanfan_hand);
    }

    @Override
    public void onRunable() {
        setChatView(false);
        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);
        sendOrder(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        startRecognizerListener(true);
        mHandler.post(speakRunnable);
    }

    @Override
    public void stopSound() {
        mSoundPresenter.stopVoice();
        stopRecognizerListener();
    }

    private void loadImage(int load, int place) {
        ImageLoader.loadImage(NovelApp.getInstance().getApplicationContext(), ivFanfan, load, false, place, 1000);
    }


    private void setChatView(boolean isShow) {
        if (isShow) {
            ViewAnimator
                    .animate(chatContent)
                    .alpha(0, 1)
                    .interpolator(new LinearInterpolator())
                    .duration(300)
                    .onStart(new AnimationListener.Start() {
                        @Override
                        public void onStart() {

                        }
                    })
                    .onStop(new AnimationListener.Stop() {
                        @Override
                        public void onStop() {
                            chatContent.setVisibility(View.VISIBLE);
                        }
                    })
                    .start();
        } else {
            ViewAnimator
                    .animate(chatContent)
                    .alpha(1, 0)
                    .interpolator(new LinearInterpolator())
                    .duration(1000)
                    .onStart(new AnimationListener.Start() {
                        @Override
                        public void onStart() {

                        }
                    })
                    .onStop(new AnimationListener.Stop() {
                        @Override
                        public void onStop() {
                            chatContent.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    //**********************************************************************************************
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        Print.i("onSendMessageSuccess : 发送消息成功");
    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        Print.e("onSendMessageFail : 发送消息失败");
    }

    @Override
    public void parseMsgcomplete(String str) {
        addSpeakAnswer("TextMsg", str, true, false);
    }

    @Override
    public void parseCustomMsgcomplete(String customMsg) {
        RobotBean bean = GsonUtil.GsonToBean(customMsg, RobotBean.class);
        if (bean == null) {
            return;
        }
        if (bean.getType() == null || bean.getType().equals("")) {
            return;
        }
        if (bean.getOrder() == null || bean.getOrder().equals("")) {
            return;
        }
        RobotType robotType = bean.getType();
        switch (robotType) {
            case AutoAction:
                break;
            case VoiceSwitch:
                boolean isSpeech = bean.getOrder().equals("语音开");
                setSpeech(isSpeech);
                break;
            case Text:
                addSpeakAnswer("CustomMsg", bean.getOrder(), true, false);
                break;
            case SmartChat:

                break;
            case Motion:
                sendOrder(SerialService.DEV_BAUDRATE, bean.getOrder());
                break;
            case GETIP:
                Constants.CONNECT_IP = bean.getOrder();
                if (Constants.IP != null && Constants.PORT > 0) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("robotIp", Constants.IP);
                        object.put("robotPort", Constants.PORT);
                        RobotBean robotBean = new RobotBean();
                        robotBean.setOrder(object.toString());
                        robotBean.setType(RobotType.GETIP);
                        Print.e("发送: " + object.toString());
                        showToast("发送: " + object.toString());
                        sendCustom(robotBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("robotIp", "");
                        object.put("robotPort", Constants.PORT);
                        RobotBean robotBean = new RobotBean();
                        robotBean.setOrder(object.toString());
                        robotBean.setType(RobotType.GETIP);
                        Print.e("发送: " + object.toString());
                        sendCustom(robotBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case LocalVoice:
                List<VoiceBean> voiceBeanList = mVoiceDBManager.loadAll();
                List<String> anwers = new ArrayList<>();
                if (voiceBeanList != null && voiceBeanList.size() > 0) {
                    for (VoiceBean voiceBean : voiceBeanList) {
                        anwers.add(voiceBean.getShowTitle());
                    }
                    String voiceJson = GsonUtil.GsonString(anwers);
                    RobotBean localVoice = new RobotBean();
                    localVoice.setType(RobotType.LocalVoice);
                    localVoice.setOrder(voiceJson);
                    sendCustom(localVoice);
                }
                break;
            case Anwser:
                mSoundPresenter.stopVoice();
                stopRecognizerListener();
                aiuiForLocal(bean.getOrder());
                break;
        }
    }

    @Override
    public void parseServerMsgcomplete(String txt) {
        Print.e("接收到客服发来的消息： " + txt);

        txt = txt.trim();
        if (txt.equals("a")) {
            mSoundPresenter.stopVoice();
            stopRecognizerListener();
        } else if (txt.equals("s")) {
            startRecognizerListener(false);
        } else if (txt.equals("g")) {
            stopAll();

        } else if (txt.equals("h")) {//前进
            sendOrder(SerialService.DEV_BAUDRATE, "A5038002AA");

        } else if (txt.equals("j")) {//后退
            sendOrder(SerialService.DEV_BAUDRATE, "A5038008AA");

        } else if (txt.equals("k")) {//左转
            sendOrder(SerialService.DEV_BAUDRATE, "A5038004AA");

        } else if (txt.equals("l")) {//右转
            sendOrder(SerialService.DEV_BAUDRATE, "A5038006AA");

        } else if (txt.equals("q")) {
            animateSequentially(ivFanfan);

        } else if (txt.equals("w")) {
            ProblemConsultingActivity.newInstance(this);

        } else if (txt.equals("e")) {
            VideoIntroductionActivity.newInstance(this);
        } else if (txt.equals("r")) {
            NavigationActivity.newInstance(this);
        } else if (txt.equals("t")) {
            if (Constants.isDance) {
                Print.e("正在跳舞，return");
                return;
            }
            beginDance("ServerMsg");
        } else if (txt.equals("u")) {
            sendOrder(SerialService.DEV_BAUDRATE, "A50C800CAA");
            addSpeakAnswer("ServerMsg", "你好", false, false);
        } else if (txt.equals("d")) {
            if (isAutoAction) {
                return;
            }
            sendAutoAction();
        } else if (txt.equals("f")) {
            stopAutoAction();
        } else {
            if (txt.equals("1") || txt.equals("2") || txt.equals("3") || txt.equals("4") || txt.equals("5")
                    || txt.equals("6") || txt.equals("7") || txt.equals("8") || txt.equals("9")) {
                int i = Integer.valueOf(txt);
                switch (i) {
                    case 1:
                        txt = "正在为您检索答案，请您稍等";
                        break;
                    case 2:
                        txt = "数据库中未检索到符合的答案，如需咨询业务相关，您可以对我说“业务办理”";
                        break;
                    case 3:
                        txt = "您好，请问您想办理什么业务";
                        break;
                    case 4:
                        txt = "您好，很高兴见到您";
                        break;
                    case 5:
                        txt = "这个问题好难啊，我还没学过这个，您能教我么";
                        break;
                    case 6:
                        txt = "领导您好，我是境境，希望您能喜欢我";
                        break;
                    case 7:
                        txt = "我是服务机器人境境";
                        break;
                    case 8:
                        txt = "您在我前面站了这么久  要和我说点什么么";
                        break;
                    case 9:
                        txt = "您好，您可以和我对话聊天，提问解答，我很乐意为您服务";
                        break;

                }
            }
            addSpeakAnswer("ServerMsg", txt, true, false);
        }
    }

    private void beginDance(final String problem) {
        Constants.isDance = true;
        addSpeakAnswer(problem, "请您欣赏舞蹈", false, false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DanceDBManager danceDBManager = new DanceDBManager();
                List<Dance> dances = danceDBManager.loadAll();
                if (dances != null && dances.size() > 0) {
                    Dance dance = dances.get(0);
                    DanceActivity.newInstance(MainActivity.this, dance.getId());
                } else {
                    addSpeakAnswer(problem, "本地暂未添加舞蹈，请到设置或多媒体中添加舞蹈", true, false);
                }
            }
        }, 2000);
    }

    //**********************************************************************************************
    @Override
    public void stopAll() {
        sendOrder(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        mSoundPresenter.stopVoice();
        stopRecognizerListener();
        String wakeUp = resFoFinal(R.array.wake_up);
        mySynthesizer.stop();
        mySynthesizer.speak(wakeUp);
    }

    @Override
    public void onMoveStop() {

    }

    @Override
    public void onAlarm(Alarm alarm) {

    }

    //**********************************************************************************************
    @Override
    public void aiuiForLocal(String result) {
        String unicode = result.replaceAll("\\p{P}", "");
        if (unicode.equals("百度")) {
            IntentUtil.openUrl(mContext, "http://www.baidu.com/");
        } else if (unicode.equals("新闻")) {
            IntentUtil.openUrl(mContext, "http://www.cepb.gov.cn/");
        } else {
            List<VoiceBean> voiceBeanList = mVoiceDBManager.loadAll();
            if (voiceBeanList != null && voiceBeanList.size() > 0) {
                for (VoiceBean voiceBean : voiceBeanList) {
                    if (voiceBean.getShowTitle().equals(unicode)) {
                        refHomePage(voiceBean);
                        return;
                    }
                }
            }
            mSoundPresenter.onlineResult(unicode);
        }
    }

    @Override
    public void doAiuiAnwer(String question, String anwer) {
        addSpeakAnswer(question, anwer, true, false);
    }

    @Override
    public void doAiuiUrl(String question, String url) {
        addSpeakAnswer(question, url, false, true);
    }

    @Override
    public void refHomePage(VoiceBean voiceBean) {
        if (voiceBean.getActionData() != null)
            sendOrder(SerialService.DEV_BAUDRATE, voiceBean.getActionData());
        if (voiceBean.getExpressionData() != null)
            sendOrder(SerialService.DEV_BAUDRATE, voiceBean.getExpressionData());

        addSpeakAnswer(voiceBean.getShowTitle(), voiceBean.getVoiceAnswer(), true, false);
    }


    @Override
    public void refHomePage(String question, String finalText) {
    }

    @Override
    public void refHomePage(String question, String finalText, String url) {
    }

    @Override
    public void refHomePage(String question, News news) {
    }

    @Override
    public void refHomePage(String question, Radio radio) {
    }

    @Override
    public void refHomePage(String question, Poetry poetry) {
    }

    @Override
    public void refHomePage(String question, Cookbook cookbook) {
    }

    @Override
    public void refHomePage(String question, EnglishEveryday englishEveryday) {
    }

    @Override
    public void special(String result, SpecialType type) {
        switch (type) {
            case Story:
                onCompleted();
                break;
            case Music:
                bindService(true);
                break;
            case Joke:
                onCompleted();
                break;
            case Dance:
                beginDance(result);
                break;
            case Hand:
                sendOrder(SerialService.DEV_BAUDRATE, "A50C800CAA");
                addSpeakAnswer(result, "你好", false, false);
                break;
        }
    }

    @Override
    public void doCallPhone(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void train(List<Train> trains) {

    }

    @Override
    public void startPage(SpecialType specialType) {
        switch (specialType) {
            case Fanfan:
                animateSequentially(ivFanfan);
                break;
            case Video:
                VideoIntroductionActivity.newInstance(this);
                break;
            case Problem:
                ProblemConsultingActivity.newInstance(this);
                break;
            case MultiMedia:
                bindService(false);
                break;
            case Face:
                FaceRecognitionActivity.newInstance(this);
                break;
            case Seting_up:
                clickSetting();
                break;
            case Public_num:
                PublicNumberActivity.newInstance(this);
                break;
            case Navigation:
                NavigationActivity.newInstance(this);
                break;
            default:
                onCompleted();
                break;
        }
    }

    @Override
    public void spakeMove(SpecialType type, String result) {
        onCompleted();
        switch (type) {
            case Forward:
                sendOrder(SerialService.DEV_BAUDRATE, "A5038002AA");
                break;
            case Backoff:
                sendOrder(SerialService.DEV_BAUDRATE, "A5038008AA");
                break;
            case Turnleft:
                sendOrder(SerialService.DEV_BAUDRATE, "A5038004AA");
                break;
            case Turnright:
                sendOrder(SerialService.DEV_BAUDRATE, "A5038006AA");
                break;
        }
    }

    @Override
    public void openMap() {
        AMapActivity.newInstance(this);
    }

    @Override
    public void openVr() {
        PanoramicMapActivity.newInstance(this);
    }

    @Override
    public void spakeLogout() {
        LoginBusiness.logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                showMsg("退出登录失败，请稍后重试");
            }

            @Override
            public void onSuccess() {
//                liveLogout();
                logout();
            }
        });
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void noAnswer(String question) {
        String identifier = UserInfo.getInstance().getIdentifier();
        youtucode.requestProblem(identifier, question, id);
    }

    @Override
    public void setSpeech(boolean speech) {
        if (speech) {
            startRecognizerListener(false);
        } else {
            stopRecognizerListener();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(RequestProblemEvent event) {
        if (!event.isOk()) {
            onError(event);
            onCompleted();
            return;
        }
        String identifier = UserInfo.getInstance().getIdentifier();
        RequestProblem requestProblem = event.getBean();
        if (requestProblem.getCode() == 2) {//添加成功
            Print.e(requestProblem.getMsg());

            mChatPresenter.sendServerMessage(identifier, requestProblem.getQuestion());
            onCompleted();
        } else if (requestProblem.getCode() == 0) {//已经添加过，有答案
            RequestProblem.AnswerBean answerBean = requestProblem.getAnswerBean();
            if (answerBean == null) {
                mChatPresenter.sendServerMessage(identifier, requestProblem.getQuestion());
                onCompleted();
                return;
            }
            Print.e(requestProblem);
            String anwer = answerBean.getAnswer();
            id = answerBean.getId();
            if (anwer == null || anwer.length() < 1) {
                mChatPresenter.sendServerMessage(identifier, requestProblem.getQuestion());
                onCompleted();
            } else {
                addSpeakAnswer(requestProblem.getQuestion(), anwer, true, false);
            }
        } else {
            mChatPresenter.sendServerMessage(identifier, requestProblem.getQuestion());
            onError(requestProblem.getCode(), requestProblem.getMsg());
            onCompleted();
        }
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            MusicCache.get().setPlayService(playService);
            playService.updateMusicList(new EventCallback<Void>() {
                @Override
                public void onEvent(Void aVoid) {
                    dismissLoading();
                    MultimediaActivity.newInstance(MainActivity.this, isPlay, MultimediaActivity.MULTIMEDIA_REQUESTCODE);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

}
