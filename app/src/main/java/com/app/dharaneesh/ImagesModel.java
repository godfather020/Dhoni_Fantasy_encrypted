package com.app.dharaneesh;

import android.net.Uri;

public class ImagesModel {
    Uri imageUrl;
    String imageName;


    public ImagesModel() {
    }

    public ImagesModel( String imageName,Uri imageUrl) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
