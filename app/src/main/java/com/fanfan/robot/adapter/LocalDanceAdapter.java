package com.fanfan.robot.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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

public class LocalDanceAdapter extends SimpleAdapter<Dance> {

    public LocalDanceAdapter(Context context, List<Dance> dances) {
        super(context, R.layout.view_holder_dance, dances);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, Dance item, int pos) {

        ImageView ivCover = viewHolder.getImageView(R.id.iv_cover);
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.default_cover_dance)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transform(new GlideRoundTransform());
        Glide.with(context)
                .load(item.getCoverPath())
                .apply(options)
                .into(ivCover);


        viewHolder.getTextView(R.id.tv_title).setText(item.getTitle());
    }
}
