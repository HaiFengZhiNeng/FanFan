package com.fanfan.robot.dagger.manager;

import com.fanfan.novel.common.Constants;
import com.fanfan.novel.model.RobotBean;
import com.fanfan.novel.model.SerialBean;
import com.fanfan.novel.presenter.ChatPresenter;
import com.fanfan.novel.presenter.SerialPresenter;
import com.fanfan.novel.presenter.SynthesizerPresenter;
import com.fanfan.novel.service.SerialService;
import com.fanfan.robot.presenter.LineSoundPresenter;

import javax.inject.Inject;

/**
 * Created by Administrator on 2018/3/8/008.
 */

public class MainManager {

    ChatPresenter mChatPresenter;
    SerialPresenter mSerialPresenter;
    SynthesizerPresenter mTtsPresenter;
    LineSoundPresenter mSoundPresenter;

    @Inject
    public MainManager(ChatPresenter chatPresenter, SerialPresenter serialPresenter,
                       SynthesizerPresenter synthesizerPresenter,
                       LineSoundPresenter lineSoundPresenter) {
        mChatPresenter = chatPresenter;
        mSerialPresenter = serialPresenter;
        mTtsPresenter = synthesizerPresenter;
        mSoundPresenter = lineSoundPresenter;
    }

    public void onCreate() {
        mChatPresenter.start();
        mSerialPresenter.start();
        mTtsPresenter.start();
        mSoundPresenter.start();
    }

    public void onResume() {
        mSoundPresenter.setOpening(true);
        mTtsPresenter.buildTts();
        mSoundPresenter.buildIat();
    }

    public void onPause() {
        mSoundPresenter.setOpening(false);
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
        mTtsPresenter.stopTts();
        mTtsPresenter.stopHandler();
        stopVoice();
    }

    public void onDestroy() {
        mTtsPresenter.finish();
        mChatPresenter.finish();
        mSoundPresenter.finish();
    }

    public void callStop() {
        mTtsPresenter.stopTts();
        mTtsPresenter.stopHandler();
        stopVoice();
    }

    public void startVoice(){
        mSoundPresenter.startRecognizerListener();
    }

    public void stopVoice(){
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopVoice();
    }

    public void receiveMotion(int type, String motion) {
        mSerialPresenter.receiveMotion(type, motion);
    }

    public void onCompleted() {
        mTtsPresenter.onCompleted();
    }

    public void onlineResult(String unicode) {
        mSoundPresenter.onlineResult(unicode);
    }

    public void stopAll(String wakeUp) {
        mTtsPresenter.stopAll(wakeUp);
    }

    public void sendCustomMessage(RobotBean robotBean) {
        mChatPresenter.sendCustomMessage(robotBean);
    }

    public void onDataReceiverd(SerialBean serialBean) {
        mSerialPresenter.onDataReceiverd(serialBean);
    }

    public void doAnswer(String messageContent) {
        mTtsPresenter.doAnswer(messageContent);
    }

    public void setSpeech(boolean isSpeech) {
        mSoundPresenter.setSpeech(isSpeech);
    }

    public void stopHandler() {
        mTtsPresenter.stopHandler();
    }

    public void sendMessage(String identifier, String question) {
        mChatPresenter.sendServerMessage(question);
    }
}
