package com.fanfan.youtu;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.fanfan.youtu.api.face.api.FaceAPI;
import com.fanfan.youtu.api.face.api.FaceImpl;
import com.fanfan.youtu.api.uploadfile.api.FilezooAPI;
import com.fanfan.youtu.api.uploadfile.api.FilezooImpl;

import java.io.IOException;
import java.util.List;

/**
 * Created by android on 2018/1/4.
 */

public class Youtucode implements FilezooAPI, FaceAPI {

    private static FilezooImpl sFilezooImplement;
    private static FaceImpl sFaceImplement;

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

        initImplement(context);
        return getSingleInstance();
    }

    private static void initImplement(Context context) {
        try {
            sFilezooImplement = new FilezooImpl(context);
            sFaceImplement = new FaceImpl(context);
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
}
