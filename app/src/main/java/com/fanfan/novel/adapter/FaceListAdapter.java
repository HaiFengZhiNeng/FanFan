package com.fanfan.novel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;

import java.util.List;

/**
 * Created by android on 2018/1/9.
 */

public class FaceListAdapter extends SimpleAdapter<String> {

    public FaceListAdapter(Context context, List<String> face_ids) {
        super(context, android.R.layout.simple_list_item_1, face_ids);
    }


    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, String item, int pos) {
        TextView textView = viewHolder.getTextView(android.R.id.text1);
        textView.setText(item);
    }
}