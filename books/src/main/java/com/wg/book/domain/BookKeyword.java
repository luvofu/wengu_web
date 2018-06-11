package com.wg.book.domain;

/**
 * Created by wzhonggo on 8/4/2016.
 */

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

//书籍表
@Cacheable
@Entity
@Table(name = "book_keyword", indexes = {@Index(name = "ukey_keyword", columnList = "keyword", unique = true)})
public class BookKeyword {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long keywordId;
    private String keyword;
    private long searchNum = 0;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    public long getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(long keywordId) {
        this.keywordId = keywordId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getSearchNum() {
        return searchNum;
    }

    public void setSearchNum(long searchNum) {
        this.searchNum = searchNum;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }
}
