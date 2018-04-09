package com.fanfan.robot.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.novel.activity.DanceActivity;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.enums.RobotType;
import com.fanfan.novel.common.enums.SpecialType;
import com.fanfan.novel.db.manager.VoiceDBManager;
import com.fanfan.novel.im.init.LoginBusiness;
import com.fanfan.novel.map.activity.AMapActivity;
import com.fanfan.novel.model.RobotBean;
import com.fanfan.novel.model.SerialBean;
import com.fanfan.novel.model.UserInfo;
import com.fanfan.novel.model.VoiceBean;
import com.fanfan.novel.model.xf.service.Cookbook;
import com.fanfan.novel.model.xf.service.News;
import com.fanfan.novel.model.xf.service.Poetry;
import com.fanfan.novel.model.xf.service.englishEveryday.EnglishEveryday;
import com.fanfan.novel.model.xf.service.radio.Radio;
import com.fanfan.novel.model.xf.service.train.Train;
import com.fanfan.novel.pointdown.db.DownloadDBDao;
import com.fanfan.novel.pointdown.model.Progress;
import com.fanfan.novel.presenter.ipresenter.IChatPresenter;
import com.fanfan.novel.presenter.ipresenter.ISerialPresenter;
import com.fanfan.novel.presenter.ipresenter.ISynthesizerPresenter;
import com.fanfan.novel.service.LoadFileService;
import com.fanfan.novel.service.PlayService;
import com.fanfan.novel.service.SerialService;
import com.fanfan.novel.service.UdpService;
import com.fanfan.novel.service.cache.LoadFileCache;
import com.fanfan.novel.service.cache.MusicCache;
import com.fanfan.novel.service.event.ReceiveEvent;
import com.fanfan.novel.service.event.ServiceToActivityEvent;
import com.fanfan.novel.service.music.EventCallback;
import com.fanfan.novel.service.udp.SocketManager;
import com.fanfan.novel.ui.ChatTextView;
import com.fanfan.novel.utils.AppUtil;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.novel.utils.FileUtil;
import com.fanfan.novel.utils.ImageLoader;
import com.fanfan.novel.utils.PreferencesUtils;
import com.fanfan.novel.utils.customtabs.IntentUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.dagger.componet.DaggerMainComponet;
import com.fanfan.robot.dagger.manager.MainManager;
import com.fanfan.robot.dagger.module.MainModule;
import com.fanfan.robot.db.DanceDBManager;
import com.fanfan.robot.model.Dance;
import com.fanfan.robot.presenter.ipersenter.ILineSoundPresenter;
import com.fanfan.robot.train.PanoramicMapActivity;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.base.Constant;
import com.fanfan.youtu.api.hfrobot.bean.UpdateProgram;
import com.fanfan.youtu.api.hfrobot.bean.UploadProblem;
import com.fanfan.youtu.api.hfrobot.event.UpdateProgramEvent;
import com.fanfan.youtu.api.hfrobot.event.UploadProblemEvent;
import com.fanfan.youtu.utils.GsonUtil;
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
import java.util.Random;
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

    @Inject
    MainManager mMainManager;

    private VoiceDBManager mVoiceDBManager;

    private ServiceConnection mPlayServiceConnection;

    private MaterialDialog materialDialog;

    private boolean isPlay;

    private Youtucode youtucode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main1;
    }

    @Override
    protected void initView() {
        super.initView();

        DaggerMainComponet.builder().mainModule(new MainModule(this)).build().inject(this);

        mMainManager.onCreate();

        youtucode = Youtucode.getSingleInstance();

        youtucode.updateProgram(1);
    }

    @Override
    protected void initData() {
        mVoiceDBManager = new VoiceDBManager();

        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);
    }

    @Override
    protected void callStop() {
        mMainManager.callStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
        mMainManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setChatView(false);
        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);
        mMainManager.onPause();
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
        super.onDestroy();
        mMainManager.onDestroy();
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
                FaceRecognitionActivity.newInstance(this);
                break;
            case R.id.iv_seting_up:
                SettingActivity.newInstance(this, SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE);
                break;
            case R.id.iv_public:
                PublicNumberActivity.newInstance(this);
                break;
            case R.id.iv_navigation:
                NavigationActivity.newInstance(this);
                break;
        }
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
                mMainManager.onResume();
            }
        }
    }

    //**********************************************************************************************

    private boolean isSuspendAction;
    private boolean isAutoAction;

    private void sendOrder(int type, String motion) {
        mMainManager.receiveMotion(type, motion);
    }

    private void sendCustom(RobotBean localVoice) {
        mMainManager.sendCustomMessage(localVoice);
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
            mHandler.postDelayed(runnable, 600);
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
                mHandler.postDelayed(runnable, 600);
                Print.e("自由运动(开)");
            }
        }
    };

    //**********************************************************************************************

    private void addSpeakAnswer(String messageContent, boolean isAction) {
        mMainManager.doAnswer(messageContent);
        if (isAction) {
            speakingAddAction(messageContent.length());
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
            mMainManager.onDataReceiverd(serialBean);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(UpdateProgramEvent event) {
        if (event.isOk()) {
            UpdateProgram updateProgram = event.getBean();
            Print.e(updateProgram);
            if (updateProgram.getCode() == 0) {
                UpdateProgram.UpdateProgramBean appVerBean = updateProgram.getUpdateProgram();
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
                onError(updateProgram.getCode(), updateProgram.getMsg());
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
    public void onSpeakBegin() {
        setChatView(true);
        loadImage(R.mipmap.fanfan_lift_hand, R.mipmap.fanfan_hand);
    }

    @Override
    public void onRunable() {
        setChatView(false);
        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);
        sendOrder(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        mMainManager.startVoice();
    }

    private void loadImage(int load, int place) {
        ImageLoader.loadImage(this, ivFanfan, load, false, place, 1000);
    }


    private void setChatView(final boolean isShow) {
        AlphaAnimation alphaAnimation;
        if (isShow) {
            alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(300);
        } else {
            alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(1000);
        }
        alphaAnimation.setFillAfter(true);
        chatContent.bringToFront();
        chatContent.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShow) {
                    if (chatContent.getText().equals("")) {
                        chatContent.setVisibility(View.GONE);
                    } else {
                        chatContent.setVisibility(View.VISIBLE);
                    }
                } else {
                    chatContent.setText("");
                    chatContent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //**********************************************************************************************
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showToast("发送消息成功");
    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        showToast("发送消息失败");
    }

    @Override
    public void parseMsgcomplete(String str) {
        addSpeakAnswer(str, true);
        setChatContent(str);
    }

    @Override
    public void parseCustomMsgcomplete(String customMsg) {
        RobotBean bean = GsonUtil.GsonToBean(customMsg, RobotBean.class);
        if (bean == null || bean.getType().equals("") || bean.getOrder().equals("")) {
            return;
        }
        RobotType robotType = bean.getType();
        switch (robotType) {
            case AutoAction:
                break;
            case VoiceSwitch:
                boolean isSpeech = bean.getOrder().equals("语音开");
                mMainManager.setSpeech(isSpeech);
                break;
            case Text:
                mMainManager.stopVoice();
                addSpeakAnswer(bean.getOrder(), true);
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
                mMainManager.stopVoice();
                aiuiForLocal(bean.getOrder());
                break;
        }
    }

    @Override
    public void parseServerMsgcomplete(String txt) {
        addSpeakAnswer(txt, true);
        setChatContent(txt);
    }

    //**********************************************************************************************
    @Override
    public void stopAll() {
        sendOrder(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        mMainManager.stopVoice();
        String wakeUp = resFoFinal(R.array.wake_up);
        setChatContent(wakeUp);
        mMainManager.stopAll(wakeUp);
    }

    @Override
    public void onMoveStop() {

    }

    //**********************************************************************************************
    @Override
    public void aiuiForLocal(String result) {
        Print.e("aiuiForLocal : " + result);
        String unicode = result.replaceAll("\\p{P}", "");
        Print.e("aiuiForLocal : " + unicode);
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
            mMainManager.onlineResult(unicode);
        }
    }

    @Override
    public void doAiuiAnwer(String anwer) {
        addSpeakAnswer(anwer, true);
    }

    @Override
    public void refHomePage(VoiceBean voiceBean) {
        if (voiceBean.getActionData() != null)
            sendOrder(SerialService.DEV_BAUDRATE, voiceBean.getActionData());
        if (voiceBean.getExpressionData() != null)
            sendOrder(SerialService.DEV_BAUDRATE, voiceBean.getExpressionData());

        setChatContent(voiceBean.getVoiceAnswer());
        addSpeakAnswer(voiceBean.getVoiceAnswer(), true);
    }


    @Override
    public void refHomePage(String question, String finalText) {
        setChatContent(finalText);
    }

    @Override
    public void refHomePage(String question, String finalText, String url) {
        setChatContent(finalText);
    }

    @Override
    public void refHomePage(String question, News news) {
        setChatContent(news.getContent());
    }

    @Override
    public void refHomePage(String question, Radio radio) {
        setChatContent(radio.getDescription());
    }

    @Override
    public void refHomePage(String question, Poetry poetry) {
        setChatContent(poetry.getContent());
    }

    @Override
    public void refHomePage(String question, Cookbook cookbook) {
        setChatContent(cookbook.getSteps());
    }

    @Override
    public void refHomePage(String question, EnglishEveryday englishEveryday) {
        setChatContent(englishEveryday.getContent());
    }

    @Override
    public void special(String result, SpecialType type) {
        switch (type) {
            case Story:
                break;
            case Music:
                bindService(true);
                break;
            case Joke:
                break;
            case Dance:
                DanceDBManager danceDBManager = new DanceDBManager();
                List<Dance> dances = danceDBManager.loadAll();
                if (dances != null && dances.size() > 0) {
                    Dance dance = dances.get(new Random().nextInt(dances.size()));
                    DanceActivity.newInstance(this, dance.getId());
                } else {
                    setChatContent("本地暂未添加舞蹈，请到设置或多媒体中添加舞蹈");
                    addSpeakAnswer("本地暂未添加舞蹈，请到设置或多媒体中添加舞蹈", true);
                }
                break;
            case Hand:
                sendOrder(SerialService.DEV_BAUDRATE, "A50C800CAA");
                addSpeakAnswer("你好", false);
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
                SettingActivity.newInstance(this, SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE);
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
        mMainManager.onCompleted();
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
        mMainManager.onCompleted();
    }

    @Override
    public void noAnswer(String question) {
        Print.e("noAnswer : " + question);
        String identifier = UserInfo.getInstance().getIdentifier();
        youtucode.uploadProblem(identifier, question);
        mMainManager.sendMessage(identifier, question);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(UploadProblemEvent event) {
        if (!event.isOk()) {
            onError(event);
            onCompleted();
            return;
        }
        UploadProblem uploadProblem = event.getBean();
        if (uploadProblem.getCode() == 0) {
            Print.e(uploadProblem.getMsg());
            onCompleted();
        } else if (uploadProblem.getCode() == 2) {
            UploadProblem.UploadProblemBean uploadProblemBean = uploadProblem.getUploadProblem();
            if (uploadProblemBean == null) {
                onCompleted();
                return;
            }
            Print.e(uploadProblemBean);
            String anwer = uploadProblemBean.getAnswer();
            if (anwer == null) {
                onCompleted();
            } else {
                setChatContent(anwer);
                addSpeakAnswer(anwer, true);
            }
        } else {
            onError(uploadProblem.getCode(), uploadProblem.getMsg());
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
