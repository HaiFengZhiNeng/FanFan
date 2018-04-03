package com.fanfan.youtu.api.hfrobot.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.hfrobot.bean.UploadProblem;

/**
 * Created by Administrator on 2018/3/28/028.
 */

public class UploadProblemEvent extends BaseEvent<UploadProblem> {
    public UploadProblemEvent(@Nullable String uuid) {
        super(uuid);
    }

    public UploadProblemEvent(@Nullable String uuid, @NonNull Integer code, @Nullable UploadProblem uploadProblem) {
        super(uuid, code, uploadProblem);
    }
}
