package com.wg.bookgroup.model.request;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-3
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
public class CommunityCommentRequest {
    private long commentId;
    private String content;
    private int replyType;
    private long replyObjId;
    private long replyObjUserId;

    private String token;

    private int page = 0;

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
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

    public long getReplyObjUserId() {
        return replyObjUserId;
    }

    public void setReplyObjUserId(long replyObjUserId) {
        this.replyObjUserId = replyObjUserId;
    }

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
}
