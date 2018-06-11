package com.wg.news.domain;

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/13.
 */
@Cacheable
@Entity
@Table(name = "temp_news", indexes = {@Index(name = "ukey_uniqueKey", columnList = "uniqueKey", unique = true)})
public class TempNews {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long tempNewsId;
    private String uniqueKey;//新闻唯一标识,标题md5值
    @Column(length = 10000)
    private String newsJson;//新闻体
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();

    public long getTempNewsId() {
        return tempNewsId;
    }

    public void setTempNewsId(long tempNewsId) {
        this.tempNewsId = tempNewsId;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getNewsJson() {
        return newsJson;
    }

    public void setNewsJson(String newsJson) {
        this.newsJson = newsJson;
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
}
