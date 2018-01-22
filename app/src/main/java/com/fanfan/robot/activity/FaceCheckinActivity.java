package com.fanfan.robot.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.db.manager.FaceAuthDBManager;
import com.fanfan.novel.model.FaceAuth;
import com.fanfan.novel.presenter.CameraPresenter;
import com.fanfan.novel.presenter.ipresenter.ICameraPresenter;
import com.fanfan.novel.ui.camera.DetectOpenFaceView;
import com.fanfan.novel.ui.camera.DetectionFaceView;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.db.CheckInDBManager;
import com.fanfan.robot.model.CheckIn;
import com.fanfan.robot.presenter.FaceCheckinPresenter;
import com.fanfan.robot.presenter.ipersenter.IFaceCheckinPresenter;
import com.fanfan.youtu.api.face.bean.FaceIdentify;
import com.fanfan.youtu.api.face.bean.GetInfo;
import com.seabreeze.log.Print;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.facedetect.DetectionBasedTracker;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/9.
 */

public class FaceCheckinActivity extends BarBaseActivity implements SurfaceHolder.Callback,
        ICameraPresenter.ICameraView, IFaceCheckinPresenter.ICheckinView {

    @BindView(R.id.camera_surfaceview)
    SurfaceView cameraSurfaceView;
    @BindView(R.id.detection_face_view)
    DetectionFaceView detectionFaceView;
    @BindView(R.id.opencv_face_view)
    DetectOpenFaceView opencvFaceView;
    @BindView(R.id.tv_sign_info)
    TextView tvSignInfo;
    @BindView(R.id.tv_sign_all)
    TextView tvSignAll;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, FaceCheckinActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    //opencv
    private Mat mRgba;
    private Mat mGray;

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    private int mAbsoluteFaceSize = 0;
    private float mRelativeFaceSize = 0.2f;
    private int mDetectorType = JAVA_DETECTOR;
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    System.loadLibrary("detection_based_tracker");

                    try {
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            mJavaDetector = null;
                        } else

                            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private CameraPresenter mCameraPresenter;
    private FaceCheckinPresenter mCheckinPresenter;

    private FaceAuthDBManager mFaceAuthDBManager;
    private CheckInDBManager mCheckInDBManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check_in;
    }

    @Override
    protected void initView() {
        super.initView();
        SurfaceHolder holder = cameraSurfaceView.getHolder(); // 获得SurfaceHolder对象
        holder.addCallback(this); // 为SurfaceView添加状态监听
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mRgba = new Mat();
        mGray = new Mat();

        mCameraPresenter = new CameraPresenter(this, holder);

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        mCheckinPresenter = new FaceCheckinPresenter(this);
    }

    @Override
    protected void initData() {
        mFaceAuthDBManager = new FaceAuthDBManager();
        mCheckInDBManager = new CheckInDBManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCheckinPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraPresenter.closeCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCheckinPresenter.finish();
    }

    @SuppressLint("NewApi")
    @OnClick({R.id.tv_sign_info, R.id.tv_sign_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_sign_info:
                break;
            case R.id.tv_sign_all:
                SignAllActivity.newInstance(this);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_white, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                SignAllActivity.newInstance(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCameraPresenter.openCamera();
        mCameraPresenter.doStartPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCameraPresenter.setMatrix(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCameraPresenter.closeCamera();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void showMsg(int msg) {
        showToast(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void previewFinish() {

    }

    @Override
    public void pictureTakenSuccess(String savePath) {

    }

    @Override
    public void pictureTakenFail() {

    }

    @Override
    public void autoFocusSuccess() {

    }

    @Override
    public void noFace() {
        detectionFaceView.clear();
        opencvFaceView.clear();
    }

    @Override
    public void tranBitmap(Bitmap bitmap, int num) {

        mCheckinPresenter.faceIdentifyFace(bitmap);

        if (!CameraPresenter.unusual) {
            opencvDraw(bitmap);
        }
    }

    private void opencvDraw(Bitmap bitmap) {
        Utils.bitmapToMat(bitmap, mRgba);
        Mat mat1 = new Mat();
        Utils.bitmapToMat(bitmap, mat1);
        Imgproc.cvtColor(mat1, mGray, Imgproc.COLOR_BGR2GRAY);
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }
        MatOfRect faces = new MatOfRect();
        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)

                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        }
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            opencvFaceView.setFaces(facesArray, mCameraPresenter.getOrientionOfCamera());
        }
    }

    @Override
    public void setCameraFaces(Camera.Face[] faces) {
        if (faces.length > 0) {
            detectionFaceView.setFaces(faces, mCameraPresenter.getOrientionOfCamera());
        }
    }

    @Override
    public void identifyFaceFinish(String person) {
        FaceAuth faceAuth = mFaceAuthDBManager.queryByPersonId(person);
        if (faceAuth != null) {
            List<CheckIn> checkIns = mCheckInDBManager.queryByName(faceAuth.getAuthId());
            mCheckinPresenter.signToday(faceAuth, checkIns);
        } else {
            mCheckinPresenter.getPersonInfo(person);
        }
    }

    @Override
    public void identifyNoFace() {
        tvSignInfo.setText("请正对屏幕或您未注册个人信息");
    }

    @Override
    public void confidenceLow(FaceIdentify.IdentifyItem identifyItem) {

        tvSignInfo.setText(String.format("识别度为 %s， 较低。请正对屏幕或您未注册个人信息", identifyItem.getConfidence()));
    }

    @Override
    public void chinkInSuccess(String authId) {
        CheckIn checkIn = new CheckIn();
        checkIn.setName(authId);
        checkIn.setTime(System.currentTimeMillis());
        boolean insert = mCheckInDBManager.insert(checkIn);
        if (insert) {
            tvSignInfo.setText(String.format("%s 已签到", authId));
            List<CheckIn> checkIns = mCheckInDBManager.queryByName(authId);
            List<CheckIn> screenIns = new ArrayList<>();
            for (CheckIn in : checkIns) {
                if (TimeUtils.isToday(in.getTime())) {
                    screenIns.add(in);
                }
            }
            if (screenIns.size() > 0) {
                Collections.sort(screenIns);
                for (int i = 0; i < screenIns.size() - 1; i++) {
                    mCheckInDBManager.delete(screenIns.get(i));
                }
            }
        }
    }

    @Override
    public void isToday() {
        tvSignInfo.setText("今日您已签到");
    }

    @Override
    public void fromCloud(GetInfo getInfo) {
        FaceAuth faceAuth = new FaceAuth();
        faceAuth.setAuthId(getInfo.getPerson_name());
        faceAuth.setPersonId(getInfo.getPerson_id());
        faceAuth.setFaceCount(1);
        faceAuth.setSaveTime(System.currentTimeMillis());
        boolean insert = mFaceAuthDBManager.insert(faceAuth);
        if (insert) {
            chinkInSuccess(getInfo.getPerson_name());
            mCheckinPresenter.setFaceIdentify();
        }
    }
}
