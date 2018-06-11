package com.wg.bookgroup.domain;

/**
 * Created by wzhonggo on 8/4/2016.
 */

import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.util.Date;

//社区回复表
@Cacheable
@Entity
@Table(name = "community_reply", indexes = {
        @Index(name = "key_userId", columnList = "userId"),
        @Index(name = "key_commentId", columnList = "commentId"),
        @Index(name = "key_replyObjId", columnList = "replyObjId")})
public class GroupReply {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long replyId;
    private long userId;
    private long commentId;
    @Column(length = 10000)
    private String content;
    //回复对象类型:0评论,1回复
    private int replyType;
    // 回复对象id:评论id、回复id
    private long replyObjId;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId", insertable = false, updatable = false)
    private GroupComment groupComment;

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReplyType() {
        return replyType;
    }

    public void setReplyType(int replyType) {
        this.replyType = replyType;
    }

    public long getReplyObjId() {
        return replyObjId;
    }

    public void setReplyObjId(long replyObjId) {
        this.replyObjId = replyObjId;
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

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public GroupComment getGroupComment() {
        return groupComment;
    }

    public void setGroupComment(GroupComment groupComment) {
        this.groupComment = groupComment;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
