package com.wg.userbook.model.response;

import com.wg.userbook.model.CategoryStatis;
import com.wg.userbook.model.CategoryStatisEx;

import java.util.List;

/**
 * Created by Administrator on 2016/11/11 0011.
 */
public class UserBookCategoryStatisResponse {
    private List<CategoryStatis> normalCategoryStatisList;
    private List<CategoryStatisEx> customCategoryStatisList;

    public UserBookCategoryStatisResponse(List<CategoryStatis> normalCategoryStatisList, List<CategoryStatisEx> customCategoryStatisList) {
        this.normalCategoryStatisList = normalCategoryStatisList;
        this.customCategoryStatisList = customCategoryStatisList;
    }

    public List<CategoryStatis> getNormalCategoryStatisList() {
        return normalCategoryStatisList;
    }

    public void setNormalCategoryStatisList(List<CategoryStatis> normalCategoryStatisList) {
        this.normalCategoryStatisList = normalCategoryStatisList;
    }

    public List<CategoryStatisEx> getCustomCategoryStatisList() {
        return customCategoryStatisList;
    }

    public void setCustomCategoryStatisList(List<CategoryStatisEx> customCategoryStatisList) {
        this.customCategoryStatisList = customCategoryStatisList;
    }
}
