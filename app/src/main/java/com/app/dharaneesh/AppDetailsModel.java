package com.app.dharaneesh;

public class AppDetailsModel {
    String AppLink;
    String VersionName;


    public AppDetailsModel() {
    }

    public AppDetailsModel(String appLink, String versionName) {
        AppLink = appLink;
        VersionName = versionName;
    }

    public String getAppLink() {
        return AppLink;
    }

    public void setAppLink(String appLink) {
        AppLink = appLink;
    }

    public String getVersionName() {
        return VersionName;
    }

    public void setVersionName(String versionName) {
        VersionName = versionName;
    }
}
