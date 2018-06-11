package com.wg.userbook.model.response;

import com.wg.common.Enum.common.Permission;
import com.wg.common.Enum.userbook.CategoryGroupType;
import com.wg.common.Enum.userbook.OtherCategory;
import com.wg.common.Enum.userbook.ReadStatus;
import com.wg.user.domain.UserInfo;
import com.wg.userbook.model.CategoryGroup;
import com.wg.userbook.model.CategoryStatis;
import com.wg.userbook.utils.UserBookUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.userBookDao;
import static com.wg.common.utils.dbutils.DaoUtils.userInfoDao;

/**
 * Created by Administrator on 2016/11/11 0011.
 */
public class UserBookCategoryStatisExResponse {
    private List<CategoryGroup> categoryGroupList = new ArrayList<CategoryGroup>();

    public UserBookCategoryStatisExResponse(long userId, int permission) {
        //add normal statis
        this.categoryGroupList.add(new CategoryGroup(CategoryGroupType.Normal.getType(), CategoryGroupType.Normal.getName(), UserBookUtils.getNormalCategoryStatis(userId)));
        //add custom statis
        this.categoryGroupList.add(new CategoryGroup(CategoryGroupType.Custom.getType(), CategoryGroupType.Custom.getName(), UserBookUtils.getCustomCategoryStatis(userId)));
        //add other statis
        List<CategoryStatis> categoryStatisList = new ArrayList<CategoryStatis>();
        if (permission == Permission.Personal.getType()) {
            categoryStatisList.add(new CategoryStatis(OtherCategory.Personal.getFilter(), userBookDao.countByUserIdAndPermission(userId, Permission.Personal.getType())));
            categoryStatisList.add(new CategoryStatis(OtherCategory.Reading.getFilter(), userBookDao.countByUserIdAndReadStatus(userId, ReadStatus.Reading.getType())));
            categoryStatisList.add(new CategoryStatis(OtherCategory.Not_read.getFilter(), userBookDao.countByUserIdAndReadStatus(userId, ReadStatus.NotRead.getType())));
            categoryStatisList.add(new CategoryStatis(OtherCategory.Finish_read.getFilter(), userBookDao.countByUserIdAndReadStatus(userId, ReadStatus.Finish.getType())));
        }
        UserInfo userInfo = userInfoDao.findOne(userId);
        categoryStatisList.add(new CategoryStatis(OtherCategory.Leaseable.getFilter(), userInfo.getLeaseNum()));
        categoryStatisList.add(new CategoryStatis(OtherCategory.Saleable.getFilter(), userInfo.getSaleNum()));
        this.categoryGroupList.add(new CategoryGroup(CategoryGroupType.Other.getType(), CategoryGroupType.Other.getName(), categoryStatisList));
    }

    public List<CategoryGroup> getCategoryGroupList() {
        return categoryGroupList;
    }

    public void setCategoryGroupList(List<CategoryGroup> categoryGroupList) {
        this.categoryGroupList = categoryGroupList;
    }
}
