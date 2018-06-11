package com.wg.bookcircle.model.request;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-10
 * Time: 下午7:42
 * To change this template use File | Settings | File Templates.
 */
public class DynamicRequest {
    private String token;
    private int page = 0;

    private long dynamicId = -1;
    private String content;
    private int replyType = -1;
    private long replyObjId = -1;
    private long acceptUserId = -1;

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

    public long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(long dynamicId) {
        this.dynamicId = dynamicId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReplyType() {
        return replyType;
    }

    public void setReplyType(int replyType) {
        this.replyType = replyType;
    }

    public long getReplyObjId() {
        return replyObjId;
    }

    public void setReplyObjId(long replyObjId) {
        this.replyObjId = replyObjId;
    }

    public long getAcceptUserId() {
        return acceptUserId;
    }

    public void setAcceptUserId(long acceptUserId) {
        this.acceptUserId = acceptUserId;
    }
}
