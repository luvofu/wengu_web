package com.wg.bookgroup.domain;

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//书籍社区表
@Cacheable
@Entity
@Table(name = "book_community", indexes = {@Index(name = "key_title", columnList = "title"),
        @Index(name = "key_updatedTime", columnList = "updatedTime")})
public class BookGroup {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long communityId;
    private long commentNum = 0;
    private float heat = 0;
    private float rating;
    private String title;
    private String subTitle;
    private String author;
    @Column(length = 10000)
    private String categoryStatis;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(long commentNum) {
        this.commentNum = commentNum;
    }

    public float getHeat() {
        return heat;
    }

    public void setHeat(float heat) {
        this.heat = heat;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCategoryStatis() {
        return categoryStatis;
    }

    public void setCategoryStatis(String categoryStatis) {
        this.categoryStatis = categoryStatis;
    }
}
