package com.fanfan.novel.presenter;

import android.content.res.Resources;
import android.os.Handler;

import com.fanfan.novel.model.SerialBean;
import com.fanfan.novel.presenter.ipresenter.ISerialPresenter;
import com.fanfan.novel.service.SerialService;
import com.fanfan.novel.service.event.ActivityToServiceEvent;
import com.fanfan.robot.R;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

/**
 * Created by android on 2017/12/26.
 */

public class SerialPresenter extends ISerialPresenter {

    private ISerialView mSerialView;

    private Handler mHandler;

    public SerialPresenter(ISerialView baseView) {
        super(baseView);
        this.mSerialView = baseView;

        mHandler = new Handler();
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void receiveMotion(final int type, final String motion) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Print.e("send " + motion);
                SerialBean serialBean = new SerialBean();
                serialBean.setBaudRate(type);
                serialBean.setMotion(motion);
                ActivityToServiceEvent serialEvent = new ActivityToServiceEvent("");
                serialEvent.setEvent(200, serialBean);
                EventBus.getDefault().post(serialEvent);
            }
        }, 100);

    }


    @Override
    public void onDataReceiverd(SerialBean serialBean) {
        int iBaudRate = serialBean.getBaudRate();
        String motion = serialBean.getMotion();
        if (iBaudRate == SerialService.DEV_BAUDRATE) {

        } else if (iBaudRate == SerialService.VOICE_BAUDRATE) {
            if (motion.toString().contains("WAKE UP!")) {

                mSerialView.stopAll();
                if (motion.toString().contains("##### IFLYTEK")) {

                    String str = motion.toString().substring(motion.toString().indexOf("angle:") + 6, motion.toString().indexOf("##### IFLYTEK"));
                    int angle = Integer.parseInt(str.trim());
                    Print.e("解析到应该旋转的角度 : " + angle);
                    if (0 <= angle && angle < 30) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A521821EAA");
                    } else if (30 <= angle && angle <= 60) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A521823CAA");
                    } else if (120 <= angle && angle <= 150) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A5218278AA");
                    } else if (150 < angle && angle <= 180) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A5218296AA");
                    }
                }
            }

        } else if (iBaudRate == SerialService.CRUISE_BAUDRATE) {
            if (Arrays.asList(resFoFinal(R.array.navigation_data)).contains(motion.toString().trim())) {

//                receiveMotion(SerialService.DEV_BAUDRATE, "A50C8001AA");
                mSerialView.onMoveStop();
            }
        }
    }

    private String[] resFoFinal(int id) {
        String[] res = mSerialView.getContext().getResources().getStringArray(id);
        return res;
    }


}
