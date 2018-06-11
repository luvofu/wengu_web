package com.wg.userbook.model.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/3.
 */
public class UserBookAddScanBooksRequest {
    //user
    private String token;

    //book
    private String scanBooks;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getScanBooks() {
        return scanBooks;
    }

    public void setScanBooks(String scanBooks) {
        this.scanBooks = scanBooks;
    }
}
