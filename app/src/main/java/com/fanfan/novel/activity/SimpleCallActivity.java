package com.fanfan.novel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.utils.music.DanceUtils;
import com.fanfan.robot.R;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.base.BaseActivity;
import com.fanfan.novel.ui.glowpadview.GlowPadView;
import com.seabreeze.log.Print;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.callsdk.ILVBCallMemberListener;
import com.tencent.callsdk.ILVCallConfig;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallNotification;
import com.tencent.callsdk.ILVCallNotificationListener;
import com.tencent.callsdk.ILVCallOption;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhangyuanyuan on 2017/9/27.
 */
public class SimpleCallActivity extends BaseActivity implements
        ILVCallListener,
        ILVCallNotificationListener,
        ILVBCallMemberListener,
        ILiveLoginManager.TILVBStatusListener,
        ILiveCallBack {

    public static final String CALL_ID = "call_id";
    public static final String CALL_TYPE = "call_type";
    public static final String SENDER = "sender";
    public static final String CALLNUMBERS = "callnumbers";

    public static void newInstance(Activity context, int callId, int callType, String sender) {
        Intent intent = new Intent();
        intent.setClass(context, SimpleCallActivity.class);
        intent.putExtra(CALL_ID, callId);
        intent.putExtra(CALL_TYPE, callType);
        intent.putExtra(SENDER, sender);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void newInstance(Activity context, int callType, ArrayList<String> nums) {
        Intent intent = new Intent();
        intent.setClass(context, SimpleCallActivity.class);
        intent.putExtra(CALL_ID, 0);
        intent.putExtra(CALL_TYPE, callType);
        intent.putExtra(SENDER, ILiveLoginManager.getInstance().getMyUserId());
        intent.putStringArrayListExtra(CALLNUMBERS, nums);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @BindView(R.id.av_root_view)
    AVRootView avRootView;
    @BindView(R.id.btn_hang_up)
    ImageView btnHandup;
    @BindView(R.id.tv_sender)
    TextView tvSender;
    @BindView(R.id.call_back)
    ImageView callBack;
    @BindView(R.id.glow_pad_view)
    GlowPadView glowPadView;

    private String mSender;
    private int mCallId;
    private int mCallType;

    private int mCurCameraId = ILiveConstants.FRONT_CAMERA;

    private ILVCallOption ilvCallOption;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_call;
    }

    @Override
    protected void initView() {

        mSender = getIntent().getStringExtra(SENDER);
        mCallId = getIntent().getIntExtra(CALL_ID, 0);
        mCallType = getIntent().getIntExtra(CALL_TYPE, ILVCallConstants.CALL_TYPE_VIDEO);

        ILVCallManager.getInstance().init(new ILVCallConfig().setNotificationListener(this));
        ILVCallManager.getInstance().addCallListener(this);

        ilvCallOption = new ILVCallOption(mSender)
                .callTips("呼叫标题")
                .setMemberListener(this)
                .setCallType(mCallType);

        if (0 == mCallId) { // 发起呼叫
            setPageHide(true);
            tvSender.setText(mSender + " 呼叫");
            List<String> nums = getIntent().getStringArrayListExtra(CALLNUMBERS);
            if (nums.size() > 1) {
                mCallId = ILVCallManager.getInstance().makeMutiCall(nums, ilvCallOption, this);
            } else {
                mCallId = ILVCallManager.getInstance().makeCall(nums.get(0), ilvCallOption, this);
            }
        } else {
            playNewCall();
            setPageHide(false);
            tvSender.setText(mSender + " 呼叫");
        }


        ILiveLoginManager.getInstance().setUserStatusListener(this);

        ILVCallManager.getInstance().initAvView(avRootView);
        avRootView.setRemoteRotationFix(180);
    }

    private void playNewCall() {
        isPlayFirst = true;
        mHandler.post(musicRunnable);
    }

    @Override
    protected int setBackgroundGlide() {
        return 0;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        glowPadView.setOnTriggerListener(new GlowPadView.OnTriggerListener() {
            @Override
            public void onGrabbed(View v, int handle) {

            }

            @Override
            public void onReleased(View v, int handle) {
//                mHandler.removeCallbacks(runnable);
//                mHandler.postDelayed(runnable, 500);
            }

            @Override
            public void onTrigger(View v, int target) {
                DanceUtils.getInstance().stopPlay();
                final int resId = glowPadView.getResourceIdForTarget(target);
                switch (resId) {
                    case R.drawable.ic_lockscreen_answer:
                        mHandler.removeCallbacks(musicRunnable);
                        Print.e("ic_lockscreen_answer");
                        ILVCallManager.getInstance().acceptCall(mCallId, ilvCallOption);
                        break;

                    case R.drawable.ic_lockscreen_decline:
                        mHandler.removeCallbacks(musicRunnable);
                        Print.e("ic_lockscreen_decline");
                        ILVCallManager.getInstance().rejectCall(mCallId);
                        break;
                }
            }

            @Override
            public void onGrabbedStateChange(View v, int handle) {

            }

            @Override
            public void onFinishFinalAnimation() {

            }
        });
        glowPadView.setShowTargetsOnIdle(true);
    }


    @Override
    protected void onResume() {
        ILVCallManager.getInstance().onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        ILVCallManager.getInstance().onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ILVCallManager.getInstance().removeCallListener(this);
        ILVCallManager.getInstance().onDestory();
        super.onDestroy();
    }

    @Override
    protected void setResult() {

    }

    @Override
    public void onBackPressed() {
        ILVCallManager.getInstance().endCall(mCallId);
    }


    private void setPageHide(boolean b) {
        callBack.setVisibility(b ? View.GONE : View.VISIBLE);
        btnHandup.setVisibility(b ? View.VISIBLE : View.GONE);
        glowPadView.setVisibility(b ? View.GONE : View.VISIBLE);
    }


    private void initCallManager() {
        //打开摄像头
        ILVCallManager.getInstance().enableCamera(mCurCameraId, true);
        //关闭摄像头
//        ILVCallManager.getInstance().enableCamera(mCurCameraId, false);
//        avRootView.closeUserView(ILiveLoginManager.getInstance().getMyUserId(), AVView.VIDEO_SRC_TYPE_CAMERA, true);
        //切换摄像头
//        mCurCameraId = (ILiveConstants.FRONT_CAMERA==mCurCameraId) ? ILiveConstants.BACK_CAMERA : ILiveConstants.FRONT_CAMERA;
        ILVCallManager.getInstance().switchCamera(mCurCameraId);
        //打开麦克风
        ILVCallManager.getInstance().enableMic(true);
        //关闭麦克风
//        ILVCallManager.getInstance().enableMic(false);
        //切换到听筒
//        ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_HEADSET);
        //切换到扬声器
        ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_SPEAKER);
        //设置美艳
        ILiveSDK.getInstance().getAvVideoCtrl().inputBeautyParam(0.0f);
    }

    @OnClick({R.id.btn_hang_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_hang_up:
                DanceUtils.getInstance().endCall(this);
                ILVCallManager.getInstance().endCall(mCallId);
                break;
        }
    }

    //设置电话回调（初始化设置）
    @Override
    public void onCallEstablish(int callId) {
        Print.e("onCallEstablish");
        Constants.isCalling = true;
        setPageHide(true);
        initCallManager();
        avRootView.swapVideoView(0, 1);
        for (int i = 1; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++) {
            final int index = i;
            AVVideoView minorView = avRootView.getViewByIndex(i);
            if (ILiveLoginManager.getInstance().getMyUserId().equals(minorView.getIdentifier())) {
                minorView.setMirror(true);      // 本地镜像
            }
            minorView.setDragable(true);    // 小屏可拖动
            minorView.setGestureListener(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    avRootView.swapVideoView(0, index);     // 与大屏交换
                    return false;
                }
            });
        }
    }


    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        Print.e("onCallEnd  endResult : " + endResult + " , endInfo :" + endInfo);
        Constants.isCalling = false;
        finish();
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {
        Print.e("onException");
    }

    //设置成员事件回调(调用ILVCallManager中的摄像头及麦克风接口才会有事件)
    @Override
    public void onCameraEvent(String id, boolean bEnable) {
        Print.e("onCameraEvent");
    }

    @Override
    public void onMicEvent(String id, boolean bEnable) {
        Print.e("onMicEvent");
    }

    //设置用户状态回调(每次登录前都需要重新设置)
    @Override
    public void onForceOffline(int error, String message) {
        Print.e("onForceOffline  " + message);
        finish();
    }

    @Override
    public void onRecvNotification(int callid, ILVCallNotification ilv) {
        Print.e("视频来电onRecvNotification : " + ilv);
        //判断是否正在通话
        if (!Constants.isCalling) {
            //判断targets是否为空
            if (ilv.getTargets().size() > 0) {//为0，有挂断电话
                DanceUtils.getInstance().endCall(this);
            } else {
                playNewCall();
            }
        }
    }

    private boolean isPlayFirst;

    Runnable musicRunnable = new Runnable() {
        @Override
        public void run() {
            glowPadView.ping();
            DanceUtils.getInstance().newIncomingCall(SimpleCallActivity.this, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (isPlayFirst) {
                        isPlayFirst = false;
                        mHandler.post(musicRunnable);
                    }
                }
            });

        }
    };

    Runnable callRunnable = new Runnable() {
        @Override
        public void run() {
            DanceUtils.getInstance().newIncomingCall(SimpleCallActivity.this, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mHandler.post(callRunnable);
                }
            });

        }
    };

    @Override
    public void onSuccess(Object data) {
        Print.e("拨打电话" + data);
        mHandler.post(callRunnable);
    }

    @Override
    public void onError(String module, int errCode, String errMsg) {
        Print.e("拨打电话" + errMsg);
    }
}
