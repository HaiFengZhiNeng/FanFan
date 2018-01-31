package com.fanfan.novel.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.instance.SpeakIat;
import com.fanfan.novel.db.manager.NavigationDBManager;
import com.fanfan.novel.db.manager.VideoDBManager;
import com.fanfan.novel.db.manager.VoiceDBManager;
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.model.VideoBean;
import com.fanfan.novel.model.VoiceBean;
import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class LocalLexicon {

    private Context mContext;

    private VideoDBManager mVideoDBManager;
    private VoiceDBManager mVoiceDBManager;
    private NavigationDBManager mNavigationDBManager;

    private RobotLexiconListener mListener;

    private static LocalLexicon mInstance;

    public synchronized static LocalLexicon getInstance() {
        if (mInstance == null) {
            mInstance = new LocalLexicon();
        }
        return mInstance;
    }

    public LocalLexicon() {
        mContext = NovelApp.getInstance().getApplicationContext();
    }

    public LocalLexicon initDBManager() {
        mVideoDBManager = new VideoDBManager();
        mVoiceDBManager = new VoiceDBManager();
        mNavigationDBManager = new NavigationDBManager();
        return this;
    }


    public LocalLexicon setListener(RobotLexiconListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * 更新所有
     */
    public void updateContents() {
        if (mVideoDBManager == null || mVoiceDBManager == null || mNavigationDBManager == null) {
            throw new NullPointerException("local loxicon unll");
        }
        StringBuilder lexiconContents = new StringBuilder();
        //本地语音
        List<VoiceBean> voiceBeanList = mVoiceDBManager.loadAll();
        for (VoiceBean voiceBean : voiceBeanList) {
            lexiconContents.append(voiceBean.getShowTitle()).append("\n");
        }
        //本地视频
        List<VideoBean> videoBeanList = mVideoDBManager.loadAll();
        for (VideoBean videoBean : videoBeanList) {
            lexiconContents.append(videoBean.getShowTitle()).append("\n");
        }
        //本地导航
        List<NavigationBean> navigationBeanList = mNavigationDBManager.loadAll();
        for (NavigationBean navigationBean : navigationBeanList) {
            lexiconContents.append(navigationBean.getTitle()).append("\n");
        }

        lexiconContents.append(words2Contents());
        updateLocation(lexiconContents.toString());
    }

    public String words2Contents() {
        StringBuilder sb = new StringBuilder();
        List<String> words = getLocalStrings();
        for (String anArrStandard : words) {
            sb.append(anArrStandard).append("\n");
        }
        return sb.toString();
    }

    @NonNull
    public List<String> getLocalStrings() {

        List<String> words = new ArrayList<>();
        words.add(mContext.getResources().getString(R.string.FanFan));
        words.add(mContext.getResources().getString(R.string.Video));
        words.add(mContext.getResources().getString(R.string.Problem));
        words.add(mContext.getResources().getString(R.string.MultiMedia));
        words.add(mContext.getResources().getString(R.string.Seting_up));
        words.add(mContext.getResources().getString(R.string.Public_num));
        words.add(mContext.getResources().getString(R.string.Navigation));
        words.add(mContext.getResources().getString(R.string.Face));
        words.add(mContext.getResources().getString(R.string.Map));
        words.add(mContext.getResources().getString(R.string.Logout));
        words.add(mContext.getResources().getString(R.string.StopListener));
        words.add(mContext.getResources().getString(R.string.Back));
        words.add(mContext.getResources().getString(R.string.Forward));
        words.add(mContext.getResources().getString(R.string.Backoff));
        words.add(mContext.getResources().getString(R.string.Turnleft));
        words.add(mContext.getResources().getString(R.string.Turnright));
        words.add(mContext.getResources().getString(R.string.Artificial));
        words.add(mContext.getResources().getString(R.string.Face_check_in));
        words.add(mContext.getResources().getString(R.string.Instagram));
        words.add(mContext.getResources().getString(R.string.Witness_contrast));
        words.add(mContext.getResources().getString(R.string.Face_lifting_area));
        words.add(mContext.getResources().getString(R.string.Next));
        words.add(mContext.getResources().getString(R.string.Lase));
        return words;
    }


    public void updateLocation(String lexiconContents) {
        SpeechRecognizer mIat = SpeakIat.getInstance().mIat();
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 指定资源路径
        mIat.setParameter(ResourceUtil.ASR_RES_PATH,
                ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        // 指定语法路径
        mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
        // 指定语法名字
        mIat.setParameter(SpeechConstant.GRAMMAR_LIST, "local");
        // 设置文本编码格式
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // lexiconName 为词典名字，lexiconContents 为词典内容，lexiconListener 为回调监听器
        int ret = mIat.updateLexicon("voice", lexiconContents, new LexiconListener() {
            @Override
            public void onLexiconUpdated(String lexiconId, SpeechError error) {
                if (error == null) {
                    Print.e("词典更新成功");
                    if (mListener != null)
                        mListener.onLexiconSuccess();
                } else {
                    Print.e("词典更新失败,错误码：" + error.getErrorCode());
                    if (mListener != null)
                        mListener.onLexiconError(error.getErrorDescription());
                }
            }
        });
        if (ret != ErrorCode.SUCCESS) {
            Print.e("更新词典失败,错误码：" + ret);
        }
    }

    public interface RobotLexiconListener {

        void onLexiconSuccess();

        void onLexiconError(String error);
    }

}
