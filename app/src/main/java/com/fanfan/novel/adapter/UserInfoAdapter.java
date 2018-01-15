package com.fanfan.novel.adapter;

import android.content.Context;

import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.model.FaceAuth;
import com.fanfan.novel.utils.PreferencesUtils;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/9/30.
 */

public class UserInfoAdapter extends SimpleAdapter<FaceAuth> {

    public UserInfoAdapter(Context context, List<FaceAuth> faceAuths) {
        super(context, R.layout.item_person, faceAuths);
    }


    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, FaceAuth item, int pos) {
        if (item.getAuthId() != null) {

            viewHolder.getTextView(R.id.tv_info_id).setText(item.getAuthId());
        } else {
            viewHolder.getTextView(R.id.tv_info_id).setText("数据丢失，点击获取");
        }
    }
}
