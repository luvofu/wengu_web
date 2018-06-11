package com.wg.userbook.model;

/**
 * Created by Administrator on 2016/10/17 0017.
 */
public class CategoryStatisEx {
    private long categoryId;
    private String category;
    private long statis;

    public CategoryStatisEx(long categoryId, String category, long statis) {
        this.categoryId = categoryId;
        this.category = category;
        this.statis = statis;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
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
