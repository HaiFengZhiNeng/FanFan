package com.fanfan.robot.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.common.glide.GlideRoundTransform;
import com.fanfan.novel.utils.music.MusicUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.model.Dance;

import java.util.List;

/**
 * Created by android on 2018/1/11.
 */

public class LocalDanceAdapter extends BaseQuickAdapter<Dance, BaseViewHolder> {

    public LocalDanceAdapter(@Nullable List<Dance> data) {
        super(R.layout.view_holder_dance, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Dance item) {
        ImageView ivCover = helper.getView(R.id.iv_cover);
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.default_cover_dance)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new GlideRoundTransform());
        Glide.with(mContext)
                .load(item.getCoverPath())
                .apply(options)
                .into(ivCover);

        helper.setText(R.id.tv_title, item.getTitle());
    }

}
