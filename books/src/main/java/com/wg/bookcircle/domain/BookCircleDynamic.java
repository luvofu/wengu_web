package com.wg.bookcircle.domain;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//书圈动态表

import com.wg.common.Enum.bookcircle.DynamicType;
import com.wg.common.Enum.common.Permission;
import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.util.Date;


@Cacheable
@Entity
@Table(name = "bookcircle_dynamic", indexes = {@Index(name = "key_userId", columnList = "userId")})
public class BookCircleDynamic {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long dynamicId;
    private long userId;
    @Column(length = 10000)
    private String content;
    private String image;
    private String location;
    //    链接id（普通图文id为空,其他均为实际的id,用于点击跳转到指定的内容模块）
    private long linkId;
    //    类型（0:普通图文/1:书籍/2:书单/3:评论）
    private int linkType;
    //    0:(默认)用户自定义动态；1:系统推送动态
    private int dynamicType = DynamicType.Personal.getType();
    //  0公开、1好友、2私密
    private int permission = Permission.Open.getType();
    private long goodNum = 0;
    private long replyNum = 0;
    //删除标志
    private boolean isDelete = false;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;

    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    public long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(long dynamicId) {
        this.dynamicId = dynamicId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public int getDynamicType() {
        return dynamicType;
    }

    public void setDynamicType(int dynamicType) {
        this.dynamicType = dynamicType;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public long getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(long goodNum) {
        this.goodNum = goodNum;
    }

    public long getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(long replyNum) {
        this.replyNum = replyNum;
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

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
