package com.fanfan.youtu.api.hfrobot.bean;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class UpdateProgram extends RobotMsg {


    /**
     * UpdateProgram : {"id":"1","versionCode":"1","versionName":"1.0.2","appName":"robot.apk","updateUrl":"files/robot.apk","upgradeInfo":"修改部分bug","updateTime":"2018-03-27"}
     * code : 0
     * msg : 最新
     */

    private UpdateProgramBean UpdateProgram;

    public UpdateProgramBean getUpdateProgram() {
        return UpdateProgram;
    }

    public void setUpdateProgram(UpdateProgramBean UpdateProgram) {
        this.UpdateProgram = UpdateProgram;
    }

    public static class UpdateProgramBean {
        /**
         * id : 1
         * versionCode : 1
         * versionName : 1.0.2
         * appName : robot.apk
         * updateUrl : files/robot.apk
         * upgradeInfo : 修改部分bug
         * updateTime : 2018-03-27
         */

        private String id;
        private int versionCode;
        private String versionName;
        private String appName;
        private String updateUrl;
        private String upgradeInfo;
        private String updateTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getUpdateUrl() {
            return updateUrl;
        }

        public void setUpdateUrl(String updateUrl) {
            this.updateUrl = updateUrl;
        }

        public String getUpgradeInfo() {
            return upgradeInfo;
        }

        public void setUpgradeInfo(String upgradeInfo) {
            this.upgradeInfo = upgradeInfo;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
