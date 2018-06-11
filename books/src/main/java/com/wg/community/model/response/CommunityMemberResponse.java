package com.wg.community.model.response;

import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.community.domain.CommMember;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public class CommunityMemberResponse {
    private long commMemberId;
    private long communityId;
    private long userId;
    private String nickname;
    private String avatar;
    private int memberType;

    public CommunityMemberResponse(CommMember commMember) {
        this.commMemberId = commMember.getCommMemberId();
        this.communityId = commMember.getCommunityId();
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(commMember.getUserId());
        this.userId = userInfo.getUserId();
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.memberType = commMember.getMemberType();
    }

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

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }
}
