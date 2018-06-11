package com.wg.admin.model.request;

import com.wg.common.Enum.userbook.CheckStatus;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-11-6
 * Time: 下午2:57
 * To change this template use File | Settings | File Templates.
 */
public class BookCheckQueryRequest {
    private long bookCheckId;
    private int checkStatus = CheckStatus.InCheck.getStatus();
    private String startTime;
    private String endTime;
    private int page = 0;
    private String checkInfo;

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getBookCheckId() {
        return bookCheckId;
    }

    public void setBookCheckId(long bookCheckId) {
        this.bookCheckId = bookCheckId;
    }

    public String getCheckInfo() {
        return checkInfo;
    }

    public void setCheckInfo(String checkInfo) {
        this.checkInfo = checkInfo;
    }
}
