package com.fanfan.robot.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.common.base.BaseDialogFragment;
import com.fanfan.novel.db.manager.VoiceDBManager;
import com.fanfan.novel.model.VoiceBean;
import com.fanfan.novel.ui.MyScrollView;
import com.fanfan.novel.ui.PinchImageView;
import com.fanfan.robot.R;
import com.fanfan.robot.activity.ProblemConsultingActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/3/1.
 */

public class ImageFragment extends BaseDialogFragment {

    @BindView(R.id.tv_titlebar_name)
    TextView mTvTitlebarName;
    @BindView(R.id.rl_top)
    RelativeLayout mRlTop;
    @BindView(R.id.tv_info)
    TextView mTvInfo;
    @BindView(R.id.scrollview)
    MyScrollView mScrollview;
    @BindView(R.id.relativeLayout)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.pinch_image_view)
    PinchImageView mPinchImageView;

    public static final String IMAGE_ID = "image_id";

    public static ImageFragment newInstance(long id) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(IMAGE_ID, id);
        imageFragment.setArguments(bundle);
        return imageFragment;
    }

    private VoiceDBManager mVoiceDBManager;
    private VoiceBean mBean;

    private boolean isShow = true;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_image;
    }

    @Override
    protected void initData() {
        long id = getArguments().getLong(IMAGE_ID, -1);
        if (id == -1) {
            return;
        }
        mVoiceDBManager = new VoiceDBManager();
        mBean = mVoiceDBManager.selectByPrimaryKey(id);
        if(mBean == null){
            return;
        }

        mRelativeLayout.getBackground().setAlpha(255);
        mScrollview.getBackground().mutate().setAlpha(100);
        mRlTop.getBackground().mutate().setAlpha(100);

        mTvTitlebarName.setText(mBean.getShowTitle());
        mTvInfo.setText(mBean.getVoiceAnswer());

        if(mBean.getImgUrl() != null) {
            Glide.with(mContext).load(mBean.getImgUrl())
                    .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.video_image))
                    .into(mPinchImageView);
        }
        mPinchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    isShow = false;
                    setView(mRlTop, false);
                    setView(mScrollview, false);
                } else {
                    isShow = true;
                    setView(mRlTop, true);
                    setView(mScrollview, true);
                }
            }
        });
    }

    @Override
    protected void setListener(View rootView) {

    }

    @Override
    public void onDestroyView() {
        ((ProblemConsultingActivity)getActivity()).isShow(false);
        super.onDestroyView();
    }

    private void setView(final View view, final boolean isShow) {
        AlphaAnimation alphaAnimation;
        if (isShow) {
            alphaAnimation = new AlphaAnimation(0, 1);
        } else {
            alphaAnimation = new AlphaAnimation(1, 0);
        }
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(500);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
