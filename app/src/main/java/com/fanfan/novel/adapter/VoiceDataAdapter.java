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
import com.fanfan.novel.model.VoiceBean;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;

import java.util.List;

import static com.tencent.qalsdk.service.QalService.context;

/**
 * Created by android on 2018/1/6.
 */

public class VoiceDataAdapter extends BaseQuickAdapter<VoiceBean, BaseViewHolder> {

    public VoiceDataAdapter(@Nullable List<VoiceBean> data) {
        super(R.layout.item_voice_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VoiceBean item) {
        helper.setText(R.id.show_title, item.getShowTitle());
        helper.setText(R.id.save_time, TimeUtils.getShortTime(item.getSaveTime()));
        helper.setText(R.id.tv_expression, item.getExpression());
        helper.setText(R.id.tv_action, item.getAction());
        Glide.with(mContext).load(item.getImgUrl())
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.ic_logo))
                .into((ImageView) helper.getView(R.id.iv_voice_image));
    }
}
