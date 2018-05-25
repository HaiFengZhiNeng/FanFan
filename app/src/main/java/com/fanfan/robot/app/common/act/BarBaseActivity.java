package com.fanfan.robot.app.common.act;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.robot.R;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.utils.ErrorMsg;
import com.seabreeze.log.Print;

import java.util.Random;

/**
 * Created by zhangyuanyuan on 2017/12/15.
 */

public abstract class BarBaseActivity extends IMBaseActivity {

    @Override
    protected int setBackgroundGlide() {
        return 0;
    }

    @Override
    protected boolean setResult() {
        return false;
    }

    public void onError(int code, String msg) {
        Print.e("onError  code : " + code + " ; msg : " + msg + " ; describe : " + ErrorMsg.getCodeDescribe(code));
    }

    public void onError(BaseEvent event) {
        Print.e("onError : " + event.getCode() + "  " + event.getCodeDescribe());
    }

    public String resFoFinal(int id) {
        String[] arrResult = getResources().getStringArray(id);
        return arrResult[new Random().nextInt(arrResult.length)];
    }

    public int resFoInter(String[] res) {
        return new Random().nextInt(res.length);
    }

    protected void stopAll() {

    }

    protected void isEmpty() {
        RelativeLayout rlLayout = findViewById(R.id.rl_empty);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        if (rlLayout != null && tvEmpty != null) {
            rlLayout.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    protected void isNuEmpty() {
        RelativeLayout rlLayout = findViewById(R.id.rl_empty);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        if (rlLayout != null && tvEmpty != null) {
            rlLayout.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
