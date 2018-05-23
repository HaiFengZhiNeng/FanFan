package com.fanfan.robot.listener.base.recog;

import android.os.Bundle;
import android.util.ArrayMap;

import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.model.local.Asr;
import com.fanfan.robot.model.local.Cw;
import com.fanfan.robot.model.local.Trans;
import com.fanfan.robot.model.local.Ws;
import com.fanfan.youtu.utils.GsonUtil;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.seabreeze.log.Print;

import java.util.List;

public class RecogEventAdapter implements RecognizerListener, NulState {

    private IRecogListener listener;

    private ArrayMap<Integer, String> mIatResults;

    public RecogEventAdapter(IRecogListener listener) {
        this.listener = listener;
        mIatResults = new ArrayMap<>();
    }


    @Override
    public void onVolumeChanged(int volume, byte[] bytes) {
        listener.onAsrVolume(bytes.length, volume);
    }

    @Override
    public void onBeginOfSpeech() {
        listener.onAsrBegin();
    }

    @Override
    public void onEndOfSpeech() {
        String engineType = RobotInfo.getInstance().getEngineType();
        if (engineType.equals(SpeechConstant.TYPE_CLOUD)) {
            if (mIatResults.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (String result : mIatResults.values()) {
                    sb.append(result);
                }
                listener.onAsrFinalResult(sb.toString());
            } else {
                listener.onAsrOnlineNluResult(STATUS_END, null);
            }
            mIatResults.clear();
            listener.onAsrEnd();
        }
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean isLast) {

        String engineType = RobotInfo.getInstance().getEngineType();
        if (engineType.equals(SpeechConstant.TYPE_LOCAL)) {

            Asr local = GsonUtil.GsonToBean(recognizerResult.getResultString(), Asr.class);

            if (local.getSc() > 30) {

                List<Ws> wsList = local.getWs();

                StringBuilder sbLocal = new StringBuilder();

                for (int i = 0; i < wsList.size(); i++) {
                    Ws ws = wsList.get(i);
                    List<Cw> cwList = ws.getCw();
                    for (int j = 0; j < cwList.size(); j++) {
                        Cw cw = cwList.get(j);
                        if (!sbLocal.equals(cw.getW())) {
                            sbLocal.append(cw.getW());
                        }
                    }
                }

                listener.onAsrLocalFinalResult(sbLocal.toString());
            } else {
                listener.onAsrLocalDegreeLow(local, local.getSc());
            }

        } else if (engineType.equals(SpeechConstant.TYPE_CLOUD)) {

            if (RobotInfo.getInstance().isTranslateEnable()) {
                Trans trans = GsonUtil.GsonToBean(recognizerResult.getResultString(), Trans.class);
                listener.onTrans(trans);
            } else {

                Asr line = GsonUtil.GsonToBean(recognizerResult.getResultString(), Asr.class);
                if (!line.getLs()) {

                    List<Ws> wsList = line.getWs();

                    if (wsList.size() > 1) {
                        String[] results = new String[wsList.size()];
                        StringBuilder builder = new StringBuilder();

                        for (int i = 0; i < wsList.size(); i++) {
                            Ws ws = wsList.get(i);
                            List<Cw> cwList = ws.getCw();
                            results[i] = cwList.get(0).getW();
                            builder.append(cwList.get(0).getW());
                        }

                        mIatResults.put(line.getSn(), builder.toString());
                        listener.onAsrPartialResult(line, results);
                    } else if (wsList.size() == 1) {
                        String w = wsList.get(0).getCw().get(0).getW();
                        if (!w.equals("")) {
                            mIatResults.put(line.getSn(), w);
                            listener.onAsrPartialResult(line, new String[]{w});
                        } else {
                            listener.onAsrOnlineNluResult(STATE_W_EMPTY, recognizerResult.getResultString());
                        }
                    } else {
                        listener.onAsrOnlineNluResult(STATUS_LIST_NUL, recognizerResult.getResultString());
                    }
                } else {

                    listener.onAsrOnlineNluResult(STATUS_LS_FALSE, recognizerResult.getResultString());
                }
            }
        }

    }

    @Override
    public void onError(SpeechError speechError) {
        listener.onAsrFinishError(speechError.getErrorCode(), speechError.getErrorDescription());
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }
}
