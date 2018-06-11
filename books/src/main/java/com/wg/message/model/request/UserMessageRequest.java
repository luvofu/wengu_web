package com.wg.message.model.request;

import com.wg.common.Enum.message.MsgOptType;

/**
 * Created by Administrator on 2016/9/11.
 */
public class UserMessageRequest {
    private String token;
    private int page = 0;
    private long messageId = -1;
    //接受消息用户id
    private long acceptUserId = -1;
    //发送消息用户id
    private long sendUserId = -1;
    //消息类型:0好友邀请、1书桌邀请、2书圈回复、3社区回复
    private int messageType = -1;
    //消息对象id :0好友ID 、1书桌ID、 2书圈回复ID 、3社区回复ID
    private long messageObjId = -1;
    private String content;

    private String userIdList;
    private long deskId = -1;

    private int msgReadType = MsgOptType.ByIds.getType();
    private String messageTypes;
    private String messageIds;
    private int msgGetType = MsgOptType.ByIds.getType();


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getAcceptUserId() {
        return acceptUserId;
    }

    public void setAcceptUserId(long acceptUserId) {
        this.acceptUserId = acceptUserId;
    }

    public long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(long sendUserId) {
        this.sendUserId = sendUserId;
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

    public String getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(String userIdList) {
        this.userIdList = userIdList;
    }

    public long getDeskId() {
        return deskId;
    }

    public void setDeskId(long deskId) {
        this.deskId = deskId;
    }

    public int getMsgReadType() {
        return msgReadType;
    }

    public void setMsgReadType(int msgReadType) {
        this.msgReadType = msgReadType;
    }

    public String getMessageTypes() {
        return messageTypes;
    }

    public void setMessageTypes(String messageTypes) {
        this.messageTypes = messageTypes;
    }

    public String getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(String messageIds) {
        this.messageIds = messageIds;
    }

    public int getMsgGetType() {
        return msgGetType;
    }

    public void setMsgGetType(int msgGetType) {
        this.msgGetType = msgGetType;
    }
}
