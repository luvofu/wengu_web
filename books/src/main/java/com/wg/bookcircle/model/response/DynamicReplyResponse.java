package com.wg.bookcircle.model.response;

import com.alibaba.fastjson.JSONObject;
import com.wg.bookcircle.domain.BookCircleReply;
import com.wg.common.Enum.bookcircle.ReplyType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/24.
 */
public class DynamicReplyResponse {
    private long replyId;
    private int replyType;
    private long userId;
    private String nickname;
    private String avatar;
    private String content;
    private Date createdTime;

    JSONObject replyObj;

    public DynamicReplyResponse(BookCircleReply bookCircleReply) {
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(bookCircleReply.getUserId());
        this.replyId = bookCircleReply.getReplyId();
        this.replyType = bookCircleReply.getReplyType();
        this.userId = userInfo.getUserId();
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.content = bookCircleReply.getContent();
        this.createdTime = bookCircleReply.getCreatedTime();
        if (bookCircleReply.getReplyType() == ReplyType.Reply.getType()) {
            replyObj = new JSONObject();
            BookCircleReply objCr = DaoUtils.bookCircleReplyDao.findOne(bookCircleReply.getReplyObjId());
            replyObj.put("replyObjId", objCr.getReplyId());
            UserInfo objUser = DaoUtils.userInfoDao.findOne(objCr.getUserId());
            replyObj.put("userId", objUser.getUserId());
            replyObj.put("nickname", objUser.getNickname());
        }
    }

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }

    public int getReplyType() {
        return replyType;
    }

    public void setReplyType(int replyType) {
        this.replyType = replyType;
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

    public JSONObject getReplyObj() {
        return replyObj;
    }

    public void setReplyObj(JSONObject replyObj) {
        this.replyObj = replyObj;
    }
}
