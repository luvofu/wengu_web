package com.wg.common.model.request;

/**
 * Created by Administrator on 2016/9/7.
 */
public class CommonRequest {
    //user
    String token;
    //good
    int goodType;
    long goodObjId;
    //share
    int shareType;
    long shareObjId;
    String content;
    String location;
    //logo
    String logo;
    //editimg
    int imageType;
    long imageObjId = -1;
    //delete
    int deleteType = -1;
    long deleteObjId = -1;

    long updatedTime;

    int exportType = -1;
    long id = -1;
    private int categoryType;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getGoodType() {
        return goodType;
    }

    public void setGoodType(int goodType) {
        this.goodType = goodType;
    }

    public long getGoodObjId() {
        return goodObjId;
    }

    public void setGoodObjId(long goodObjId) {
        this.goodObjId = goodObjId;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public long getShareObjId() {
        return shareObjId;
    }

    public void setShareObjId(long shareObjId) {
        this.shareObjId = shareObjId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public long getImageObjId() {
        return imageObjId;
    }

    public void setImageObjId(long imageObjId) {
        this.imageObjId = imageObjId;
    }

    public int getDeleteType() {
        return deleteType;
    }

    public void setDeleteType(int deleteType) {
        this.deleteType = deleteType;
    }

    public long getDeleteObjId() {
        return deleteObjId;
    }

    public void setDeleteObjId(long deleteObjId) {
        this.deleteObjId = deleteObjId;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getExportType() {
        return exportType;
    }

    public void setExportType(int exportType) {
        this.exportType = exportType;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }
}
