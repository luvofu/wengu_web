package com.wg.community.model.request;

import com.wg.common.Enum.community.MemberType;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public class CommunityRequest {
    private String token;
    private long communityId = -1;
    private String name;
    private String themePic;
    private double longitude = -1;
    private double latitude = -1;
    private String address;
    private int joinStatus = -1;
    private String tag;
    private String commDes;
    private String commNote;

    private int page = 0;
    private String keyword;
    private String ids;

    private long commMemberId = -1;

    private long messageId = -1;
    private int dealStatus = -1;
    private String content;

    private int categoryType = -1;
    private String category;

    private long bookId;

    int memberType = MemberType.Stranger.getType();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public int getJoinStatus() {
        return joinStatus;
    }

    public void setJoinStatus(int joinStatus) {
        this.joinStatus = joinStatus;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public long getCommMemberId() {
        return commMemberId;
    }

    public void setCommMemberId(long commMemberId) {
        this.commMemberId = commMemberId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(int dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }
}
