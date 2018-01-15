package com.fanfan.robot.app;

import android.content.Context;

import com.fanfan.novel.common.Constants;
import com.fanfan.novel.utils.PreferencesUtils;
import com.iflytek.cloud.SpeechConstant;
import com.seabreeze.log.Print;

/**
 * Created by android on 2018/1/5.
 */

public class RobotInfo {

    private volatile static RobotInfo instance;

    private RobotInfo() {
    }

    public static RobotInfo getInstance() {
        if (instance == null) {
            synchronized (RobotInfo.class) {
                if (instance == null) {
                    instance = new RobotInfo();
                }
            }
        }
        return instance;
    }

    public RobotInfo init(Context context) {
        setEngineType(SpeechConstant.TYPE_CLOUD);
        setControlId(Constants.controlId);
        setRoomId(Constants.roomAVId);
        setTtsLineTalker(PreferencesUtils.getString(context, Constants.IAT_LINE_LANGUAGE_TALKER, "xiaoyan"));
        setTtsLocalTalker(PreferencesUtils.getString(context, Constants.IAT_LOCAL_LANGUAGE_TALKER, "xiaoyan"));
        setIatLineHear(PreferencesUtils.getString(context, Constants.IAT_LINE_LANGUAGE_HEAR, "xiaoyan"));
        setIatLocalHear(PreferencesUtils.getString(context, Constants.IAT_LOCAL_LANGUAGE_HEAR, "xiaoyan"));
        isInitialization = PreferencesUtils.getBoolean(context, Constants.IS_INITIALIZATION, false);
        return getInstance();
    }

    //IAT_CLOUD_BUILD
    private boolean cloudBuild;

    public boolean isCloudBuild() {
        if (cloudBuild)
            return true;
        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_CLOUD_BUILD, false);
    }

    public void setCloudBuild() {
        Print.e("在线语法构建成功");
        this.cloudBuild = true;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_CLOUD_BUILD, true);
    }

    //IAT_LOCAL_BUILD
    private boolean localBuild;

    public boolean isLocalBuild() {
        if (localBuild)
            return true;
        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_BUILD, false);
    }

    public void setLocalBuild() {
        Print.e("本地语法构建成功");
        this.localBuild = true;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_BUILD, true);
    }

    //IAT_CLOUD_UPDATELEXICON
    private boolean cloudUpdatelexicon;

    public boolean isCloudUpdatelexicon() {
        if (cloudUpdatelexicon)
            return true;
        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_CLOUD_UPDATELEXICON, false);
    }

    public void setCloudUpdatelexicon() {
        Print.e("在线热词上传成功");
        this.cloudUpdatelexicon = true;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_CLOUD_UPDATELEXICON, true);
    }

    //IAT_LOCAL_UPDATELEXICON
    private boolean localUpdatelexicon;

    public boolean isLocalUpdatelexicon() {
        if (localUpdatelexicon)
            return true;
        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_UPDATELEXICON, false);
    }

    public void setLocalUpdatelexicon() {
        Print.e("本地词典更新成功");
        this.localUpdatelexicon = true;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_UPDATELEXICON, true);
    }

    //本地在线语音控制
    private String mEngineType;

    public String getEngineType() {
        return mEngineType;
    }

    public void setEngineType(String engineType) {
        this.mEngineType = engineType;
    }

    //需要设置可以连接的控制端id
    private String controlId;

    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
    }

    //需要设置进入的群，以便控制端一对多
    private String roomId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    //在线的发言人
    private String ttsLineTalker;

    public String getTtsLineTalker() {
        return ttsLineTalker;
    }

    public void setTtsLineTalker(String ttsLineTalker) {
        this.ttsLineTalker = ttsLineTalker;
    }

    //离线的发言人
    private String ttsLocalTalker;

    public String getTtsLocalTalker() {
        return ttsLocalTalker;
    }

    public void setTtsLocalTalker(String ttsLocalTalker) {
        this.ttsLocalTalker = ttsLocalTalker;
    }

    //在线的监听人
    private String iatLineHear;

    public String getIatLineHear() {
        return iatLineHear;
    }

    public void setIatLineHear(String iatLineHear) {
        this.iatLineHear = iatLineHear;
    }

    //离线的监听人
    private String iatLocalHear;

    public String getIatLocalHear() {
        return iatLocalHear;
    }

    public void setIatLocalHear(String iatLocalHear) {
        this.iatLocalHear = iatLocalHear;
    }

    //是否已经构建语法，上传热词等
    private boolean isInitialization;

    public boolean isInitialization() {
        return isInitialization;
    }

    public void setInitialization(boolean initialization) {
        isInitialization = initialization;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IS_INITIALIZATION, isInitialization);
    }
}
