package com.fanfan.robot.presenter.ipersenter;

import android.graphics.Bitmap;

import com.fanfan.novel.common.presenter.BasePresenter;
import com.fanfan.novel.common.presenter.BaseView;
import com.fanfan.novel.model.FaceAuth;
import com.fanfan.robot.model.CheckIn;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.FaceIdentify;
import com.fanfan.youtu.api.face.bean.GetInfo;

import java.util.List;

/**
 * Created by android on 2018/1/10.
 */

public abstract class IFaceCheckinPresenter implements BasePresenter {

    private ICheckinView mBaseView;

    public IFaceCheckinPresenter(ICheckinView baseView) {
        mBaseView = baseView;
    }

    public abstract Bitmap bitmapSaturation(Bitmap baseBitmap);

    public abstract void setFaceIdentify();

    public abstract void faceIdentifyFace(Bitmap bitmap);

    public abstract void compareFace(FaceIdentify faceIdentify);

    public abstract void signToday(FaceAuth faceAuth, List<CheckIn> checkIns);

    public abstract void getPersonInfo(String person);

    public interface ICheckinView extends BaseView {

        void onError(BaseEvent event);

        void onError(int code, String msg);

        void identifyFaceFinish(String person);

        void identifyNoFace();

        void confidenceLow(FaceIdentify.IdentifyItem identifyItem);

        void chinkInSuccess(String authId);

        void isToday();

        void fromCloud(GetInfo getInfo);
    }

}
