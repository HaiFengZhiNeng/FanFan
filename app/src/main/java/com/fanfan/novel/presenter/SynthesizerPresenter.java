package com.fanfan.novel.presenter;

import android.app.Activity;
import android.os.Handler;

import com.fanfan.robot.R;
import com.fanfan.novel.common.Constants;
import com.fanfan.robot.app.NovelApp;
import com.fanfan.novel.common.instance.SpeakTts;
import com.fanfan.novel.presenter.ipresenter.ISynthesizerPresenter;
import com.fanfan.novel.service.listener.TtsListener;
import com.fanfan.novel.utils.PreferencesUtils;
import com.fanfan.robot.app.RobotInfo;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.seabreeze.log.Print;

import java.util.Random;

/**
 * Created by android on 2018/1/3.
 */

public class SynthesizerPresenter extends ISynthesizerPresenter implements TtsListener.SynListener {

    private ITtsView mTtsView;

    private SpeechSynthesizer mTts;
    private TtsListener mTtsListener;

    private Handler mHandler = new Handler();

    public SynthesizerPresenter(ITtsView baseView) {
        super(baseView);
        this.mTtsView = baseView;
        mTtsListener = new TtsListener(this);
    }

    @Override
    public void start() {
        initTts();
    }

    @Override
    public void finish() {
        if (mTts != null) {
            mTts.destroy();
        }
        mTtsListener = null;
    }

    @Override
    public void initTts() {

        mTts = SpeakTts.getInstance().mTts();
        if (mTts == null) {
            SpeakTts.getInstance().initTts(NovelApp.getInstance().getApplicationContext(), new InitListener() {
                @Override
                public void onInit(int code) {
                    if (code != ErrorCode.SUCCESS) {
                        Print.e("初始化失败，错误码：" + code);
                    }
                    mTts = SpeakTts.getInstance().mTts();
                }
            });
        }
    }

    @Override
    public void buildTts() {
        if (mTts == null) {
            initTts();
        }
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

        mTts.setParameter(SpeechConstant.VOICE_NAME, RobotInfo.getInstance().getTtsLineTalker());
        mTts.setParameter(SpeechConstant.SPEED, String.valueOf(RobotInfo.getInstance().getLineSpeed()));
        mTts.setParameter(SpeechConstant.PITCH, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Constants.PROJECT_PATH + "/msc/tts.wav");
        Print.e("initTts success ...");
    }

    @Override
    public void stopTts() {
        if (mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    @Override
    public void doAnswer(String answer) {
        mTts.startSpeaking(answer, mTtsListener);
    }

    @Override
    public void stopHandler() {
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public void stopAll(String wakeUp) {
        stopTts();
        doAnswer(wakeUp);
    }

    private String resFoFinal(int id) {
        String[] arrResult = ((Activity) mTtsView).getResources().getStringArray(id);
        return arrResult[new Random().nextInt(arrResult.length)];
    }

    @Override
    public void onCompleted() {
        Print.e("结束说话");
        mHandler.postDelayed(runnable, 1000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mTtsView.onRunable();
        }
    };

    @Override
    public void onSpeakBegin() {
        Print.e("开始说话");
        mTtsView.onSpeakBegin();
    }
}
