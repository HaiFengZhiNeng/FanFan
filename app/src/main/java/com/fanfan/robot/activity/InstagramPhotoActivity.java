package com.fanfan.robot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.presenter.CameraPresenter;
import com.fanfan.novel.presenter.ipresenter.ICameraPresenter;
import com.fanfan.robot.R;
import com.fanfan.robot.presenter.TakePresenter;
import com.fanfan.robot.presenter.ipersenter.ITakePresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/9.
 */

public class InstagramPhotoActivity extends BarBaseActivity implements SurfaceHolder.Callback,
        ICameraPresenter.ICameraView, ITakePresenter.ITakeView {

    @BindView(R.id.camera_surfaceview)
    SurfaceView cameraSurfaceView;
    @BindView(R.id.tv_countTime)
    TextView tvCountTime;
    @BindView(R.id.time_layout)
    RelativeLayout timeLayout;
    @BindView(R.id.take_layout)
    RelativeLayout takeLayout;
    @BindView(R.id.choose_layout)
    LinearLayout chooseLayout;
    @BindView(R.id.share_layout)
    RelativeLayout shareLayout;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.iv_take_photo)
    ImageView ivTakePhoto;
    @BindView(R.id.iv_or_code)
    ImageView ivOrCode;


    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, InstagramPhotoActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private CameraPresenter mCameraPresenter;
    private TakePresenter mTakePresenter;

    private String mSavePath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_instagram_photo;
    }

    @Override
    protected void initView() {
        super.initView();
        SurfaceHolder holder = cameraSurfaceView.getHolder(); // 获得SurfaceHolder对象
        holder.addCallback(this); // 为SurfaceView添加状态监听
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCameraPresenter = new CameraPresenter(this, holder);
        mTakePresenter = new TakePresenter(this);

        timeLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mTakePresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraPresenter.closeCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTakePresenter.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTakePresenter.stopCountDownTimer();
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

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.tv_back, R.id.tv_share, R.id.tv_next, R.id.iv_take_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                mCameraPresenter.pictureTakenFinsih();
                chooseLayout.setVisibility(View.GONE);
                takeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_share:
                mCameraPresenter.pictureTakenFinsih();
                chooseLayout.setVisibility(View.GONE);
                mTakePresenter.sharePhoto(mSavePath);
                break;
            case R.id.tv_next:
                shareLayout.setVisibility(View.GONE);
                takeLayout.setVisibility(View.VISIBLE);
                mCameraPresenter.pictureTakenFinsih();
                break;
            case R.id.iv_take_photo:
                mCameraPresenter.cameraTakePicture();
                break;
        }
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
        mTakePresenter.startCountDownTimer();
    }

    @Override
    public void pictureTakenSuccess(String savePath) {
        showToast("拍照完成");
        takeLayout.setVisibility(View.GONE);
        timeLayout.setVisibility(View.GONE);
        chooseLayout.setVisibility(View.VISIBLE);
        mSavePath = savePath;
    }

    @Override
    public void pictureTakenFail() {
        mCameraPresenter.pictureTakenFinsih();
    }

    @Override
    public void autoFocusSuccess() {

    }

    @Override
    public void noFace() {

    }

    @Override
    public void tranBitmap(Bitmap bitmap, int num) {

    }

    @Override
    public void setCameraFaces(Camera.Face[] faces) {

    }

    @Override
    public void onTick(String l) {
        tvCountTime.setText(l);
    }

    @Override
    public void onFinish() {
        tvCountTime.setText("0");
        mCameraPresenter.cameraTakePicture();
    }

    @Override
    public void uploadSuccess(String url) {
        shareLayout.setVisibility(View.VISIBLE);
        Bitmap codeBitmap = mTakePresenter.generatingCode(url, 480, 480,
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo));
        ivOrCode.setImageBitmap(codeBitmap);
    }

}
