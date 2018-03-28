package com.fanfan.youtu.api.hfrobot.api;

import com.fanfan.youtu.api.hfrobot.bean.RobotMsg;
import com.fanfan.youtu.api.hfrobot.bean.UpdateProgram;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by android on 2017/12/21.
 */

public interface RobotService {


    @GET("robot/UpdateProgram.php")
    Call<UpdateProgram> updateProgram();

    @GET("files/test_102.apk")
    Call<ResponseBody> downloadFileWithFixedUrl();

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    @GET("robot/UploadProblem.php")
    Call<RobotMsg> uploadProblem(@Query("identifier") String identifier, @Query("problem") String problem);

}
