package com.wg.userbook.domain;

import com.wg.common.utils.TimeUtils;
import com.wg.common.Enum.userbook.ReadStatus;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//用户书签表
@Cacheable
@Entity
@Table(name = "user_bookmark", indexes = {
        @Index(name = "ukey_userBookId", columnList = "userBookId", unique = true),
        @Index(name = "key_userId", columnList = "userId")})
public class UserBookmark {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long bookmarkId;
    private long userId;
    private long userBookId;
    private String name;
    private int pages;
    private int totalPage;
    private int status = ReadStatus.Reading.getType();
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userbookId", insertable = false, updatable = false)
    private UserBook userBook;

    public long getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(long bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(long userBookId) {
        this.userBookId = userBookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public UserBook getUserBook() {
        return userBook;
    }

    public void setUserBook(UserBook userBook) {
        this.userBook = userBook;
    }
}
