package com.wg.community.model.response;

import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.community.domain.CommBook;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;
import com.wg.userbook.domain.UserBook;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class CommunityBookerResponse {
    private long userId;
    private String nickname;
    private String avatar;
    private double longitude;
    private double latitude;
    private long userBookId;
    private double dayRentGold;
    private double evaluation;
    private String bookDes;
    private boolean isLease;
    private boolean isSale;

    public CommunityBookerResponse(CommBook commBook) {
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(commBook.getUserId());
        this.userId = userInfo.getUserId();
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.longitude = userInfo.getLongitude();
        this.latitude = userInfo.getLatitude();
        UserBook userBook = DaoUtils.userBookDao.findByBookIdAndUserId(commBook.getBookId(), commBook.getUserId());
        this.userBookId = userBook.getUserBookId();
        this.dayRentGold = userBook.getDayRentGold();
        this.evaluation = userBook.getEvaluation();
        this.bookDes = userBook.getDescription();
        this.isLease = userBook.isLease();
        this.isSale = userBook.isSale();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(long userBookId) {
        this.userBookId = userBookId;
    }

    public double getDayRentGold() {
        return dayRentGold;
    }

    public void setDayRentGold(double dayRentGold) {
        this.dayRentGold = dayRentGold;
    }

    public double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    public String getBookDes() {
        return bookDes;
    }

    public void setBookDes(String bookDes) {
        this.bookDes = bookDes;
    }

    public boolean isLease() {
        return isLease;
    }

    public void setLease(boolean lease) {
        isLease = lease;
    }

    public boolean isSale() {
        return isSale;
    }

    public void setSale(boolean sale) {
        isSale = sale;
    }
}
