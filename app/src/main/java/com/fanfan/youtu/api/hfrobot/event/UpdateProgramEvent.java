package com.fanfan.youtu.api.hfrobot.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.hfrobot.bean.UpdateProgram;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class UpdateProgramEvent extends BaseEvent<UpdateProgram> {
    public UpdateProgramEvent(@Nullable String uuid) {
        super(uuid);
    }

    public UpdateProgramEvent(@Nullable String uuid, @NonNull Integer code, @Nullable UpdateProgram updateProgram) {
        super(uuid, code, updateProgram);
    }
}
