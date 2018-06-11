package com.wg.admin.model.response;

import com.wg.user.domain.Feedback;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-12-3
 * Time: 下午3:00
 * To change this template use File | Settings | File Templates.
 */
public class FeedbackQueryResponse {
    private long feedbackId;
    private String username;
    private String content;
    private String connection;
    private String updatedTime;
    private String createdTime;

    public FeedbackQueryResponse(Feedback feedback, UserInfo userInfo) {
        this.feedbackId = feedback.getFeedbackId();
        this.username = UserUtils.getSafeNickname(userInfo.getNickname());
        this.content = feedback.getContent();
        this.connection = feedback.getConnection();
        this.updatedTime = feedback.getUpdatedTime().toString();
        this.createdTime = feedback.getCreatedTime().toString();
    }

    public long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
