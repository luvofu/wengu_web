package com.wg.bookgroup.model.response;

import com.wg.common.Enum.bookgroup.ReplyType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.bookgroup.domain.GroupReply;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/1.
 */
public class CommentReplyResponse {
    private long replyId;
    private String content;
    private Date createdTime;

    private long userId;
    private String avatar;
    private String nickname;

    private int replyType;
    private Long replyObjId;
    private String replyObjContent;
    private Long replyObjUserId;
    private String replyObjNickname;

    public CommentReplyResponse(GroupReply groupReply) {
        //reply
        this.replyId = groupReply.getReplyId();
        this.content = groupReply.getContent();
        this.replyType = groupReply.getReplyType();
        this.createdTime = groupReply.getCreatedTime();
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(groupReply.getUserId());
        this.userId = userInfo.getUserId();
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());

        //replyobj
        if (replyType == ReplyType.Reply.getType()) {
            GroupReply objCr = DaoUtils.groupReplyDao.findOne(groupReply.getReplyObjId());
            this.replyObjId = objCr.getReplyId();
            this.replyObjContent = objCr.getContent();
            UserInfo objUser = DaoUtils.userInfoDao.findOne(objCr.getUserId());
            this.replyObjUserId = objUser.getUserId();
            this.replyObjNickname = objUser.getNickname();
        }
    }

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getReplyType() {
        return replyType;
    }

    public void setReplyType(int replyType) {
        this.replyType = replyType;
    }

    public Long getReplyObjId() {
        return replyObjId;
    }

    public void setReplyObjId(Long replyObjId) {
        this.replyObjId = replyObjId;
    }

    public String getReplyObjContent() {
        return replyObjContent;
    }

    public void setReplyObjContent(String replyObjContent) {
        this.replyObjContent = replyObjContent;
    }

    public Long getReplyObjUserId() {
        return replyObjUserId;
    }

    public void setReplyObjUserId(Long replyObjUserId) {
        this.replyObjUserId = replyObjUserId;
    }

    public String getReplyObjNickname() {
        return replyObjNickname;
    }

    public void setReplyObjNickname(String replyObjNickname) {
        this.replyObjNickname = replyObjNickname;
    }
}
