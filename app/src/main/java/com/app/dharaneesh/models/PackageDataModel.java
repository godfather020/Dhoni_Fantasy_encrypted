package com.app.dharaneesh.models;

public class PackageDataModel {

    String packageName, packageDetails, packagePrice, packageOfferPrice, packageValidity, packageimg;

    public PackageDataModel(){

    }

    public PackageDataModel(String packageName, String packageDetails, String packagePrice, String packageOfferPrice, String packageValidity, String packageimg) {
        this.packageName = packageName;
        this.packageDetails = packageDetails;
        this.packagePrice = packagePrice;
        this.packageOfferPrice = packageOfferPrice;
        this.packageValidity = packageValidity;
        this.packageimg = packageimg;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageDetails() {
        return packageDetails;
    }

    public void setPackageDetails(String packageDetails) {
        this.packageDetails = packageDetails;
    }

    public String getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(String packagePrice) {
        this.packagePrice = packagePrice;
    }

    public String getPackageOfferPrice() {
        return packageOfferPrice;
    }

    public void setPackageOfferPrice(String packageOfferPrice) {
        this.packageOfferPrice = packageOfferPrice;
    }

    public String getPackageValidity() {
        return packageValidity;
    }

    public void setPackageValidity(String packageValidity) {
        this.packageValidity = packageValidity;
    }

    public String getPackageimg() {
        return packageimg;
    }

    public void setPackageimg(String packageimg) {
        this.packageimg = packageimg;
    }
}
