package com.fanfan.youtu.api.hfrobot.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.youtu.api.base.callback.BaseCallback;
import com.fanfan.youtu.api.base.impl.BaseImpl;
import com.fanfan.youtu.api.hfrobot.event.DownLoadEvent;
import com.fanfan.youtu.api.hfrobot.event.UpdateProgramEvent;
import com.fanfan.youtu.api.hfrobot.event.UploadProblemEvent;
import com.fanfan.youtu.utils.UUIDGenerator;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class RobotImpl extends BaseImpl<RobotService> implements RobotAPI {
    public RobotImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    public String updateProgram() {
        String uuid = UUIDGenerator.getUUID();
        robotService.updateProgram()
                .enqueue(new BaseCallback<>(new UpdateProgramEvent(uuid)));
        return uuid;
    }

    @Override
    public String downloadFileWithFixedUrl() {
        String uuid = UUIDGenerator.getUUID();
        robotService.downloadFileWithFixedUrl()
                .enqueue(new BaseCallback<>(new DownLoadEvent(uuid)));
        return uuid;
    }

    @Override
    public String downloadFileWithDynamicUrlSync(String fileUrl) {
        String uuid = UUIDGenerator.getUUID();
        robotService.downloadFileWithDynamicUrlSync(fileUrl)
                .enqueue(new BaseCallback<>(new DownLoadEvent(uuid)));
        return uuid;
    }

    @Override
    public String uploadProblem(String identifier, String problem) {
        String uuid = UUIDGenerator.getUUID();
        robotService.uploadProblem(identifier, problem)
                .enqueue(new BaseCallback<>(new UploadProblemEvent(uuid)));
        return uuid;
    }
}
