package com.fanfan.novel.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.model.SiteBean;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/2/23.
 */

public class SiteDataAdapter extends BaseQuickAdapter<SiteBean, BaseViewHolder> {

    public SiteDataAdapter(@Nullable List<SiteBean> data) {
        super(R.layout.item_site_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SiteBean item) {
        if (item.getName().isEmpty())
            return;
        helper.setText(R.id.name, item.getName());
        ImageView icon = helper.getView(R.id.icon);
        if (item.getAvatar_url() != null) {
            Glide.with(mContext).load(item.getAvatar_url())
//                    .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.ic_logo))
                    .into(icon);
        } else {
            icon.setVisibility(View.GONE);
        }
    }


}
