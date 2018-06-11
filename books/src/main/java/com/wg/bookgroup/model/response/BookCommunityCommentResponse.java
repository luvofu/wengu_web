package com.wg.bookgroup.model.response;

import com.wg.common.Enum.common.GoodType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserGoodUtils;
import com.wg.user.utils.UserUtils;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/1.
 */
public class BookCommunityCommentResponse {
    private long commentId;
    private long userId;
    private String nickname;
    private String avatar;
    private long communityId;
    private String title;
    private String subTitle;
    private String author;
    private long goodNum;
    private long replyNum;
    private String content;
    private Date createdTime;
    private boolean isGood = false;

    public BookCommunityCommentResponse(GroupComment groupComment, UserToken userToken) {
        //comment
        this.commentId = groupComment.getCommentId();
        this.goodNum = groupComment.getGoodNum();
        this.replyNum = groupComment.getReplyNum();
        this.content = groupComment.getContent();
        this.createdTime = groupComment.getCreatedTime();

        //user
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(groupComment.getUserId());
        this.userId = userInfo.getUserId();
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.avatar = Utils.getUrl(userInfo.getAvatar());

        //bookgroup
        BookGroup bookGroup = DaoUtils.bookGroupDao.findOne(groupComment.getCommunityId());
        this.communityId = bookGroup.getCommunityId();
        this.title = bookGroup.getTitle();
        this.subTitle = bookGroup.getSubTitle();
        this.author = bookGroup.getAuthor();

        //good
        if (userToken != null) {
            isGood = UserGoodUtils.isUserGood(groupComment.getCommentId(), userToken.getUserId(), GoodType.Comment.getType());
        }
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
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

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }
}
