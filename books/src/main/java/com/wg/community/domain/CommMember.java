package com.wg.community.domain;

import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
@Cacheable
@Entity
@Table(name = "community_member",
        uniqueConstraints = {@UniqueConstraint(name = "ukey_cid_uid", columnNames = {"communityId", "userId"})},
        indexes = {@Index(name = "key_userId",columnList = "userId")})
public class CommMember {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long commMemberId;
    private long communityId;
    private long userId;
    private int memberType;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communityId", insertable = false, updatable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    public long getCommMemberId() {
        return commMemberId;
    }

    public void setCommMemberId(long commMemberId) {
        this.commMemberId = commMemberId;
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
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

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
