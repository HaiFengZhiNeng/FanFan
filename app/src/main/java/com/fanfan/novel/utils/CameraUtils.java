package com.fanfan.novel.utils;

import android.app.Activity;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

public class CameraUtils {


    public static int setCameraDisplayOrientation(Activity activity, int paramInt) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(paramInt, info);
        int rotation = ((WindowManager) activity.getSystemService("window")).getDefaultDisplay().getRotation(); // 获得显示器件角度
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }


}
