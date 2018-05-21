package com.fanfan.youtu;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fanfan.novel.common.Constants;
import com.fanfan.youtu.api.base.Constant;
import com.fanfan.youtu.api.base.OkhttpManager;
import com.fanfan.youtu.api.face.api.FaceAPI;
import com.fanfan.youtu.api.face.api.FaceImpl;
import com.fanfan.youtu.api.hfrobot.api.RobotAPI;
import com.fanfan.youtu.api.hfrobot.api.RobotImpl;
import com.fanfan.youtu.api.uploadfile.api.FilezooAPI;
import com.fanfan.youtu.api.uploadfile.api.FilezooImpl;
import com.fanfan.youtu.token.YoutuSign;
import com.seabreeze.log.Print;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by android on 2018/1/4.
 */

public class Youtucode implements FilezooAPI, FaceAPI, RobotAPI {

    private static FilezooImpl sFilezooImplement;
    private static FaceImpl sFaceImplement;
    private static RobotImpl sRobotImplement;

    private volatile static Youtucode mYoutucode;

    private Youtucode() {
    }

    public static Youtucode getSingleInstance() {
        if (null == mYoutucode) {
            synchronized (Youtucode.class) {
                if (null == mYoutucode) {
                    mYoutucode = new Youtucode();
                }
            }
        }
        return mYoutucode;
    }

    public static Youtucode init(@NonNull Context context) {

        OkhttpManager.getInstance().init();
        initImplement(context);
        return getSingleInstance();
    }


    private static void initImplement(Context context) {
        try {
            sFilezooImplement = new FilezooImpl(context);
            sFaceImplement = new FaceImpl(context);
            sRobotImplement = new RobotImpl(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String upfilesZoo(String zooPath) {
        return sFilezooImplement.upfilesZoo(zooPath);
    }

    @Override
    public String getGroupids() {
        return sFaceImplement.getGroupids();
    }

    @Override
    public String getPersonids() {
        return sFaceImplement.getPersonids();
    }

    @Override
    public String getFaceids(String personId) {
        return sFaceImplement.getFaceids(personId);
    }

    @Override
    public String getFaceinfo(String faceId) {
        return sFaceImplement.getFaceinfo(faceId);
    }

    @Override
    public String faceverify(String personId, Bitmap bitmap) {
        return sFaceImplement.faceverify(personId, bitmap);
    }

    @Override
    public String faceIdentify(Bitmap bitmap) {
        return sFaceImplement.faceIdentify(bitmap);
    }

    @Override
    public String newPerson(Bitmap bitmap, String personId) {
        return sFaceImplement.newPerson(bitmap, personId);
    }

    @Override
    public String newPerson(Bitmap bitmap, String personId, String personName) {
        return sFaceImplement.newPerson(bitmap, personId, personName);
    }

    @Override
    public String faceCompare(Bitmap bitmapA, Bitmap bitmapB) throws IOException {
        return sFaceImplement.faceCompare(bitmapA, bitmapB);
    }

    @Override
    public String modifyPersonName(String personId, String personName) {
        return sFaceImplement.modifyPersonName(personId, personName);
    }

    @Override
    public String modifyPersonTag(String personId, String tag) {
        return sFaceImplement.modifyPersonTag(personId, tag);
    }

    @Override
    public String delPerson(String personId) {
        return sFaceImplement.delPerson(personId);
    }

    @Override
    public String addFace(Bitmap bitmap, String personId) {
        return sFaceImplement.addFace(bitmap, personId);
    }

    @Override
    public String addFaces(List<Bitmap> bitmapArr, String personId) throws IOException {
        return sFaceImplement.addFaces(bitmapArr, personId);
    }

    @Override
    public String detectFace(Bitmap bitmap, int mode) {
        return sFaceImplement.detectFace(bitmap, mode);
    }

    @Override
    public String delFace(String personId, String faceId) {
        return sFaceImplement.delFace(personId, faceId);
    }

    @Override
    public String getInfo(String personId) {
        return sFaceImplement.getInfo(personId);
    }

    @Override
    public String updateProgram(int type) {
        return sRobotImplement.updateProgram(type);
    }

    @Override
    public String downloadFileWithDynamicUrlSync(String fileUrl) {
        return sRobotImplement.downloadFileWithDynamicUrlSync(fileUrl);
    }

    @Override
    public String requestProblem(String identifier, String problem, int id) {
        return sRobotImplement.requestProblem(identifier, problem, id);
    }

    @Override
    public String addSet(String user_name, String set_pwd) {
        return sRobotImplement.addSet(user_name, set_pwd);
    }

    @Override
    public String updateSet(String user_name, String set_pwd) {
        return sRobotImplement.updateSet(user_name, set_pwd);
    }

    @Override
    public String selectSet(String user_name) {
        return sRobotImplement.selectSet(user_name);
    }
}
