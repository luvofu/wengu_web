package com.wg.user.model.request;

/**
 * Created by Administrator on 2016/9/6.
 */
public class UserCollectionRequest {
    private long collectionId;

    private long userId=-1;
    private String token;

    private int collectType;
    private long collectObjId;

    private int page=0;

    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCollectType() {
        return collectType;
    }

    public void setCollectType(int collectType) {
        this.collectType = collectType;
    }

    public long getCollectObjId() {
        return collectObjId;
    }

    public void setCollectObjId(long collectObjId) {
        this.collectObjId = collectObjId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
