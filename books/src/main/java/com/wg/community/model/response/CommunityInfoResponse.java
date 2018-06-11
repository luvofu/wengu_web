package com.wg.community.model.response;

import com.wg.common.Enum.community.MemberType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.community.domain.CommMember;
import com.wg.community.domain.Community;
import com.wg.user.domain.UserToken;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public class CommunityInfoResponse {
    private long communityId;
    private String name;
    private String themePic;
    private double longitude;
    private double latitude;
    private String address;
    private String commDes;
    private long bookNum;
    private long memberNum;
    private long imTribeId;
    private int memberType = MemberType.Owner.getType();

    public CommunityInfoResponse(CommMember commMember, UserToken userToken) {
        Community community = DaoUtils.communityDao.findOne(commMember.getCommunityId());
        this.communityId = community.getCommunityId();
        this.name = community.getName();
        this.themePic = Utils.getUrl(community.getThemePic());
        this.longitude = community.getLongitude();
        this.latitude = community.getLatitude();
        this.address = community.getAddress();
        this.commDes = community.getCommDes();
        this.bookNum = community.getBookNum();
        this.memberNum = community.getMemberNum();
        this.imTribeId = community.getImTribeId();
        if (userToken != null && commMember.getUserId() == userToken.getUserId()) {
            this.memberType = commMember.getMemberType();
        }
    }

    public CommunityInfoResponse(Community community, UserToken userToken) {
        this.communityId = community.getCommunityId();
        this.name = community.getName();
        this.themePic = Utils.getUrl(community.getThemePic());
        this.longitude = community.getLongitude();
        this.latitude = community.getLatitude();
        this.address = community.getAddress();
        this.commDes = community.getCommDes();
        this.bookNum = community.getBookNum();
        this.memberNum = community.getMemberNum();
        this.imTribeId = community.getImTribeId();
        if (userToken != null) {
            CommMember commMember = DaoUtils.commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userToken.getUserId());
            if (commMember != null) {
                this.memberType = commMember.getMemberType();
            }
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

    public String getCommDes() {
        return commDes;
    }

    public void setCommDes(String commDes) {
        this.commDes = commDes;
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

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }
}
