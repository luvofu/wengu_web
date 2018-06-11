package com.wg.userbook.model.response;

import com.wg.book.domain.BookRemark;
import com.wg.userbook.domain.UserBook;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class UserBookDetailResponse {
    private long userBookId;
    private long bookId;
    private int getType;
    private Date getTime;
    //    纸质/电子
    private int bookType;
    //    已读/未读/正在读
    private int readStatus;
    private String getPlace;
    private String readPlace;
    private Date readTime;
    private String tag;
    private String other;
    private int permission;
    private float rating;
    private long remarkId;
    //    0:描码/1:isbn输入/2:手动输入
    private int enterType;
    private String remark;
    private boolean isLease;//可租借状态
    private boolean isSale;//可出售状态
    private double evaluation;//估价
    private double dayRentGold;//日租金
    private String bookDescription;//描述
    private Date updatedTime;

    public UserBookDetailResponse(UserBook userBook, BookRemark bookRemark) {
        this.userBookId = userBook.getUserBookId();
        this.bookId = userBook.getBookId();
        this.getType = userBook.getGetType();
        this.getTime = userBook.getGetTime();
        this.bookType = userBook.getBookType();
        this.readStatus = userBook.getReadStatus();
        this.getPlace = userBook.getGetPlace();
        this.readPlace = userBook.getReadPlace();
        this.readTime = userBook.getReadTime();
        this.tag = userBook.getTag();
        this.other = userBook.getOther();
        this.permission = userBook.getPermission();
        this.rating = userBook.getRating();
        this.remarkId = userBook.getRemarkId();
        this.isLease = userBook.isLease();
        this.isSale = userBook.isSale();
        this.evaluation = userBook.getEvaluation();
        this.dayRentGold = userBook.getDayRentGold();
        this.bookDescription = userBook.getDescription();
        this.enterType = userBook.getEnterType();
        if (bookRemark != null) {
            this.remark = bookRemark.getRemark();
        }
        this.updatedTime = userBook.getUpdatedTime();
    }

    public long getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(long userBookId) {
        this.userBookId = userBookId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getGetType() {
        return getType;
    }

    public void setGetType(int getType) {
        this.getType = getType;
    }

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public String getGetPlace() {
        return getPlace;
    }

    public void setGetPlace(String getPlace) {
        this.getPlace = getPlace;
    }

    public String getReadPlace() {
        return readPlace;
    }

    public void setReadPlace(String readPlace) {
        this.readPlace = readPlace;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(long remarkId) {
        this.remarkId = remarkId;
    }

    public int getEnterType() {
        return enterType;
    }

    public void setEnterType(int enterType) {
        this.enterType = enterType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    public double getDayRentGold() {
        return dayRentGold;
    }

    public void setDayRentGold(double dayRentGold) {
        this.dayRentGold = dayRentGold;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
