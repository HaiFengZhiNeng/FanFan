package com.fanfan.novel.activity;

import android.graphics.BitmapFactory;

import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.robot.R;
import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import butterknife.BindView;

/**
 * Created by android on 2018/1/22.
 */

public class Main2Activity extends BarBaseActivity {

    @BindView(R.id.mVrPanoramaView)
    VrPanoramaView mVrPanoramaView;

    private VrPanoramaView.Options paNormalOptions;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_google_vr;
    }


    @Override
    protected void initData() {
        initVrPaNormalView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVrPanoramaView.resumeRendering();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVrPanoramaView.pauseRendering();
    }


    @Override
    protected void onDestroy() {
        // Destroy the widget and free memory.
        super.onDestroy();
        mVrPanoramaView.shutdown();
    }


    //初始化VR图片
    private void initVrPaNormalView() {
        paNormalOptions = new VrPanoramaView.Options();
        //图像类型为立体图像
        paNormalOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
        mVrPanoramaView.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_MONO);//全屏模式，弹出一个全屏的Dialog

//        mVrPanoramaView.setFullscreenButtonEnabled (false); //隐藏全屏模式按钮
        mVrPanoramaView.setInfoButtonEnabled(false); //设置隐藏最左边信息的按钮
        mVrPanoramaView.setStereoModeButtonEnabled(false); //设置隐藏立体模型的按钮
        mVrPanoramaView.setEventListener(new ActivityEventListener()); //设置监听
        //加载本地的图片源
        mVrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.andes), paNormalOptions);
        //设置网络图片源
//        panoWidgetView.loadImageFromByteArray();
    }

    private class ActivityEventListener extends VrPanoramaEventListener {
        @Override
        public void onLoadSuccess() {//图片加载成功
        }


        @Override
        public void onLoadError(String errorMessage) {//图片加载失败
        }

        @Override
        public void onClick() {//当我们点击了VrPanoramaView 时候触发            super.onClick();
        }

        @Override
        public void onDisplayModeChanged(int newDisplayMode) {
            //改变显示模式时候出发（全屏模式和纸板模式）
            super.onDisplayModeChanged(newDisplayMode);
        }
    }
}
