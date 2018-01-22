package com.fanfan.robot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.glide.GlideRoundTransform;
import com.fanfan.novel.model.PersonInfo;
import com.fanfan.novel.presenter.CameraPresenter;
import com.fanfan.novel.presenter.ipresenter.ICameraPresenter;
import com.fanfan.novel.ui.camera.DetectOpenFaceView;
import com.fanfan.novel.ui.camera.DetectionFaceView;
import com.fanfan.robot.R;
import com.fanfan.robot.presenter.FaceVerifPresenter;
import com.fanfan.robot.presenter.HsOtgPresenter;
import com.fanfan.robot.presenter.ipersenter.IFaceVerifPresenter;
import com.fanfan.robot.presenter.ipersenter.IHsOtgPresenter;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.utils.ErrorMsg;
import com.huashi.otg.sdk.HsOtgService;
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
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by android on 2018/1/10.
 */

public class AuthenticationActivity extends BarBaseActivity implements SurfaceHolder.Callback,
        ICameraPresenter.ICameraView, IFaceVerifPresenter.IFaceverifView, IHsOtgPresenter.IHsOtgView {

    @BindView(R.id.camera_surfaceview)
    SurfaceView cameraSurfaceView;
    @BindView(R.id.detection_face_view)
    DetectionFaceView detectionFaceView;
    @BindView(R.id.opencv_face_view)
    DetectOpenFaceView opencvFaceView;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_family)
    TextView tvFamily;
    @BindView(R.id.tv_birth)
    TextView tvBirth;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.person_info_layout)
    LinearLayout infoLayout;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
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
    private FaceVerifPresenter mFaceVerifPresenter;
    private HsOtgPresenter mHsOtgPresenter;

    private PersonInfo personInfo;
    private Bitmap bitmapB;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_authentication;
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
        mFaceVerifPresenter = new FaceVerifPresenter(this);

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        mHsOtgPresenter = new HsOtgPresenter(this);

        mHsOtgPresenter.start();

        mHandler.postDelayed(testRun, 5000);
    }

    Runnable testRun = new Runnable() {
        @Override
        public void run() {
            bitmapB = BitmapFactory.decodeResource(getResources(), R.mipmap.compare_a);
            personInfo = new PersonInfo();
            personInfo.setIDCard("142622199205180071");
            personInfo.setName("张涛");
            personInfo.setGender("男");
            personInfo.setFamily("汉");
            personInfo.setBirth(new SimpleDateFormat("yyyy年MM月dd日").format(System.currentTimeMillis()));
            personInfo.setAddress("山西省翼城县坩南环路23号加加加加加加加加加加加加加加");
            personInfo.setDepartment("山西省翼城县");
            personInfo.setStrartDate("");
            personInfo.setEndDate("");
            personInfo.setFristPFInfo("");
            personInfo.setSecondPFInfo("");
            personInfo.setHeadUrl(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib/compare_a.png");
            setPersonInfo();
        }
    };

    @Override
    protected void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFaceVerifPresenter.start();
        mHsOtgPresenter.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraPresenter.closeCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFaceVerifPresenter.finish();
        mHsOtgPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHsOtgPresenter.finish();
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
    public void onError(BaseEvent event) {
        super.onError(event);
        mHsOtgPresenter.compareFail();
    }

    @Override
    public void onError(int code, String msg) {
        Print.e("onError  code : " + code + " ; msg : " + msg + " ; describe : " + ErrorMsg.getCodeDescribe(code));
        mHsOtgPresenter.compareFail();
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

        if (personInfo != null && bitmapB != null) {
            mFaceVerifPresenter.faceCompare(bitmap, bitmapB);
        }

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
    public void compareSuccess() {
        Print.e("身份证头像认证成功");
        showToast("身份认证成功，注册人脸信息，请正对摄像头");
    }

    @Override
    public void similarityLow(float similarity) {
        showToast("识别度较低，相似度为 ： " + similarity + ", 请重新将省份证放入感应区");
        Print.e("识别度较低，相似度为 ： " + similarity);
        bitmapB = null;
        mHsOtgPresenter.compareFail();
    }

    @Override
    public void faceCompare(Bitmap bitmap) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_head)
                .error(R.mipmap.ic_head)
                .priority(Priority.HIGH)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transform(new GlideRoundTransform());

        Glide.with(this).load(personInfo.getHeadUrl())
                .apply(options)
                .into(ivHead);
    }

    @Override
    public void init(int code) {
        if (code == 1) {
            Print.e("身份证已连接");
            mHsOtgPresenter.authenticate();
        } else {
            mHsOtgPresenter.authFail();
        }
    }

    @Override
    public void authenticate(int code) {
        switch (code) {
            case 1:
                Print.e("卡认证成功");
                mHsOtgPresenter.readCard();
                break;
            case 2:
                Print.e("卡认证失败");
                mHsOtgPresenter.authFail();
                break;
            case 0:
                Print.e("未连接");
                mHsOtgPresenter.authFail();
                break;
        }
    }

    @Override
    public void readCard(int code) {
        if (code == 1) {
            mHsOtgPresenter.identityRead(personInfo);
        } else {
            Print.e("读卡失败");
            mHsOtgPresenter.authFail();
        }
    }

    @Override
    public void identityFinish(PersonInfo info) {

        String headUrl = info.getHeadUrl();
        bitmapB = BitmapFactory.decodeFile(headUrl);
        personInfo = info;
        setPersonInfo();
    }

    private void setPersonInfo() {
        infoLayout.setVisibility(View.VISIBLE);
        Glide.with(this).load(personInfo.getHeadUrl())
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(ivHead);
        tvName.setText(personInfo.getName());
        tvGender.setText(personInfo.getGender());
        tvFamily.setText(personInfo.getGender());
        tvBirth.setText(personInfo.getBirth());
        tvAddress.setText(personInfo.getAddress());
    }

    @Override
    public void identityFail(String msg) {
        Print.e(msg);
        mHsOtgPresenter.authFail();
    }

}
