package com.wg.userbook.model.request;

import com.wg.common.Enum.userbook.CategoryGroupType;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/9/3.
 */
public class UserBookPersonalRequest {
    //user
    private String token;
    private long userId = -1;
    //page
    private int page = 0;
    //goal
    int categoryType = CategoryGroupType.All.getType();//全部、中图法、自定义、其它
    //filter
    String category;

    public boolean paramOk() {
        if (userId == -1) {
            return false;
        }
        if (categoryType == CategoryGroupType.All.getType()) {
        } else if (categoryType == CategoryGroupType.Normal.getType()
                || categoryType == CategoryGroupType.Custom.getType()
                || categoryType == CategoryGroupType.Other.getType()) {
            if (StringUtils.isBlank(category)) {
                return false;
            }
        }
        return true;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
