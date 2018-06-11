package com.wg.userbook.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/17 0017.
 */
public class CategoryGroup {
    private int categoryType;
    private String categoryTypeName;
    private List<CategoryStatis> categoryStatisList = new ArrayList<CategoryStatis>();

    public CategoryGroup(int categoryType, String categoryTypeName, List<CategoryStatis> categoryStatisList) {
        this.categoryType = categoryType;
        this.categoryTypeName = categoryTypeName;
        this.categoryStatisList = categoryStatisList;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryTypeName() {
        return categoryTypeName;
    }

    public void setCategoryTypeName(String categoryTypeName) {
        this.categoryTypeName = categoryTypeName;
    }

    public List<CategoryStatis> getCategoryStatisList() {
        return categoryStatisList;
    }

    public void setCategoryStatisList(List<CategoryStatis> categoryStatisList) {
        this.categoryStatisList = categoryStatisList;
    }
}
