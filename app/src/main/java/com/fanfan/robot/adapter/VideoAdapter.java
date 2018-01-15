package com.fanfan.robot.adapter;

import android.content.Context;

import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.model.VideoBean;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class VideoAdapter extends SimpleAdapter<VideoBean> {

    public VideoAdapter(Context context, List<VideoBean> datas) {
        super(context, R.layout.item_video_simple, datas);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, VideoBean item, int pos) {
        viewHolder.getTextView(R.id.tv_name).setText(item.getShowTitle());
    }
}
