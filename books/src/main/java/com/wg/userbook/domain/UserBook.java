package com.wg.userbook.domain;

import com.wg.book.domain.Book;
import com.wg.common.Enum.common.Permission;
import com.wg.common.utils.TimeUtils;
import com.wg.common.Enum.userbook.ReadStatus;
import com.wg.user.domain.UserInfo;
import com.wg.userbook.utils.UserBookUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//用户书籍表
@Cacheable
@Entity
@Table(name = "user_book",
        uniqueConstraints = {@UniqueConstraint(name = "ukey_uid_bid", columnNames = {"userId", "bookId"})},
        indexes = {@Index(name = "key_bookId", columnList = "bookId"),
                @Index(name = "key_categoryId", columnList = "categoryId"),
                @Index(name = "key_updatedTime", columnList = "updatedTime")})
public class UserBook {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long userBookId;
    private long userId;
    private long bookId;
    private String title;
    private String author;
    private String category;
    private long categoryId;
    private String customCategory = UserBookUtils.NULL_CATEGORY;
    private int getType;
    private Date getTime;
    private int bookType;
    private int readStatus = ReadStatus.NotRead.getType();
    private String getPlace;
    private String readPlace;
    private Date readTime;
    private String tag;
    private String other;
    private int permission = Permission.Open.getType();
    private float rating;
    private long remarkId;
    private int enterType;
    private boolean isLease;//可租借状态
    private boolean isSale;//可出售状态
    private double evaluation;//估价
    private double dayRentGold;//日租金
    @Column(length = 10000)
    private String description;//描述
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", insertable = false, updatable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private UserCategory userCategory;

    public long getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(long userBookId) {
        this.userBookId = userBookId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(String customCategory) {
        this.customCategory = customCategory;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public UserCategory getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(UserCategory userCategory) {
        this.userCategory = userCategory;
    }
}
