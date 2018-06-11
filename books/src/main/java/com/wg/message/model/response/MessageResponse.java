package com.wg.message.model.response;

import com.wg.common.Enum.message.MessageType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.message.domain.UserMessage;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;

import java.util.Date;

import static com.wg.message.utils.UserMsgUtils.loadMsgText;

/**
 * Created by Administrator on 2016/9/11.
 */
public class MessageResponse {
    private long messageId;
    private int messageType;
    private long messageObjId;
    private String content;
    private int readStatus;
    private int dealStatus;
    private long sendUserId;
    private String avatar;
    private String nickname;
    private Date createdTime;
    private boolean notify;

    private Boolean isDir;

    public MessageResponse(UserMessage userMessage) {
        try {
            this.messageId = userMessage.getMessageId();
            this.messageType = userMessage.getMessageType();
            this.messageObjId = userMessage.getMessageObjId();

            UserInfo userInfo = DaoUtils.userInfoDao.findOne(userMessage.getSendUserId());
            this.sendUserId = userInfo.getUserId();
            this.avatar = Utils.getUrl(userInfo.getAvatar());
            this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());

            this.content = loadMsgText(userMessage);

            this.readStatus = userMessage.getReadStatus();
            this.dealStatus = userMessage.getDealStatus();
            this.createdTime = userMessage.getCreatedTime();

            this.notify = MessageType.isNotify(userMessage.getMessageType());
        } catch (Exception e) {
            setDir(true);
        }
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getMessageObjId() {
        return messageObjId;
    }

    public void setMessageObjId(long messageObjId) {
        this.messageObjId = messageObjId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public int getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(int dealStatus) {
        this.dealStatus = dealStatus;
    }

    public long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(long sendUserId) {
        this.sendUserId = sendUserId;
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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public Boolean getDir() {
        return isDir;
    }

    public void setDir(Boolean dir) {
        isDir = dir;
    }
}
