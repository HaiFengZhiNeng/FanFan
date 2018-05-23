package com.fanfan.robot.listener.base.recog.lexicon;

public interface HotLexiconListener {

    void onCloudLexiconUpdatedSuccess();

    void onLocalLexiconUpdatedSuccess();

    void onLexiconUpdatedError(int errorCode, String errorDescription);
}
