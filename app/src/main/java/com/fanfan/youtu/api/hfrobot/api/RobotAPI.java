package com.fanfan.youtu.api.hfrobot.api;

import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public interface RobotAPI {

    String updateProgram();

    String downloadFileWithFixedUrl();

    String downloadFileWithDynamicUrlSync(@Url String fileUrl);

    String uploadProblem(@Query("identifier") String identifier, @Query("problem") String problem);
}
