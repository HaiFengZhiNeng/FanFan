package com.fanfan.robot.activity;

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
import com.fanfan.novel.db.manager.NavigationDBManager;
import com.fanfan.novel.db.manager.VideoDBManager;
import com.fanfan.novel.db.manager.VoiceDBManager;
import com.fanfan.novel.im.init.LoginBusiness;
import com.fanfan.novel.map.activity.AMapActivity;
import com.fanfan.novel.model.RobotBean;
import com.fanfan.novel.model.SerialBean;
import com.fanfan.novel.model.VoiceBean;
import com.fanfan.novel.model.xf.service.Cookbook;
import com.fanfan.novel.model.xf.service.News;
import com.fanfan.novel.model.xf.service.Poetry;
import com.fanfan.novel.model.xf.service.englishEveryday.EnglishEveryday;
import com.fanfan.novel.model.xf.service.radio.Radio;
import com.fanfan.novel.presenter.ChatPresenter;
import com.fanfan.novel.presenter.SerialPresenter;
import com.fanfan.novel.presenter.SynthesizerPresenter;
import com.fanfan.novel.presenter.ipresenter.IChatPresenter;
import com.fanfan.novel.presenter.ipresenter.ISerialPresenter;
import com.fanfan.novel.presenter.ipresenter.ISynthesizerPresenter;
import com.fanfan.novel.service.PlayService;
import com.fanfan.novel.service.SerialService;
import com.fanfan.novel.service.UdpService;
import com.fanfan.novel.service.cache.MusicCache;
import com.fanfan.novel.service.event.ReceiveEvent;
import com.fanfan.novel.service.event.ServiceToActivityEvent;
import com.fanfan.novel.service.music.EventCallback;
import com.fanfan.novel.service.udp.SocketManager;
import com.fanfan.novel.ui.ChatTextView;
import com.fanfan.novel.utils.ImageLoader;
import com.fanfan.novel.utils.PreferencesUtils;
import com.fanfan.novel.utils.customtabs.IntentUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.db.DanceDBManager;
import com.fanfan.robot.model.Dance;
import com.fanfan.robot.presenter.LineSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ILineSoundPresenter;
import com.fanfan.youtu.utils.GsonUtil;
import com.github.florent37.viewanimator.ViewAnimator;
import com.iflytek.cloud.SpeechConstant;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

    private ChatPresenter mChatPresenter;
    private SerialPresenter mSerialPresenter;
    private SynthesizerPresenter mTtsPresenter;
    private LineSoundPresenter mSoundPresenter;
    private VoiceDBManager mVoiceDBManager;
    private VideoDBManager mVideoDBManager;
    private NavigationDBManager mNavigationDBManager;

    private ServiceConnection mPlayServiceConnection;

    private MaterialDialog materialDialog;

    private boolean isPlay;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main1;
    }

    @Override
    protected void initView() {
        super.initView();

        mChatPresenter = new ChatPresenter(this, TIMConversationType.C2C, RobotInfo.getInstance().getControlId());
        mChatPresenter.start();

        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();

        mTtsPresenter = new SynthesizerPresenter(this);
        mTtsPresenter.start();

        mSoundPresenter = new LineSoundPresenter(this);
        mSoundPresenter.start();

    }

    @Override
    protected void initData() {
        mVoiceDBManager = new VoiceDBManager();
        mVideoDBManager = new VideoDBManager();
        mNavigationDBManager = new NavigationDBManager();

        loadImage(R.mipmap.fanfan_hand, R.mipmap.fanfan_lift_hand);
    }

    @Override
    protected void callStop() {
        mTtsPresenter.stopTts();
        mTtsPresenter.stopHandler();
        mSoundPresenter.stopRecognizerListener();
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
        RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
        mTtsPresenter.buildTts();
        mSoundPresenter.buildIat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        mTtsPresenter.stopTts();
        mTtsPresenter.stopHandler();
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopVoice();
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
        mTtsPresenter.finish();
        mChatPresenter.finish();
        mSoundPresenter.finish();
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
                mTtsPresenter.buildTts();
                mSoundPresenter.buildIat();
            }
        }
    }

    //**********************************************************************************************

    private boolean isSuspendAction;
    private boolean isAutoAction;

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
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C80F3AA");
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C80F2AA");
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
                        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038002AA");
                        Print.e("up");
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN://20
                        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038008AA");
                        Print.e("down");
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT://21
                        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038004AA");
                        Print.e("left");
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT://20
                        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038006AA");
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
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A503800AAA");
            Print.e("自由运动(开)");
            mHandler.postDelayed(runnable, 600);
        }
    }

    public void stopAutoAction() {
        if (isAutoAction) {
            Print.e("自由运动(关)");
            mHandler.removeCallbacks(runnable);
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038005AA");
            isAutoAction = false;
        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoAction) {
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A503800AAA");
                mHandler.postDelayed(runnable, 600);
                Print.e("自由运动(开)");
            }
        }
    };

    //**********************************************************************************************

    private void addSpeakAnswer(String messageContent, boolean isAction) {
        mTtsPresenter.doAnswer(messageContent);
        if (isAction) {
            speakingAddAction(messageContent.length());
        }
    }

    private void setChatContent(String messageContent) {
        chatContent.setSpanText(mHandler, messageContent, true);
    }

    private void speakingAddAction(int length) {
//        if (length <= 13) {
//            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8001AA");
//        } else if (length > 13 && length <= 40) {
//            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8003AA");
//        } else {
//            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8021AA");
//        }
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.SPEAK_ACTION);
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
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, recvStr);
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
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        mSoundPresenter.startRecognizerListener();
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
                mSoundPresenter.setSpeech(isSpeech);
                break;
            case Text:
                mSoundPresenter.stopRecognizerListener();
                mSoundPresenter.stopVoice();
                addSpeakAnswer(bean.getOrder(), true);
                break;
            case SmartChat:

                break;
            case Motion:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, bean.getOrder());
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
                        mChatPresenter.sendCustomMessage(robotBean);
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
                        mChatPresenter.sendCustomMessage(robotBean);
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
                    mChatPresenter.sendCustomMessage(localVoice);
                }
                break;
            case Anwser:
                mSoundPresenter.stopRecognizerListener();
                mSoundPresenter.stopVoice();
                aiuiForLocal(bean.getOrder());
                break;
        }
    }

    //**********************************************************************************************
    @Override
    public void stopAll() {
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopVoice();
        String wakeUp = resFoFinal(R.array.wake_up);
        setChatContent(wakeUp);
        mTtsPresenter.stopAll(wakeUp);
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
            mSoundPresenter.onlineResult(unicode);
        }
    }

    @Override
    public void doAiuiAnwer(String anwer) {
        addSpeakAnswer(anwer, true);
    }

    @Override
    public void refHomePage(VoiceBean voiceBean) {
        if (voiceBean.getActionData() != null)
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, voiceBean.getActionData());
        if (voiceBean.getExpressionData() != null)
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, voiceBean.getExpressionData());

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
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C800CAA");
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
        }
    }

    @Override
    public void spakeMove(SpecialType type, String result) {
        mTtsPresenter.onCompleted();
        switch (type) {
            case Forward:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038002AA");
                break;
            case Backoff:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038008AA");
                break;
            case Turnleft:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038004AA");
                break;
            case Turnright:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038006AA");
                break;
        }
    }

    @Override
    public void openMap() {
        AMapActivity.newInstance(this);
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
        mTtsPresenter.onCompleted();
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
