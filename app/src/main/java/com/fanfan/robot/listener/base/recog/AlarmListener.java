package com.fanfan.robot.listener.base.recog;

import com.fanfan.robot.model.local.Asr;
import com.fanfan.robot.model.local.Trans;
import com.seabreeze.log.Print;

public class AlarmListener implements IRecogListener, NulState {


    protected static final String TAG = "AlarmListener ";

    private long speechEndTime;

    @Override
    public void onAsrBegin() {
        speechEndTime = System.currentTimeMillis();
        Print.i(TAG + "监听已启动，检测用户说话");
    }

    @Override
    public void onAsrPartialResult(Asr recogResult, String[] results) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < results.length; i++) {
            buffer.append(results[i]);
        }
        Print.i(TAG + "临时识别结果，结果是“" + buffer.toString() + "” , sc : " + recogResult.getSc() + " , ls" + recogResult.getLs());
        Print.i(recogResult);
    }

    @Override
    public void onAsrOnlineNluResult(int type, String nluResult) {
        if (nluResult != null) {
            Print.i(TAG + "原始语义识别 type " + type + " , 结果json：" + nluResult);
        }
    }

    @Override
    public void onAsrFinalResult(String result) {
        Print.i(TAG + "识别结束，结果是“" + result + "”");
        long diffTime = System.currentTimeMillis() - speechEndTime;
        Print.i(TAG + "说话结束到识别结束耗时【" + diffTime + "ms】");
    }

    @Override
    public void onAsrEnd() {
        Print.i(TAG + "检测到用户说话结束");
    }

    @Override
    public void onAsrVolume(int volumePercent, int volume) {
//        Print.i("用户说话音量 ： volumePercent ： " + volumePercent + " , volume : " + volume);
    }

    @Override
    public void onAsrFinishError(int errorCode, String errorMessage) {
        Print.i("errorCode : " + errorCode + " , errorMessage : " + errorMessage);
    }

    @Override
    public void onTrans(Trans trans) {

    }

    @Override
    public void onAsrLocalFinalResult(String result) {
        Print.i("本地语音识别结果 ： " + result);
    }

    @Override
    public void onAsrLocalDegreeLow(Asr local, int degree) {

        Print.i("本地识别置信度小 degree ： " + degree + " , local" + local);
    }
}
