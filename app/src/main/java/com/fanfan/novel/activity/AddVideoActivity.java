package com.fanfan.novel.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.instance.SpeakIat;
import com.fanfan.novel.db.manager.NavigationDBManager;
import com.fanfan.novel.db.manager.VideoDBManager;
import com.fanfan.novel.db.manager.VoiceDBManager;
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.model.VideoBean;
import com.fanfan.novel.model.VoiceBean;
import com.fanfan.novel.utils.AppUtil;
import com.fanfan.novel.utils.BitmapUtils;
import com.fanfan.novel.utils.MediaFile;
import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/6.
 */

public class AddVideoActivity extends BarBaseActivity {

    @BindView(R.id.img_video)
    ImageView imgVideo;
    @BindView(R.id.et_video_shart)
    TextView etVideoShart;

    public static final String VIDEO_ID = "videoId";
    public static final int ADD_VIDEO_REQUESTCODE = 223;

    public static final int CHOOSE_VIDEO = 4;//选择视频

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, AddVideoActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void newInstance(Activity context, long id, int requestCode) {
        Intent intent = new Intent(context, AddVideoActivity.class);
        intent.putExtra(VIDEO_ID, id);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private long saveLocalId;

    private VideoDBManager mVideoDBManager;

    private VideoBean videoBean;

    private SpeechRecognizer mIat;
    private VoiceDBManager mVoiceDBManager;
    private NavigationDBManager mNavigationDBManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_video;
    }

    @Override
    protected void initData() {
        saveLocalId = getIntent().getLongExtra(VIDEO_ID, -1);

        mVideoDBManager = new VideoDBManager();

        if (saveLocalId != -1) {
            videoBean = mVideoDBManager.selectByPrimaryKey(saveLocalId);

            String savePath = videoBean.getVideoImage();
            if (savePath != null) {
                if (new File(savePath).exists()) {
                    imgVideo.setVisibility(View.VISIBLE);
                    Glide.with(AddVideoActivity.this).load(savePath)
                            .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE).error(R.mipmap.ic_logo))
                            .into(imgVideo);
                }
            }
            etVideoShart.setText(videoBean.getShowTitle());

        }
    }

    @Override
    protected void onDestroy() {
        if (mIat != null) {
            mIat.destroy();
        }
        listener = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.finish_black, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:

                if (videoBean == null) {
                    showToast("请选择视频！");
                    break;
                }
                if (isEmpty(etVideoShart)) {
                    showToast("视频名称不能为空！");
                    break;
                }
                if (etVideoShart.getText().toString().trim().length() > 20) {
                    showToast("输入 20 字以内");
                    break;
                }
                if (saveLocalId == -1) {
                    List<VideoBean> been = mVideoDBManager.queryVoiceByQuestion(etVideoShart.getText().toString().trim());
                    if (!been.isEmpty()) {
                        showToast("请不要添加相同的视频！");
                        break;
                    }
                }
                videoIsexit();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.tv_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_video:
                if (etVideoShart.getText().toString().trim().equals("")) {
                    showToast("输入不能为空！");
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, CHOOSE_VIDEO);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (null != data) {
                    Uri uri = data.getData();
                    if (uri == null) {
                        return;
                    }
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));//// 视频路径
                            Print.e("视频路径 ： " + videoPath);

                            if (MediaFile.isVideoFileType(videoPath)) {
                                int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));//// 视频名称
                                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));//// 视频大小

                                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));//// 视频缩略图路径
                                // 方法二 ThumbnailUtils 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
                                // 第一个参数为 视频/缩略图的位置，第二个依旧是分辨率相关的kind
                                Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(imagePath, MediaStore.Video.Thumbnails.MINI_KIND);
                                String savePath = Constants.PROJECT_PATH + "video" + File.separator + title + ".jpg";
                                BitmapUtils.saveBitmapToFile(bitmap2, "video", title + ".jpg");

                                imgVideo.setVisibility(View.VISIBLE);
                                imgVideo.setImageBitmap(bitmap2);

                                videoBean = new VideoBean();
                                videoBean.setSize(size);
                                videoBean.setVideoName(title);
                                videoBean.setVideoUrl(videoPath);
                                videoBean.setVideoImage(savePath);
                            } else {
                                showToast("请选择视频文件");
                            }
                        }
                    }
                } else {
                    videoBean = null;
                }
            }

        }
    }

    private void videoIsexit() {
        videoBean.setShowTitle(getText(etVideoShart));
        videoBean.setSaveTime(System.currentTimeMillis());
        if (saveLocalId == -1) {
            mVideoDBManager.insert(videoBean);
        } else {
            videoBean.setId(saveLocalId);
            mVideoDBManager.update(videoBean);
        }

        mVoiceDBManager = new VoiceDBManager();
        mNavigationDBManager = new NavigationDBManager();

        updateContents();
    }


    private boolean isEmpty(TextView textView) {
        return textView.getText().toString().trim().equals("") || textView.getText().toString().trim().equals("");
    }

    private String getText(TextView textView) {
        return textView.getText().toString().trim();
    }


    /**
     * 更新所有
     */
    public void updateContents() {
        if (mVideoDBManager == null || mVoiceDBManager == null || mNavigationDBManager == null) {
            throw new NullPointerException("local loxicon unll");
        }
        StringBuilder lexiconContents = new StringBuilder();
        //本地语音
        List<VoiceBean> voiceBeanList = mVoiceDBManager.loadAll();
        for (VoiceBean voiceBean : voiceBeanList) {
            lexiconContents.append(voiceBean.getShowTitle()).append("\n");
        }
        //本地视频
        List<VideoBean> videoBeanList = mVideoDBManager.loadAll();
        for (VideoBean videoBean : videoBeanList) {
            lexiconContents.append(videoBean.getShowTitle()).append("\n");
        }
        //本地导航
        List<NavigationBean> navigationBeanList = mNavigationDBManager.loadAll();
        for (NavigationBean navigationBean : navigationBeanList) {
            lexiconContents.append(navigationBean.getTitle()).append("\n");
        }

        lexiconContents.append(AppUtil.words2Contents());
        updateLocation(lexiconContents.toString());
    }

    public void updateLocation(String lexiconContents) {
        mIat = SpeakIat.getInstance().mIat();
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 指定资源路径
        mIat.setParameter(ResourceUtil.ASR_RES_PATH,
                ResourceUtil.generateResourcePath(NovelApp.getInstance().getApplicationContext(),
                        ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        // 指定语法路径
        mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
        // 指定语法名字
        mIat.setParameter(SpeechConstant.GRAMMAR_LIST, "local");
        // 设置文本编码格式
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // lexiconName 为词典名字，lexiconContents 为词典内容，lexiconListener 为回调监听器
        int ret = mIat.updateLexicon("voice", lexiconContents, listener);
        if (ret != ErrorCode.SUCCESS) {
            Print.e("更新词典失败,错误码：" + ret);
        }
    }

    private LexiconListener listener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error == null) {
                Print.e("词典更新成功");
                onLexiconSuccess();
            } else {
                Print.e("词典更新失败,错误码：" + error.getErrorCode());
                onLexiconError(error.getErrorDescription());
            }
        }
    };

    public void onLexiconSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    public void onLexiconError(String error) {
        showToast(error);
    }
}
