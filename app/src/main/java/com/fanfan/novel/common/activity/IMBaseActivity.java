package com.fanfan.novel.common.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.robot.R;
import com.fanfan.novel.activity.SimpleCallActivity;
import com.fanfan.novel.activity.SplashActivity;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.base.BaseActivity;
import com.fanfan.novel.im.event.MessageEvent;
import com.fanfan.novel.im.init.TlsBusiness;
import com.fanfan.novel.model.UserInfo;
import com.fanfan.novel.service.cache.UserInfoCache;
import com.fanfan.novel.utils.DialogUtils;
import com.seabreeze.log.Print;
import com.tencent.callsdk.ILVCallConfig;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallNotification;
import com.tencent.callsdk.ILVCallNotificationListener;
import com.tencent.callsdk.ILVIncomingListener;
import com.tencent.callsdk.ILVIncomingNotification;

import java.io.IOException;

/**
 * Created by android on 2017/12/26.
 */

public abstract class IMBaseActivity extends BaseActivity implements ILVCallNotificationListener,
        ILVIncomingListener, ILVCallListener {

    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier();
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(Constants.EXIT_APP));

        ILVCallManager.getInstance().init(new ILVCallConfig()
                .setNotificationListener(this)
                .setAutoBusy(true));
        ILVCallManager.getInstance().addIncomingListener(this);//添加来电回调
        ILVCallManager.getInstance().addCallListener(this);//发起视频聊天
    }

    @Override
    protected void onDestroy() {
        ILVCallManager.getInstance().removeIncomingListener(this);
        ILVCallManager.getInstance().removeCallListener(this);
        super.onDestroy();
        mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);
    }

    @Override
    public void onRecvNotification(int callid, ILVCallNotification ilv) {
        Print.e("onRecvNotification : " + ilv);
        if (!Constants.isCalling) {
            if (ilv.getNotifDesc().equals("")) {
                Print.e("有来电 ： " + ilv.getSender());

                if(ilv.getTargets().size() > 0){
                    Print.e("电话挂断 ： " + ilv.getTargets());
                }

                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), getNotification());
//                r.play();
            }

        }
    }

    private void startAlarm() {
        MediaPlayer mMediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    private Uri getNotification() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
    }

    @Override
    public void onNewIncomingCall(final int callId, final int callType, final ILVIncomingNotification notification) {
        Print.e("onNewIncomingCall : " + notification);
        callStop();
        SimpleCallActivity.newInstance(this, callId, callType, notification.getSender());
    }

    protected void callStop() {

    }

    @Override
    public void onCallEstablish(int callId) {
        Print.e("Call Establish");
    }

    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        Print.e("End Call");
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {
        Print.e("Exception id");
    }

    public class ExitBroadcastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.EXIT_APP)) {
                //在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
                onReceiveExitMsg();
            }
        }
    }

    private void onReceiveExitMsg() {
        DialogUtils.showBasicIconDialog(IMBaseActivity.this, R.mipmap.ic_logo, "登陆提示",
                "您的帐号已在其它地方登陆", "退出", "重新登陆",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {
                        LocalBroadcastManager.getInstance(IMBaseActivity.this).sendBroadcast(new Intent(Constants.NET_LOONGGG_EXITAPP));
                    }

                    @Override
                    public void onClickRight() {
                        logout();
                    }
                });
    }


    public void logout() {
        TlsBusiness.logout(UserInfo.getInstance().getIdentifier());
        UserInfoCache.clearCache(this);
        UserInfo.getInstance().setIdentifier(null);
        MessageEvent.getInstance().clear();
//        FriendshipInfo.getInstance().clear();
//        GroupInfo.getInstance().clear();
        Intent intent = new Intent(IMBaseActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

}
