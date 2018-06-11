package com.wg.message.model;

/**
 * Created by Administrator on 2017/4/21 0021.
 */
public class ReportInfo {
    long userId;
    String imText;
    String emailSub;
    String emailText;
    String emailTo;
    boolean sendImMsg;
    boolean sendEmailMsg;

    public ReportInfo(long userId, String imText, String emailSub, String emailText, String emailTo,
                      boolean sendImMsg, boolean sendEmailMsg) {
        this.userId = userId;
        this.imText = imText;
        this.emailSub = emailSub;
        this.emailText = emailText;
        this.emailTo = emailTo;
        this.sendImMsg = sendImMsg;
        this.sendEmailMsg = sendEmailMsg;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getImText() {
        return imText;
    }

    public void setImText(String imText) {
        this.imText = imText;
    }

    public String getEmailSub() {
        return emailSub;
    }

    public void setEmailSub(String emailSub) {
        this.emailSub = emailSub;
    }

    public String getEmailText() {
        return emailText;
    }

    public void setEmailText(String emailText) {
        this.emailText = emailText;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public boolean isSendImMsg() {
        return sendImMsg;
    }

    public void setSendImMsg(boolean sendImMsg) {
        this.sendImMsg = sendImMsg;
    }

    public boolean isSendEmailMsg() {
        return sendEmailMsg;
    }

    public void setSendEmailMsg(boolean sendEmailMsg) {
        this.sendEmailMsg = sendEmailMsg;
    }
}
