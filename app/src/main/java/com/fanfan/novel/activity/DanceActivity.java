package com.fanfan.novel.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ImageView;

import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.presenter.SerialPresenter;
import com.fanfan.novel.presenter.ipresenter.ISerialPresenter;
import com.fanfan.novel.service.SerialService;
import com.fanfan.novel.utils.music.DanceUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.activity.FaceRegisterActivity;
import com.fanfan.robot.db.DanceDBManager;
import com.fanfan.robot.model.Dance;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/11.
 */

public class DanceActivity extends BarBaseActivity implements ISerialPresenter.ISerialView {

    public static final String STOP_DANCE = "A5038005AA";

    @BindView(R.id.iv_splash_back)
    ImageView ivSplashBack;

    public static final String DANCE_ID = "DANCE_ID";

    public static void newInstance(Activity context, long danceId) {
        Intent intent = new Intent(context, DanceActivity.class);
        intent.putExtra(DANCE_ID, danceId);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private Dance dance;

    private NoneReceiver noneReceiver = new NoneReceiver();

    public static final String ACTION_NONE_CLOSE = "action_none_close";

    private SerialPresenter mSerialPresenter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_none;
    }

    @Override
    protected void initData() {
        mSerialPresenter = new SerialPresenter(this);

        long danceId = getIntent().getLongExtra(DANCE_ID, -1);
        if (danceId != -1) {
            DanceDBManager danceDBManager = new DanceDBManager();
            dance = danceDBManager.selectByPrimaryKey(danceId);
            DanceUtils.getInstance().startDance(this, dance.getPath());
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, dance.getOrderData());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NONE_CLOSE);
        registerReceiver(noneReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(noneReceiver);
    }

    @OnClick(R.id.iv_splash_back)
    public void onViewClicked() {
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, STOP_DANCE);
        DanceUtils.getInstance().stopPlay();
        finish();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

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

    @Override
    public void stopAll() {

    }

    @Override
    public void onMoveStop() {

    }

    public class NoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_NONE_CLOSE)) {
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, STOP_DANCE);
                DanceUtils.getInstance().stopPlay();
                finish();
            }
        }
    }
}
