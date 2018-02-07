package com.fanfan.novel.adapter;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.model.VideoBean;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class VideoDataAdapter extends SimpleAdapter<VideoBean> {


    public VideoDataAdapter(Context context, List<VideoBean> datas) {
        super(context, R.layout.item_video_data, datas);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, VideoBean item, int pos) {
        viewHolder.getTextView(R.id.show_title).setText(item.getShowTitle());
        viewHolder.getTextView(R.id.save_time).setText(TimeUtils.getShortTime(item.getSaveTime()));
        viewHolder.getTextView(R.id.tv_video_url).setText(item.getVideoUrl());
        Glide.with(context).load(item.getVideoImage())
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.ic_logo))
                .into(viewHolder.getImageView(R.id.iv_video_image));
    }

}
