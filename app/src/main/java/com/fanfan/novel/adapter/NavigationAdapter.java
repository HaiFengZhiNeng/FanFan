package com.fanfan.novel.adapter;

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
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/2/6.
 */

public class NavigationAdapter extends BaseQuickAdapter<NavigationBean, BaseViewHolder> {

    public NavigationAdapter(@Nullable List<NavigationBean> data) {
        super(R.layout.item_navigation_data1);
    }

    @Override
    protected void convert(BaseViewHolder helper, NavigationBean item) {
        helper.setText(R.id.show_title, item.getTitle());
        helper.setText(R.id.save_time, TimeUtils.getShortTime(item.getSaveTime()));
        helper.setText(R.id.tv_guide, item.getGuide());
        helper.setText(R.id.tv_datail, item.getDatail());
        Glide.with(mContext).load(item.getImgUrl())
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.ic_logo))
                .into((ImageView) helper.getView(R.id.iv_navigation_image));
    }


}
