package com.fanfan.novel.common;

import com.fanfan.novel.service.udp.SocketManager;
import com.fanfan.robot.app.NovelApp;
import com.fanfan.novel.utils.FileUtil;

import java.io.File;

/**
 * Created by android on 2017/12/18.
 */

public class Constants {

    public static int displayWidth;
    public static int displayHeight;

    public static int IMSDK_APPID = 1400043768;

    public static int IMSDK_ACCOUNT_TYPE = 17967;

    public static String controlId = "hotel003";
    public static final String roomAVId = "@TGS#2GOCMM6EN";
    // exit
    public static final String EXIT_APP = "EXIT_APP";

    public static final String NET_LOONGGG_EXITAPP = "net.loonggg.exitapp";

    private static final String M_SDROOT_CACHE_PATH = FileUtil.getCacheDir(NovelApp.getInstance().getApplicationContext()) + File.separator;

    private static final String M_SDROOT_FILE_PATH = FileUtil.getExternalFileDir(NovelApp.getInstance().getApplicationContext()) + File.separator;

    public static final String PROJECT_PATH = M_SDROOT_FILE_PATH + "fHotel" + File.separator;
    public static final String PRINT_LOG_PATH = M_SDROOT_CACHE_PATH + "print";
    public static final String PRINT_TIMLOG_PATH = M_SDROOT_CACHE_PATH + "log" + File.separator;
    public static final String CRASH_PATH = M_SDROOT_CACHE_PATH + "crash";

    public static final String GRM_PATH = PROJECT_PATH + "msc";

    public static final String IAT_CLOUD_BUILD = "iat_cloud_build";
    public static final String IAT_LOCAL_BUILD = "iat_local_build";
    public static final String IAT_CLOUD_UPDATELEXICON = "iat_cloud_updatelexicon";
    public static final String IAT_LOCAL_UPDATELEXICON = "iat_local_updatelexicon";

    public static final String QUERYLANAGE = "query_lanage";

    public static final String IAT_LINE_LANGUAGE = "iat_line_language";
    public static final String IAT_LOCAL_LANGUAGE = "iat_local_language";

    public static final String IAT_LINE_LANGUAGE_TALKER = "iat_line_language_talker";
    public static final String IAT_LOCAL_LANGUAGE_TALKER = "iat_local_language_talker";
    public static final String IAT_LINE_LANGUAGE_HEAR = "iat_line_language_hear";
    public static final String IAT_LOCAL_LANGUAGE_HEAR = "iat_local_language_hear";

    public static final String IS_INITIALIZATION = "is_initialization";

    public static final String LINE_SPEED = "line_speed";

    //udp
    public static String IP;
    public static int PORT = SocketManager.DEFAULT_UDPSERVER_PORT;
    public static String CONNECT_IP = null;

    public static int CONNECT_PORT = 0;

    //视频
    public static boolean isCalling;

    public static final String ASSEST_PATH = "file:///android_asset/";
    public static final String[] NAVIGATIONS = {"image_navigation"};

    public static final String MUSIC_UPDATE = "music_update";
}
