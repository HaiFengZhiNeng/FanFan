package com.fanfan.youtu.api.hfrobot.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class DownLoadEvent extends BaseEvent<ResponseBody> {
    public DownLoadEvent(@Nullable String uuid) {
        super(uuid);
    }

    public DownLoadEvent(@Nullable String uuid, @NonNull Integer code, @Nullable ResponseBody responseBody) {
        super(uuid, code, responseBody);
    }
}
