package com.fanfan.novel.activity;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.robot.R;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.base.BaseActivity;
import com.fanfan.novel.ui.glowpadview.GlowPadView;
import com.seabreeze.log.Print;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.callsdk.ILVBCallMemberListener;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallOption;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhangyuanyuan on 2017/9/27.
 */
public class SimpleCallActivity extends BarBaseActivity implements ILVCallListener,
        ILVBCallMemberListener,
        ILiveLoginManager.TILVBStatusListener {

    public static final String CALL_ID = "call_id";
    public static final String CALL_TYPE = "call_type";
    public static final String SENDER = "sender";

    public static void newInstance(Context context, int callId, int callType, String sender) {
        Intent intent = new Intent();
        intent.setClass(context, SimpleCallActivity.class);
        intent.putExtra(CALL_ID, callId);
        intent.putExtra(CALL_TYPE, callType);
        intent.putExtra(SENDER, sender);
        context.startActivity(intent);
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
        setPageHide(false);

        mSender = getIntent().getStringExtra(SENDER);
        mCallId = getIntent().getIntExtra(CALL_ID, 0);
        mCallType = getIntent().getIntExtra(CALL_TYPE, ILVCallConstants.CALL_TYPE_VIDEO);

        tvSender.setText(mSender + " 来电");

        ILVCallManager.getInstance().addCallListener(this);

        ilvCallOption = new ILVCallOption(mSender)
                .callTips("呼叫标题")
                .setMemberListener(this)
                .setCallType(mCallType);

        //ILVCallManager.getInstance().acceptCall(mCallId, option);

        ILiveLoginManager.getInstance().setUserStatusListener(this);

        ILVCallManager.getInstance().initAvView(avRootView);
    }

    @Override
    protected void initData() {
        mHandler.postDelayed(runnable, 500);
    }

    @Override
    protected void setListener() {
        glowPadView.setOnTriggerListener(new GlowPadView.OnTriggerListener() {
            @Override
            public void onGrabbed(View v, int handle) {

            }

            @Override
            public void onReleased(View v, int handle) {
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable, 500);
            }

            @Override
            public void onTrigger(View v, int target) {
                final int resId = glowPadView.getResourceIdForTarget(target);
                switch (resId) {
                    case R.drawable.ic_lockscreen_answer:
                        mHandler.removeCallbacks(runnable);
                        Print.e("ic_lockscreen_answer");
                        ILVCallManager.getInstance().acceptCall(mCallId, ilvCallOption);
                        break;

                    case R.drawable.ic_lockscreen_decline:
                        mHandler.removeCallbacks(runnable);
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

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            glowPadView.ping();
            mHandler.postDelayed(this, 500);
        }
    };

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
}
