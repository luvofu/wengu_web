package com.wg.message.model.response;

import com.wg.message.domain.UserMessage;

import static com.wg.message.utils.UserMsgUtils.loadMsgLink;

/**
 * Created by Administrator on 2017/4/14 0014.
 */
public class MessageResponseEx extends MessageResponse {
    private Integer msgLinkType;
    private Long msgLinkId;
    private String msgLinkContent;
    private String msgLinkImage;

    public MessageResponseEx(UserMessage userMessage) {
        super(userMessage);
        loadMsgLink(userMessage, this);
    }

    public Integer getMsgLinkType() {
        return msgLinkType;
    }

    public void setMsgLinkType(Integer msgLinkType) {
        this.msgLinkType = msgLinkType;
    }

    public Long getMsgLinkId() {
        return msgLinkId;
    }

    public void setMsgLinkId(Long msgLinkId) {
        this.msgLinkId = msgLinkId;
    }

    public String getMsgLinkContent() {
        return msgLinkContent;
    }

    public void setMsgLinkContent(String msgLinkContent) {
        this.msgLinkContent = msgLinkContent;
    }

    public String getMsgLinkImage() {
        return msgLinkImage;
    }

    public void setMsgLinkImage(String msgLinkImage) {
        this.msgLinkImage = msgLinkImage;
    }
}
