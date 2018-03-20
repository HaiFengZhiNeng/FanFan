package com.fanfan.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.novel.activity.DataNavigationActivity;
import com.fanfan.novel.activity.DataSiteActivity;
import com.fanfan.novel.activity.DataVideoActivity;
import com.fanfan.novel.activity.DataVoiceActivity;
import com.fanfan.novel.activity.FaceDataActivity;
import com.fanfan.novel.activity.SelectCtiyActivity;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.robot.R;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.fragment.ImportFragment;
import com.fanfan.robot.fragment.XfFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/6.
 */

public class SettingActivity extends BarBaseActivity {

    public static final int LOGOUT_TO_MAIN_REQUESTCODE = 102;
    public static final int LOGOUT_TO_MAIN_RESULTCODE = 202;

    @BindView(R.id.add_video)
    RelativeLayout addVideo;
    @BindView(R.id.add_voice)
    RelativeLayout addVoice;
    @BindView(R.id.add_navigation)
    RelativeLayout addNavigation;
    @BindView(R.id.add_site)
    RelativeLayout addSite;
    @BindView(R.id.import_layout)
    RelativeLayout importLayout;
    @BindView(R.id.rl_face)
    RelativeLayout rlFace;
    @BindView(R.id.rl_dance)
    RelativeLayout rlDance;
    @BindView(R.id.tv_xf)
    TextView tvXf;
    @BindView(R.id.logout)
    TextView logout;
    @BindView(R.id.rl_city)
    RelativeLayout rlCity;
    @BindView(R.id.tv_city)
    TextView tvCity;


    private MaterialDialog materialDialog;

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {

        tvCity.setText(RobotInfo.getInstance().getCityName());
    }


    @OnClick({R.id.add_video, R.id.add_voice, R.id.add_navigation, R.id.add_site, R.id.import_layout,
            R.id.rl_face, R.id.rl_dance, R.id.tv_xf, R.id.logout, R.id.rl_city})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_video:
                DataVideoActivity.newInstance(this);
                break;
            case R.id.add_voice:
                DataVoiceActivity.newInstance(this);
                break;
            case R.id.add_navigation:
                DataNavigationActivity.newInstance(this);
                break;
            case R.id.add_site:
                DataSiteActivity.newInstance(this);
                break;
            case R.id.import_layout:
                showLoading();
                ImportFragment importFragment = ImportFragment.newInstance();
                importFragment.show(getSupportFragmentManager(), "IMPORT");
                break;
            case R.id.rl_face:
                FaceDataActivity.newInstance(this);
                break;
            case R.id.rl_dance:
                DanceAddActivity.newInstance(this);
                break;
            case R.id.tv_xf:
                XfFragment xfFragment = XfFragment.newInstance();
                xfFragment.show(getSupportFragmentManager(), "CHANNEL");
                break;
            case R.id.logout:
                setResult(LOGOUT_TO_MAIN_RESULTCODE);
                finish();
                break;
            case R.id.rl_city:
                SelectCtiyActivity.newInstance(this);
                break;
        }
    }

    public void showLoading() {
        if (materialDialog == null) {
            materialDialog = new MaterialDialog.Builder(this)
                    .title("请稍等...")
                    .content("正在获取中...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .build();
        }
        materialDialog.show();
    }

    public void dismissLoading() {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
            materialDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectCtiyActivity.CITY_REQUEST_CODE) {
            if (resultCode == SelectCtiyActivity.CITY_RESULT_CODE) {
                String city = data.getStringExtra(SelectCtiyActivity.RESULT_CODE);
                tvCity.setText(city);
                RobotInfo.getInstance().setCityName(city);
            }
        }
    }
}
