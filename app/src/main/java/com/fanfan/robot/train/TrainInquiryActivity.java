package com.fanfan.robot.train;

import android.app.Activity;
import android.content.Intent;

import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.robot.R;

/**
 * Created by Administrator on 2018/3/7/007.
 */

public class TrainInquiryActivity extends BarBaseActivity {

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, TrainInquiryActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initData() {

    }
}
