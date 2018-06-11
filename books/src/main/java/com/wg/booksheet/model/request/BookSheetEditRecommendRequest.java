package com.wg.booksheet.model.request;

/**
 * Created by wzhonggo on 8/31/2016.
 */
public class BookSheetEditRecommendRequest {
    private String token;
    private long sheetBookId = -1;
    private String recommend;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getSheetBookId() {
        return sheetBookId;
    }

    public void setSheetBookId(long sheetBookId) {
        this.sheetBookId = sheetBookId;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }
}
