package com.fanfan.youtu.api.base.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fanfan.novel.common.Constants;
import com.fanfan.youtu.api.base.Constant;
import com.fanfan.youtu.token.YoutuSign;
import com.seabreeze.log.Print;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by android on 2017/12/21.
 */

public class BaseImpl<Service> {

    private static Retrofit mRetrofit;
    protected Service mService;

    public BaseImpl(@NonNull Context context) {

        initRetrofit();
        this.mService = mRetrofit.create(getServiceClass());
    }


    private void initRetrofit() {
        if (null != mRetrofit)
            return;

        YoutuSign.init();

        // 设置 Log 拦截器，可以用于以后处理一些异常情况
        HttpLoggingInterceptor.Logger logger = new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Print.e(message);
//                try {
//                    String text = URLDecoder.decode(message, "utf-8");
//                    Print.e(text);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    Print.e(message);
//                }
            }
        };
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(logger);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        File cacheFile = new File(Constants.PRINT_TIMLOG_PATH, "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        // 为所有请求自动添加 token
        Interceptor mTokenInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                // 如果当前没有缓存 token 或者请求已经附带 token 了，就不再添加
                String accessToken = YoutuSign.getSingleInstance().getAccessToken();
                Request authorised = originalRequest.newBuilder()
                        .header("Authorization", accessToken)
                        .build();
                return chain.proceed(authorised);
            }
        };

        // 自动刷新 token
        Authenticator mAuthenticator = new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) {
                String accessToken = YoutuSign.getSingleInstance().getAccessToken();
                return response.request().newBuilder()
                        .header("Authorization", accessToken)
                        .build();
            }
        };

        // 配置 client
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)                // 设置拦截器
                .retryOnConnectionFailure(true)             // 是否重试
                .connectTimeout(5, TimeUnit.SECONDS)        // 连接超时事件
                .readTimeout(5, TimeUnit.SECONDS)           // 读取超时时间
                .addNetworkInterceptor(mTokenInterceptor)   // 自动附加 token
                .addNetworkInterceptor(new StethoInterceptor())
//                .authenticator(mAuthenticator)              // 认证失败自动刷新token
                .cache(cache)
                .build();

        // 配置 Retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.API_YOUTU_BASE)                         // 设置 base url
                .client(client)                                     // 设置 client
                .addConverterFactory(GsonConverterFactory.create()) // 设置 Json 转换工具
                .build();
    }

    private Class<Service> getServiceClass() {
        return (Class<Service>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}
