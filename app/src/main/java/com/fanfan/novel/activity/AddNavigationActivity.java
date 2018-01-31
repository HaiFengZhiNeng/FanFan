package com.fanfan.novel.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.instance.SpeakIat;
import com.fanfan.novel.db.manager.NavigationDBManager;
import com.fanfan.novel.db.manager.VideoDBManager;
import com.fanfan.novel.db.manager.VoiceDBManager;
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.model.VideoBean;
import com.fanfan.novel.model.VoiceBean;
import com.fanfan.novel.ui.RangeClickImageView;
import com.fanfan.novel.utils.AppUtil;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/8.
 */

public class AddNavigationActivity extends BarBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_guide)
    EditText etGuide;
    @BindView(R.id.et_datail)
    EditText etDatail;
    @BindView(R.id.tv_navigation)
    TextView tvNavigation;

    public static final String NAVIGATION_TITLE = "navigation_title";
    public static final String NAVIGATION_ID = "navigation_id";
    public static final int ADD_NAVIGATION_REQUESTCODE = 223;
    public static final String RESULT_CODE = "navigation_title_result";

    public static void newInstance(Activity context, String title, int requestCode) {
        Intent intent = new Intent(context, AddNavigationActivity.class);
        intent.putExtra(NAVIGATION_TITLE, title);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void newInstance(Activity context, long id, int requestCode) {
        Intent intent = new Intent(context, AddNavigationActivity.class);
        intent.putExtra(NAVIGATION_ID, id);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private long saveLocalId;

    private NavigationDBManager mNavigationDBManager;

    private NavigationBean navigationBean;

    private int curNavigation;

    private boolean isClick;

    private SpeechRecognizer mIat;
    private VideoDBManager mVideoDBManager;
    private VoiceDBManager mVoiceDBManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_navigation;
    }

    @Override
    protected void initData() {
        saveLocalId = getIntent().getLongExtra(NAVIGATION_ID, -1);
        String title = getIntent().getStringExtra(NAVIGATION_TITLE);

        mNavigationDBManager = new NavigationDBManager();

        if (saveLocalId != -1) {

            navigationBean = mNavigationDBManager.selectByPrimaryKey(saveLocalId);
            etGuide.setText(navigationBean.getGuide());
            etDatail.setText(navigationBean.getDatail());
            curNavigation = valueForArray(R.array.navigation, navigationBean.getNavigation());
        }

        tvTitle.setText(title == null ? navigationBean.getTitle() : title);
        tvNavigation.setText(resArray(R.array.navigation)[curNavigation]);
    }

    @Override
    protected void onDestroy() {
        mIat.destroy();
        listener = null;
        super.onDestroy();
    }

    @OnClick({R.id.tv_navigation})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_navigation:
                DialogUtils.showLongListDialog(AddNavigationActivity.this, "目的地", R.array.navigation, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        curNavigation = position;
                        tvNavigation.setText(text);
                    }
                });
                break;
        }

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
                if (isClick) {
                    break;
                }
                isClick = true;
                if (isEmpty(etDatail)) {
                    showToast("地点详情不能为空!");
                    break;
                }
                if (isEmpty(etGuide)) {
                    showToast("引导语不能为空!");
                    break;
                }
                navigationIsexit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigationIsexit() {
        if (navigationBean == null) {
            navigationBean = new NavigationBean();
        }
        navigationBean.setSaveTime(System.currentTimeMillis());
        navigationBean.setTitle(getText(tvTitle));
        navigationBean.setGuide(getText(etGuide));
        navigationBean.setDatail(getText(etDatail));
        navigationBean.setNavigation(resArray(R.array.navigation)[curNavigation]);
        navigationBean.setNavigationData(resArray(R.array.navigation_data)[curNavigation]);

        if (saveLocalId == -1) {//直接添加
            mNavigationDBManager.insert(navigationBean);
        } else {//更新
            navigationBean.setId(saveLocalId);
            mNavigationDBManager.update(navigationBean);
        }

        mVideoDBManager = new VideoDBManager();
        mVoiceDBManager = new VoiceDBManager();

        updateContents();
    }


    private int valueForArray(int resId, String compare) {
        String[] arrays = resArray(resId);
        return Arrays.binarySearch(arrays, compare);
    }

    private boolean isEmpty(TextView textView) {
        return textView.getText().toString().trim().equals("") || textView.getText().toString().trim().equals("");
    }

    private String[] resArray(int resId) {
        return getResources().getStringArray(resId);
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
        Intent intent = new Intent();
        intent.putExtra(RESULT_CODE, getText(tvTitle));
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onLexiconError(String error) {
        showToast(error);
    }
}
