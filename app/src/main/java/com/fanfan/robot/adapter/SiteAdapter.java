package com.fanfan.robot.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;

import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.model.SiteBean;
import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2018/2/23.
 */

public class SiteAdapter extends SimpleAdapter<SiteBean> {

    private Handler mHandler;

    private List<Boolean> isClicks;

    public SiteAdapter(Context context, List<SiteBean> datas) {
        super(context, R.layout.item_voice_simple, datas);
        mHandler = new Handler();
        isClicks = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            isClicks.add(false);
        }
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, SiteBean item, int pos) {
        viewHolder.getTextView(R.id.tv_showtitle).setText(item.getName());
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        if (isClicks.get(position)) {
            viewHolder.getTextView(R.id.tv_showtitle).setTextColor(Color.WHITE);
            viewHolder.getCardView(R.id.card_voice).setCardBackgroundColor(context.getResources().getColor(R.color.voice_item_back));
        } else {
            viewHolder.getTextView(R.id.tv_showtitle).setTextColor(Color.BLACK);
            viewHolder.getCardView(R.id.card_voice).setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public void refreshData(List<SiteBean> list) {
        for (int i = 0; i < list.size(); i++) {
            isClicks.add(false);
        }
        super.refreshData(list);
    }

    public void notifyClick(int position) {
        for (int i = 0; i < isClicks.size(); i++) {
            isClicks.set(i, false);
        }
        isClicks.set(position, true);
        notifyDataSetChanged();
    }
}
