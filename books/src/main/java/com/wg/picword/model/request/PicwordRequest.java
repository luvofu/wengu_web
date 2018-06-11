package com.wg.picword.model.request;

import com.wg.common.Enum.common.Permission;

/**
 * Created by Administrator on 2016/12/23 0023.
 */
public class PicwordRequest {
    private String token;
    private long bookId = -1;
    private long picwordId = -1;
    private int page = 0;
    private int permission = Permission.Open.getType();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public long getPicwordId() {
        return picwordId;
    }

    public void setPicwordId(long picwordId) {
        this.picwordId = picwordId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}
