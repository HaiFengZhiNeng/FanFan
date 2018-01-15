package com.fanfan.novel.activity;

import android.content.Context;
import android.content.Intent;

import com.fanfan.robot.R;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.jcvideoplayer.JCVideoPlayerStandard;

import butterknife.BindView;

/**
 * Created by android on 2017/12/21.
 */

public class VideoDetailActivity extends BarBaseActivity {

    public static final String VIDEO_URL = "VideoUrl";

    @BindView(R.id.jc_video)
    JCVideoPlayerStandard mJcVideo;

    public static void newInstance(Context context, String url) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(VIDEO_URL, url);
        context.startActivity(intent);
    }


    private String upfile;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void initView() {
        super.initView();
    }


    @Override
    protected void initData() {
        upfile = getIntent().getStringExtra(VIDEO_URL);
        mJcVideo.setUp(upfile, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        mJcVideo.startVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mJcVideo.releaseAllVideos();
    }
}
