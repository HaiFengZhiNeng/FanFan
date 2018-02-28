package com.fanfan.novel.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.model.VideoBean;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class VideoDataAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {

    public VideoDataAdapter(@Nullable List<VideoBean> data) {
        super(R.layout.item_video_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoBean item) {
        helper.setText(R.id.show_title, item.getShowTitle());
        helper.setText(R.id.save_time, TimeUtils.getShortTime(item.getSaveTime()));
        helper.setText(R.id.tv_video_url, item.getVideoUrl());
        Glide.with(mContext).load(item.getVideoImage())
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.ic_logo))
                .into((ImageView) helper.getView(R.id.iv_video_image));
    }

}
