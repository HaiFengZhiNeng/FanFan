package com.fanfan.youtu.api.hfrobot.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.youtu.api.base.callback.BaseCallback;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.base.impl.BaseImpl;
import com.fanfan.youtu.api.hfrobot.event.CheckEvent;
import com.fanfan.youtu.api.hfrobot.event.RequestProblemEvent;
import com.fanfan.youtu.utils.UUIDGenerator;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class RobotImpl extends BaseImpl<RobotService> implements RobotAPI {
    public RobotImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    public String updateProgram(int type) {
        String uuid = UUIDGenerator.getUUID();
        mService.updateProgram(type)
                .enqueue(new BaseCallback<>(new CheckEvent(uuid)));
        return uuid;
    }

    @Override
    public String downloadFileWithDynamicUrlSync(String fileUrl) {
        String uuid = UUIDGenerator.getUUID();
        mService.downloadFileWithDynamicUrlSync(fileUrl)
                .enqueue(new BaseCallback<>(new BaseEvent<ResponseBody>(uuid)));
        return uuid;
    }

    @Override
    public String requestProblem(String identifier, String problem, int id) {
        String uuid = UUIDGenerator.getUUID();
        mService.requestProblem(identifier, problem, id)
                .enqueue(new BaseCallback<>(new RequestProblemEvent(uuid)));
        return uuid;
    }
}
