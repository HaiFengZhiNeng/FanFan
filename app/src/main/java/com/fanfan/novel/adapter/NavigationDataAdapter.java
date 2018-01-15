package com.fanfan.novel.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;
import com.fanfan.youtu.api.base.Constant;

import java.io.File;
import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class NavigationDataAdapter extends SimpleAdapter<String> {


    public NavigationDataAdapter(Context context, List<String> datas) {
        super(context, R.layout.item_navigation_data, datas);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, String item, int pos) {
        String title = null;
        if (item.indexOf(".") > 0) {
            title = item.substring(0, item.indexOf("."));
        }
        viewHolder.getTextView(R.id.tv_navigation_title).setText(title);
        ImageView imageView = viewHolder.getImageView(R.id.iv_navigation_image);

        RequestOptions requestOptions = new RequestOptions()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(context)
                .asBitmap()
                .load(Constants.ASSEST_PATH + item)
                .apply(requestOptions)
                .into(imageView);

    }

}
