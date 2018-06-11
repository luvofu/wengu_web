package com.wg.userbook.model;

/**
 * Created by Administrator on 2016/10/17 0017.
 */
public class CategoryStatis {
    private String category;
    private long statis;

    public CategoryStatis() {
        
    }

    public CategoryStatis(String category, long statis) {
        this.category = category;
        this.statis = statis;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getStatis() {
        return statis;
    }

    public void setStatis(long statis) {
        this.statis = statis;
    }
}
