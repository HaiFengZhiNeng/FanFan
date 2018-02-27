package com.fanfan.novel.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.common.presenter.BaseView;
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;
import com.fanfan.youtu.api.base.Constant;

import java.io.File;
import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class NavigationDataAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public NavigationDataAdapter(@Nullable List<String> data) {
        super(R.layout.item_navigation_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        String title = null;
        if (item.indexOf(".") > 0) {
            title = item.substring(0, item.indexOf("."));
        }
        helper.setText(R.id.tv_navigation_title, title);
        ImageView imageView = helper.getView(R.id.iv_navigation_image);

        RequestOptions requestOptions = new RequestOptions()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext)
                .asBitmap()
                .load(Constants.ASSEST_PATH + item)
                .apply(requestOptions)
                .into(imageView);
    }


}
