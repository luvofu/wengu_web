package com.wg.userbook.utils;

import com.wg.book.domain.Book;
import com.wg.book.utils.BookUtils;
import com.wg.common.Constant;
import com.wg.common.Enum.common.Permission;
import com.wg.common.Enum.userbook.CategoryGroupType;
import com.wg.common.Enum.userbook.OtherCategory;
import com.wg.common.Enum.userbook.ReadStatus;
import com.wg.common.model.NearbyRange;
import com.wg.common.utils.LngLatUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.community.domain.CommBook;
import com.wg.user.domain.UserInfo;
import com.wg.userbook.domain.UserBook;
import com.wg.userbook.domain.UserCategory;
import com.wg.userbook.model.CategoryStatis;
import com.wg.userbook.model.CategoryStatisEx;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/11/11 0011.
 */
public class UserBookUtils {
    public static final String NULL_CATEGORY = "未分类";
    public static final double DEFAULT_EVALUATION = 20;
    public static final double DEFAULT_DAY_RENT_GOLD = 0.2;

    //normal category statis
    public static List<CategoryStatis> getNormalCategoryStatis(long userId) {
        List<CategoryStatis> normallCategoryStatisList = new ArrayList<CategoryStatis>();
        List objectArray = userBookDao.getCategoryStatis(userId);
        CategoryStatis other = null;
        for (int index = 0; index < objectArray.size(); index++) {
            Object[] objects = (Object[]) objectArray.get(index);
            String category = (String) objects[0];
            Long statis = (Long) objects[1];
            if (!category.equals(BookUtils.NULL_CATEGORY)) {
                normallCategoryStatisList.add(new CategoryStatis(category, statis));
            } else {
                other = new CategoryStatis(category, statis);
            }
        }
        if (other != null) {
            normallCategoryStatisList.add(other);
        }
        return normallCategoryStatisList;
    }

    //custom category statis
    public static List<CategoryStatis> getCustomCategoryStatis(long userId) {
        List<CategoryStatis> customCategoryStatisList = new ArrayList<CategoryStatis>();
        for (UserCategory userCategory : userCategoryDao.findByUserIdOrderBySortAsc(userId)) {
            customCategoryStatisList.add(new CategoryStatis(userCategory.getCategory(), userCategory.getBookNum()));
        }
        return customCategoryStatisList;
    }

    //custom category statis
    public static List<CategoryStatisEx> getCustomCategoryStatisEx(long userId) {
        List<CategoryStatisEx> customCategoryStatisList = new ArrayList<CategoryStatisEx>();
        for (UserCategory userCategory : userCategoryDao.findByUserIdOrderBySortAsc(userId)) {
            customCategoryStatisList.add(new CategoryStatisEx(userCategory.getCategoryId(), userCategory.getCategory(), userCategory.getBookNum()));
        }
        return customCategoryStatisList;
    }

    //set userbook category
    public static void setUserBookCategory(UserBook userBook, String category) {
        if (userBook != null) {
            category = StringUtils.isNotBlank(category) ? category : NULL_CATEGORY;
            UserCategory preUserCategory = userCategoryDao.findOne(userBook.getCategoryId());
            UserCategory userCategory = (userCategory = userCategoryDao.findByUserIdAndCategory(userBook.getUserId(), category)) != null ?
                    userCategory : AddUtils.addUserCategory(userBook.getUserId(), category);
            userBook.setCategoryId(userCategory.getCategoryId());
            userBook = UpdateUtils.updateUserBook(userBook);
            userCategory.setBookNum(userBookDao.countByCategoryId(userCategory.getCategoryId()));
            userCategory = userCategoryDao.save(userCategory);
            if (preUserCategory != null) {
                preUserCategory.setBookNum(userBookDao.countByCategoryId(preUserCategory.getCategoryId()));
                preUserCategory = userCategoryDao.save(preUserCategory);
            }
        }
    }

    //update user all custom category statis
    public static void updateUserCategory(long userId) {
        List<UserCategory> userCategoryList = userCategoryDao.findByUserId(userId);
        if (userCategoryList != null && userCategoryList.size() > 0) {
            for (UserCategory userCategory : userCategoryList) {
                userCategory.setBookNum(userBookDao.countByCategoryId(userCategory.getCategoryId()));
            }
            userCategoryDao.save(userCategoryList);
        }
    }

    //get userbook by category
    public static Slice<UserBook> getUserBooks(long userId, int categoryType, String category, int page, int permission) {
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        Slice<UserBook> userBookSlice = null;
        if (categoryType == CategoryGroupType.All.getType()) {
            userBookSlice = userBookDao.findByUserIdAndPermissionLessThanOrderByCreatedTimeDesc(userId, permission, pageable);
        } else if (categoryType == CategoryGroupType.Normal.getType()) {
            userBookSlice = userBookDao.findByUserIdAndPermissionLessThanAndCategoryOrderByCreatedTimeDesc(userId, permission, category, pageable);
        } else if (categoryType == CategoryGroupType.Custom.getType()) {
            UserCategory userCategory = userCategoryDao.findByUserIdAndCategory(userId, category);
            long categoryId = userCategory != null ? userCategory.getCategoryId() : 0;
            userBookSlice = userBookDao.findByCategoryIdAndPermissionLessThanOrderByCreatedTimeDesc(categoryId, permission, pageable);
        } else if (categoryType == CategoryGroupType.Other.getType()) {
            if (category.equals(OtherCategory.Personal.getFilter())) {
                userBookSlice = userBookDao.findByUserIdAndPermissionOrderByCreatedTimeDesc(userId, Permission.Personal.getType(), pageable);
            } else if (category.equals(OtherCategory.Reading.getFilter())) {
                userBookSlice = userBookDao.findByUserIdAndReadStatusOrderByCreatedTimeDesc(userId, ReadStatus.Reading.getType(), pageable);
            } else if (category.equals(OtherCategory.Not_read.getFilter())) {
                userBookSlice = userBookDao.findByUserIdAndReadStatusOrderByCreatedTimeDesc(userId, ReadStatus.NotRead.getType(), pageable);
            } else if (category.equals(OtherCategory.Finish_read.getFilter())) {
                userBookSlice = userBookDao.findByUserIdAndReadStatusOrderByCreatedTimeDesc(userId, ReadStatus.Finish.getType(), pageable);
            } else if (category.equals(OtherCategory.Leaseable.getFilter())) {
                userBookSlice = userBookDao.findByUserIdAndIsLeaseAndPermissionLessThanOrderByCreatedTimeDesc(userId, true, permission, pageable);
            } else if (category.equals(OtherCategory.Saleable.getFilter())) {
                userBookSlice = userBookDao.findByUserIdAndIsSaleAndPermissionLessThanOrderByCreatedTimeDesc(userId, true, permission, pageable);
            }
        }
        return userBookSlice;
    }

    //默认藏书估值
    public static double getEvaluation(Book book) {
        double price = Utils.getDoubleNumber(book.getPrice());
        return price > 0 ? price : UserBookUtils.DEFAULT_EVALUATION;
    }

    //附近的书和人
    public static List getNearbyUserBook(double longitude, double latitude, String keyword, int page) {
        keyword = StringUtils.isNotBlank(keyword) ? keyword : "";
        NearbyRange range = LngLatUtils.getNearbyRange(longitude, latitude, LngLatUtils.NEARBY_RADIUS);
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        List objectArray = userBookDao.findNearbyUserbook(
                range.getLongitude(), range.getLatitude(), range.getFromLng(), range.getToLng(), range.getFromLat(), range.getToLat(), keyword, pageable);
        return objectArray;
    }

    //设置交易标志
    public static void setLeaseSale(UserBook userBook) {
        //updaet userinfo lease num sale num
        UserInfo userInfo = userInfoDao.findOne(userBook.getUserId());
        userInfo.setLeaseNum(userBookDao.countByUserIdAndIsLease(userBook.getUserId(), true));
        userInfo.setSaleNum(userBookDao.countByUserIdAndIsSale(userBook.getUserId(), true));
        userInfo = userInfoDao.save(userInfo);
        //update commbook
        if (!userBook.isSale() && !userBook.isLease()) {
            for (CommBook commBook : commBookDao.findByUserIdAndBookId(userBook.getUserId(), userBook.getBookId())) {
                DeleteUtils.deleteCommBook(commBook);
            }
        }
    }
}
