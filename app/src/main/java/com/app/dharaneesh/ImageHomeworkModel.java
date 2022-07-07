package com.app.dharaneesh;

public class ImageHomeworkModel {
    private String Urls;
    private String fileName;

    public ImageHomeworkModel(String urls, String fileName) {
        Urls = urls;
        this.fileName = fileName;
    }


    public String getUrls() {
        return Urls;
    }

    public void setUrls(String urls) {
        Urls = urls;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
