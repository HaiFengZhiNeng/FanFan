package com.fanfan.novel.model.map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.EditText;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.services.core.LatLonPoint;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by android on 2018/3/2.
 */

public class AMapUtil {

    /**
     * 判断高德地图app是否已经安装
     */
    public static boolean getAppIn(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.autonavi.minimap", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        // 本手机没有安装高德地图app
        if (packageInfo != null) {
            return true;
        }
        // 本手机成功安装有高德地图app
        else {
            return false;
        }
    }

    /**
     * 调起高德地图导航功能，如果没安装高德地图，会进入异常，可以在异常中处理，调起高德地图app的下载页面
     */
    public static void startAMapNavi(Context context, Marker marker) {
        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(marker.getPosition());
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(NaviPara.DRIVING_AVOID_CONGESTION);

        // 调起高德地图导航
        try {
            AMapUtils.openAMapNavi(naviPara, context.getApplicationContext());
        } catch (com.amap.api.maps.AMapException e) {

            // 如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(context.getApplicationContext());
        }
    }

    public static String checkEditText(EditText editText) {
        if (editText != null && editText.getText() != null
                && !(editText.getText().toString().trim().equals(""))) {
            return editText.getText().toString().trim();
        } else {
            return "";
        }
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    public static String getFriendlyTime(int second) {
        if (second > 3600) {
            int hour = second / 3600;
            int miniate = (second % 3600) / 60;
            return hour + "小时" + miniate + "分钟";
        }
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }

    public static String getFriendlyLength(int lenMeter) {
        if (lenMeter > 10000) // 10 km
        {
            int dis = lenMeter / 1000;
            return dis + ChString.Kilometer;
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + ChString.Kilometer;
        }

        if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + ChString.Meter;
        }

        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }

        return dis + ChString.Meter;
    }

    private static SimpleDateFormat sdf = null;

    public static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }
}
