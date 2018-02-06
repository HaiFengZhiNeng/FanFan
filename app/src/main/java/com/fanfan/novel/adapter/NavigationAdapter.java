package com.fanfan.novel.adapter;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/2/6.
 */

public class NavigationAdapter extends SimpleAdapter<NavigationBean> {


    public NavigationAdapter(Context context, List<NavigationBean> datas) {
        super(context, R.layout.item_navigation_data1, datas);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, NavigationBean item, int pos) {
        viewHolder.getTextView(R.id.show_title).setText(item.getTitle());
        viewHolder.getTextView(R.id.save_time).setText(TimeUtils.getShortTime(item.getSaveTime()));
        viewHolder.getTextView(R.id.tv_guide).setText(item.getGuide());
        viewHolder.getTextView(R.id.tv_datail).setText(item.getDatail());
        Glide.with(context).load(item.getImgUrl())
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE).error(R.mipmap.ic_logo))
                .into(viewHolder.getImageView(R.id.iv_navigation_image));
    }
}
