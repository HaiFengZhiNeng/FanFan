package com.fanfan.robot.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.Process;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.novel.activity.DanceActivity;
import com.fanfan.novel.activity.SimpleCallActivity;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.enums.RobotType;
import com.fanfan.novel.common.enums.SpecialType;
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
import com.fanfan.novel.model.xf.service.train.Train;
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
import com.fanfan.novel.utils.PreferencesUtils;
import com.fanfan.novel.utils.customtabs.IntentUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.TrainAdapter;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.dagger.componet.DaggerMainComponet;
import com.fanfan.robot.dagger.manager.MainManager;
import com.fanfan.robot.dagger.module.MainModule;
import com.fanfan.robot.db.DanceDBManager;
import com.fanfan.robot.model.Dance;
import com.fanfan.robot.presenter.ipersenter.ILineSoundPresenter;
import com.fanfan.robot.train.PanoramicMapActivity;
import com.fanfan.robot.train.TrainInquiryActivity;
import com.fanfan.youtu.utils.GsonUtil;
import com.github.florent37.viewanimator.ViewAnimator;
import com.iflytek.cloud.SpeechConstant;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.TIMMessage;
import com.tencent.callsdk.ILVCallConstants;

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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MainNewActivity extends BarBaseActivity implements ISynthesizerPresenter.ITtsView, IChatPresenter.IChatView,
        ISerialPresenter.ISerialView, ILineSoundPresenter.ILineSoundView {


    @BindView(R.id.iv_lccx)
    ImageView ivLccx;
    @BindView(R.id.iv_vrdt)
    ImageView ivVrdt;
    @BindView(R.id.iv_qzdj)
    ImageView ivQzdj;
    @BindView(R.id.iv_ztfw)
    ImageView ivZtfw;
    @BindView(R.id.iv_zndh)
    ImageView ivZndh;
    @BindView(R.id.iv_jtcx)
    ImageView ivJtcx;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R.id.chat_content)
    ChatTextView chatContent;
    @BindView(R.id.show_laout)
    RelativeLayout showLaout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.train_layout)
    RelativeLayout trainLayout;
    @BindView(R.id.back)
    ImageView back;

    private boolean isShowing;

    private boolean quit;

    @Inject
    MainManager mMainManager;
    
    private VoiceDBManager mVoiceDBManager;

    private ServiceConnection mPlayServiceConnection;

    private MaterialDialog materialDialog;

    private boolean isPlay;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_main;
    }

    @Override
    protected void initView() {
        super.initView();

        DaggerMainComponet.builder().mainModule(new MainModule(this)).build().inject(this);

        mMainManager.onCreate();

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
        if (isShowing) {
            trainShow(false);
        } else {
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
                Process.killProcess(Process.myPid());
            }
        }
    }


    @OnClick({R.id.iv_lccx, R.id.iv_vrdt, R.id.iv_qzdj, R.id.iv_ztfw, R.id.iv_zndh, R.id.iv_jtcx, R.id.iv_setting,
            R.id.iv_qrcode, R.id.chat_content, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_lccx:
                TrainInquiryActivity.newInstance(this);
                break;
            case R.id.iv_vrdt:
                PanoramicMapActivity.newInstance(this);
                break;
            case R.id.iv_qzdj:
                ArrayList<String> nums = new ArrayList<>();
                nums.add(RobotInfo.getInstance().getControlId());
                SimpleCallActivity.newInstance(this, ILVCallConstants.CALL_TYPE_VIDEO, nums);
                break;
            case R.id.iv_ztfw:
                ProblemConsultingActivity.newInstance(this);
                break;
            case R.id.iv_zndh:
                NavigationActivity.newInstance(this);
                break;
            case R.id.iv_jtcx:
                Intent intent = new Intent("android.intent.action.VIEW",
                        Uri.parse("androidamap://showTraffic?sourceApplication=softname&amp;poiid=BGVIS1&amp;lat=36.2&amp;lon=116.1&amp;level=10&amp;dev=0"));
                intent.setPackage("com.autonavi.minimap");
                startActivity(intent);
                break;
            case R.id.iv_setting:
                SettingActivity.newInstance(this, SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE);
                break;
            case R.id.iv_qrcode:
                PublicNumberActivity.newInstance(this);
                break;
            case R.id.chat_content:
                break;
            case R.id.back:
                if(isShowing) {
                    trainShow(false);
                }
                break;
        }
    }

    private void bindService(boolean isPlay) {
        this.isPlay = isPlay;
        if (!PreferencesUtils.getBoolean(MainNewActivity.this, Constants.MUSIC_UPDATE, false))
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

    private void sendOrder(int type, String motion){
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
            case KeyEvent.KEYCODE_BACK:
                if (isShowing) {
                    trainShow(false);
                }
        }
        return false;
    }

    private void trainShow(boolean isShow) {
        isShowing = isShow;
        trainLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
        showLaout.setVisibility(isShow ? View.GONE : View.VISIBLE);
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
//            speakingAddAction(messageContent.length());
        }
    }

    private void setChatContent(String messageContent) {
        chatContent.setSpanText(mHandler, messageContent, true);
    }

    private void speakingAddAction(int length) {
//        if (length <= 13) {
//            sendOrder(SerialService.DEV_BAUDRATE, "A50C8001AA");
//        } else if (length > 13 && length <= 40) {
//            sendOrder(SerialService.DEV_BAUDRATE, "A50C8003AA");
//        } else {
//            sendOrder(SerialService.DEV_BAUDRATE, "A50C8021AA");
//        }
        sendOrder(SerialService.DEV_BAUDRATE, Constants.SPEAK_ACTION);
    }

    //************************anim****************************
    protected void animateSequentially(View view) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                FanFanIntroduceActivity.newInstance(MainActivity.this);
                PPTActivity.newInstance(MainNewActivity.this);
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
//        ImageLoader.loadImage(this, ivFanfan, load, false, place, 1000);
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
//        String jsonResult = "[{\"arrivalTime\":\"2018-03-08 13:06\",\"endtime_for_voice\":\"明天13:06\",\"endtimestamp\":1520485560,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时6分\",\"startTime\":\"2018-03-08 08:00\",\"startTimeStamp\":\"1520467200\",\"starttime_for_voice\":\"明天08:00\",\"starttimestamp\":1520467200,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G11\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 12:39\",\"endtime_for_voice\":\"明天12:39\",\"endtimestamp\":1520483940,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时56分\",\"startTime\":\"2018-03-08 06:43\",\"startTimeStamp\":\"1520462580\",\"starttime_for_voice\":\"明天06:43\",\"starttimestamp\":1520462580,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G101\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 11:34\",\"endtime_for_voice\":\"明天11:34\",\"endtimestamp\":1520480040,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"4时34分\",\"startTime\":\"2018-03-08 07:00\",\"startTimeStamp\":\"1520463600\",\"starttime_for_voice\":\"明天07:00\",\"starttimestamp\":1520463600,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G5\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 13:11\",\"endtime_for_voice\":\"明天13:11\",\"endtimestamp\":1520485860,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时36分\",\"startTime\":\"2018-03-08 07:35\",\"startTimeStamp\":\"1520465700\",\"starttime_for_voice\":\"明天07:35\",\"starttimestamp\":1520465700,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G105\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 13:49\",\"endtime_for_voice\":\"明天13:49\",\"endtimestamp\":1520488140,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时44分\",\"startTime\":\"2018-03-08 08:05\",\"startTimeStamp\":\"1520467500\",\"starttime_for_voice\":\"明天08:05\",\"starttimestamp\":1520467500,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G107\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 14:22\",\"endtime_for_voice\":\"明天14:22\",\"endtimestamp\":1520490120,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时47分\",\"startTime\":\"2018-03-08 08:35\",\"startTimeStamp\":\"1520469300\",\"starttime_for_voice\":\"明天08:35\",\"starttimestamp\":1520469300,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G111\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 14:33\",\"endtime_for_voice\":\"明天14:33\",\"endtimestamp\":1520490780,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时40分\",\"startTime\":\"2018-03-08 08:53\",\"startTimeStamp\":\"1520470380\",\"starttime_for_voice\":\"明天08:53\",\"starttimestamp\":1520470380,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G113\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 13:28\",\"endtime_for_voice\":\"明天13:28\",\"endtimestamp\":1520486880,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"4时28分\",\"startTime\":\"2018-03-08 09:00\",\"startTimeStamp\":\"1520470800\",\"starttime_for_voice\":\"明天09:00\",\"starttimestamp\":1520470800,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G1\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 14:49\",\"endtime_for_voice\":\"明天14:49\",\"endtimestamp\":1520491740,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时34分\",\"startTime\":\"2018-03-08 09:15\",\"startTimeStamp\":\"1520471700\",\"starttime_for_voice\":\"明天09:15\",\"starttimestamp\":1520471700,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G41\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 15:10\",\"endtime_for_voice\":\"明天15:10\",\"endtimestamp\":1520493000,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时50分\",\"startTime\":\"2018-03-08 09:20\",\"startTimeStamp\":\"1520472000\",\"starttime_for_voice\":\"明天09:20\",\"starttimestamp\":1520472000,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G115\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 15:37\",\"endtime_for_voice\":\"明天15:37\",\"endtimestamp\":1520494620,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"6时12分\",\"startTime\":\"2018-03-08 09:25\",\"startTimeStamp\":\"1520472300\",\"starttime_for_voice\":\"明天09:25\",\"starttimestamp\":1520472300,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G117\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 14:28\",\"endtime_for_voice\":\"明天14:28\",\"endtimestamp\":1520490480,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"4时28分\",\"startTime\":\"2018-03-08 10:00\",\"startTimeStamp\":\"1520474400\",\"starttime_for_voice\":\"明天10:00\",\"starttimestamp\":1520474400,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G13\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 15:43\",\"endtime_for_voice\":\"明天15:43\",\"endtimestamp\":1520494980,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时38分\",\"startTime\":\"2018-03-08 10:05\",\"startTimeStamp\":\"1520474700\",\"starttime_for_voice\":\"明天10:05\",\"starttimestamp\":1520474700,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G119\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 16:32\",\"endtime_for_voice\":\"明天16:32\",\"endtimestamp\":1520497920,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"6时4分\",\"startTime\":\"2018-03-08 10:28\",\"startTimeStamp\":\"1520476080\",\"starttime_for_voice\":\"明天10:28\",\"starttimestamp\":1520476080,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G121\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 15:56\",\"endtime_for_voice\":\"明天15:56\",\"endtimestamp\":1520495760,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"4时56分\",\"startTime\":\"2018-03-08 11:00\",\"startTimeStamp\":\"1520478000\",\"starttime_for_voice\":\"明天11:00\",\"starttimestamp\":1520478000,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G15\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 16:56\",\"endtime_for_voice\":\"明天16:56\",\"endtimestamp\":1520499360,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时46分\",\"startTime\":\"2018-03-08 11:10\",\"startTimeStamp\":\"1520478600\",\"starttime_for_voice\":\"明天11:10\",\"starttimestamp\":1520478600,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G125\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 17:08\",\"endtime_for_voice\":\"明天17:08\",\"endtimestamp\":1520500080,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时48分\",\"startTime\":\"2018-03-08 11:20\",\"startTimeStamp\":\"1520479200\",\"starttime_for_voice\":\"明天11:20\",\"starttimestamp\":1520479200,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G411\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-09 07:19\",\"endtime_for_voice\":\"03月09号07:19\",\"endtimestamp\":1520551140,\"originStation\":\"北京\",\"price\":[{\"name\":\"硬座\",\"value\":\"156.5\"},{\"name\":\"硬卧\",\"value\":\"304.5\"},{\"name\":\"软卧\",\"value\":\"476.5\"},{\"name\":\"无座\",\"value\":\"156.5\"}],\"runTime\":\"19时25分\",\"startTime\":\"2018-03-08 11:54\",\"startTimeStamp\":\"1520481240\",\"starttime_for_voice\":\"明天11:54\",\"starttimestamp\":1520481240,\"terminalStation\":\"上海\",\"trainNo\":\"1461\",\"trainType\":\"普速\"},{\"arrivalTime\":\"2018-03-08 17:56\",\"endtime_for_voice\":\"明天17:56\",\"endtimestamp\":1520502960,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时46分\",\"startTime\":\"2018-03-08 12:10\",\"startTimeStamp\":\"1520482200\",\"starttime_for_voice\":\"明天12:10\",\"starttimestamp\":1520482200,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G129\",\"trainType\":\"高铁\"},{\"arrivalTime\":\"2018-03-08 18:06\",\"endtime_for_voice\":\"明天18:06\",\"endtimestamp\":1520503560,\"originStation\":\"北京南\",\"price\":[{\"name\":\"二等座\",\"value\":\"553\"},{\"name\":\"一等座\",\"value\":\"933\"},{\"name\":\"商务座\",\"value\":\"1748\"}],\"runTime\":\"5时46分\",\"startTime\":\"2018-03-08 12:20\",\"startTimeStamp\":\"1520482800\",\"starttime_for_voice\":\"明天12:20\",\"starttimestamp\":1520482800,\"terminalStation\":\"上海虹桥\",\"trainNo\":\"G131\",\"trainType\":\"高铁\"}]";
//        trains = GsonUtil.GsonToArrayList(jsonResult.toString(), Train.class);
        Print.e(trains);
        trainShow(true);
        TrainAdapter trainAdapter = new TrainAdapter(trains);
        trainAdapter.isFirstOnly(false); //设置不仅是首次填充数据时有动画,以后上下滑动也会有动画
        trainAdapter.openLoadAnimation();
        recyclerView.setAdapter(trainAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void startPage(SpecialType specialType) {
        switch (specialType) {
            case TrainInquiry:
                TrainInquiryActivity.newInstance(this);
                break;
            case PanoramicMap:
                PanoramicMapActivity.newInstance(this);
                break;
            case TalkBack:
                ArrayList<String> nums = new ArrayList<>();
                nums.add(RobotInfo.getInstance().getControlId());
                SimpleCallActivity.newInstance(this, ILVCallConstants.CALL_TYPE_VIDEO, nums);
                break;
            case StationService:
                ProblemConsultingActivity.newInstance(this);
                break;
            case InternalNavigation:
                NavigationActivity.newInstance(this);
                break;
            case TrafficTravel:
                Intent intent = new Intent("android.intent.action.VIEW",
                        Uri.parse("androidamap://showTraffic?sourceApplication=softname&amp;poiid=BGVIS1&amp;lat=36.2&amp;lon=116.1&amp;level=10&amp;dev=0"));
                intent.setPackage("com.autonavi.minimap");
                startActivity(intent);
                break;
            case Seting_up:
                SettingActivity.newInstance(this, SettingActivity.LOGOUT_TO_MAIN_REQUESTCODE);
                break;
            case Public_num:
                PublicNumberActivity.newInstance(this);
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
        showMsg("Vr");
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
                    MultimediaActivity.newInstance(MainNewActivity.this, isPlay, MultimediaActivity.MULTIMEDIA_REQUESTCODE);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

}
