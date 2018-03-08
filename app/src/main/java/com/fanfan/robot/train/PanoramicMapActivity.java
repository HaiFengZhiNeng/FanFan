package com.fanfan.robot.train;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.VrImageAdapter;
import com.fanfan.robot.model.VrImage;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/3/7/007.
 */

public class PanoramicMapActivity extends BarBaseActivity {

    @BindView(R.id.vr_panorama_view)
    VrPanoramaView mVrPanoramaView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, PanoramicMapActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private VrPanoramaView.Options options;
    private Bitmap paNormalBitmap;

    private List<VrImage> vrImages = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_panorama_detail;
    }

    @Override
    protected void initData() {
//        mVrPanoramaView.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_MONO);//全屏模式，弹出一个全屏的Dialog
        mVrPanoramaView.setInfoButtonEnabled(false);//信息按钮禁掉
        mVrPanoramaView.setStereoModeButtonEnabled(false);//眼镜模式按钮禁掉
        mVrPanoramaView.setFullscreenButtonEnabled(false);//全屏模式按钮禁掉
        mVrPanoramaView.setTouchTrackingEnabled(true); //开启手触模式

        options = new VrPanoramaView.Options();
        //设置图片类型为单通道图片
        options.inputType = VrPanoramaView.Options.TYPE_MONO;

        paNormalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.andes);
        mVrPanoramaView.loadImageFromBitmap(paNormalBitmap, options);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        VrImageAdapter vrImageAdapter = new VrImageAdapter(vrImages);
        recyclerView.setAdapter(vrImageAdapter);

        for (int i = 0; i < 10; i++) {
            VrImage vrImage = new VrImage();
            vrImage.setName("测试图片1");
            vrImage.setPath(R.drawable.andes);
            vrImages.add(vrImage);
        }

        vrImageAdapter.replaceData(vrImages);

        vrImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VrImage vrImage = vrImages.get(position);
                int path = (int) vrImage.getPath();
                paNormalBitmap = BitmapFactory.decodeResource(getResources(), path);
                mVrPanoramaView.loadImageFromBitmap(paNormalBitmap, options);
            }
        });
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
        super.onDestroy();
        mVrPanoramaView.shutdown();
    }

    @OnClick(R.id.vr_panorama_view)
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.vr_panorama_view:

                break;
        }
    }
}
