package com.wg.community.model.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.common.Constant;
import com.wg.common.Enum.community.MemberType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.community.domain.CommMember;
import com.wg.community.domain.Community;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserUtils;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public class CommunityDetailResponse {
    private long communityId;
    private String name;
    private String themePic;
    private double longitude;
    private double latitude;
    private String address;
    private String tag;
    private int joinStatus;
    private String commDes;
    private String commNote;
    private long bookNum;
    private long memberNum;
    private long imTribeId;
    private Date createdTime;
    private JSONArray communityMembers = new JSONArray();
    private int memberType = MemberType.Stranger.getType();

    public CommunityDetailResponse(Community community, UserToken userToken) {
        this.communityId = community.getCommunityId();
        this.name = community.getName();
        this.themePic = Utils.getUrl(community.getThemePic());
        this.longitude = community.getLongitude();
        this.latitude = community.getLatitude();
        this.address = community.getAddress();
        this.tag = community.getTag();
        this.joinStatus = community.getJoinStatus();
        this.commDes = community.getCommDes();
        this.commNote = community.getCommNote();
        this.bookNum = community.getBookNum();
        this.memberNum = community.getMemberNum();
        this.imTribeId = community.getImTribeId();
        this.createdTime = community.getCreatedTime();

        long userId = 0;
        if (userToken != null) {
            userId = userToken.getUserId();
            CommMember commMember = DaoUtils.commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userId);
            if (commMember != null) {
                this.memberType = commMember.getMemberType();
                JSONObject jsonObject = new JSONObject();
                UserInfo userInfo = DaoUtils.userInfoDao.findOne(commMember.getUserId());
                jsonObject.put("commMemberId", commMember.getCommMemberId());
                jsonObject.put("userId", userInfo.getUserId());
                jsonObject.put("nickname", UserUtils.getSafeNickname(userInfo.getNickname()));
                jsonObject.put("avatar", Utils.getUrl(userInfo.getAvatar()));
                jsonObject.put("memberType", commMember.getMemberType());
                communityMembers.add(jsonObject);
            }
        }

        List<CommMember> commMemberList = DaoUtils.commMemberDao.findByCommunityIdAndUserIdNotOrderByUpdatedTimeDesc(
                community.getCommunityId(), userId, new PageRequest(0, Constant.PAGE_NUM_SMALL)).getContent();
        for (CommMember commMember : commMemberList) {
            JSONObject jsonObject = new JSONObject();
            UserInfo userInfo = DaoUtils.userInfoDao.findOne(commMember.getUserId());
            jsonObject.put("commMemberId", commMember.getCommMemberId());
            jsonObject.put("userId", userInfo.getUserId());
            jsonObject.put("nickname", UserUtils.getSafeNickname(userInfo.getNickname()));
            jsonObject.put("avatar", Utils.getUrl(userInfo.getAvatar()));
            jsonObject.put("memberType", commMember.getMemberType());
            communityMembers.add(jsonObject);
        }
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThemePic() {
        return themePic;
    }

    public void setThemePic(String themePic) {
        this.themePic = themePic;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getJoinStatus() {
        return joinStatus;
    }

    public void setJoinStatus(int joinStatus) {
        this.joinStatus = joinStatus;
    }

    public String getCommDes() {
        return commDes;
    }

    public void setCommDes(String commDes) {
        this.commDes = commDes;
    }

    public String getCommNote() {
        return commNote;
    }

    public void setCommNote(String commNote) {
        this.commNote = commNote;
    }

    public long getBookNum() {
        return bookNum;
    }

    public void setBookNum(long bookNum) {
        this.bookNum = bookNum;
    }

    public long getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(long memberNum) {
        this.memberNum = memberNum;
    }

    public long getImTribeId() {
        return imTribeId;
    }

    public void setImTribeId(long imTribeId) {
        this.imTribeId = imTribeId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public JSONArray getCommunityMembers() {
        return communityMembers;
    }

    public void setCommunityMembers(JSONArray communityMembers) {
        this.communityMembers = communityMembers;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }
}
