package com.fanfan.novel.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.model.SiteBean;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/2/23.
 */

public class SiteDataAdapter extends SimpleAdapter<SiteBean> {


    public SiteDataAdapter(Context context, List<SiteBean> datas) {
        super(context, R.layout.item_site_data, datas);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, SiteBean item, int pos) {
        if (item.getName().isEmpty())
            return;
        viewHolder.getTextView(R.id.name).setText(item.getName());
        ImageView icon = viewHolder.getImageView(R.id.icon);
        Glide.with(context).load(item.getAvatar_url())
//                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.ic_logo))
                .into(icon);
    }
}
