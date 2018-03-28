package com.fanfan.youtu.api.hfrobot.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.hfrobot.bean.RobotMsg;

/**
 * Created by Administrator on 2018/3/28/028.
 */

public class UploadProblemEvent extends BaseEvent<RobotMsg> {
    public UploadProblemEvent(@Nullable String uuid) {
        super(uuid);
    }

    public UploadProblemEvent(@Nullable String uuid, @NonNull Integer code, @Nullable RobotMsg robotMsg) {
        super(uuid, code, robotMsg);
    }
}
