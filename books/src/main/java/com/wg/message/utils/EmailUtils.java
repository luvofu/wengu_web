package com.wg.message.utils;

import com.wg.common.Enum.common.ExportType;
import com.wg.common.Enum.message.IMsgType;
import com.wg.common.PropConfig;
import com.wg.common.utils.TimeUtils;
import com.wg.message.model.ReportInfo;
import com.wg.task.ReportExpTask;
import com.wg.user.domain.UserInfo;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Properties;

import static com.wg.common.utils.Utils.getUrl;
import static com.wg.common.utils.Utils.logger;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 17-1-7
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */
public class EmailUtils {

    public static void sendEmailBookCheck(final String subject, final String message, final String emailTo) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Message msg = getMessage(PropConfig.EMAIL_FROM, emailTo);
                    msg.setSubject(subject);
                    msg.setText(message);
                    Transport.send(msg);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }.start();
    }

    public static void reportExp(UserInfo userInfo, Object object, String path, int exportType) {
        String expiredTime = TimeUtils.formatDate(TimeUtils.getModifyDate(TimeUtils.getCurrentDate(), 3, null, null, null), TimeUtils.YYYY_MM_DD_HH_MM);
        String url = getUrl(path);
        String objname = "";
        if (exportType == ExportType.Notebook.getType()) {
            objname = "笔记《" + object + "》";
        } else if (exportType == ExportType.Userbook.getType()) {
            objname = "书目";
        }
        final String imText = "您的" + objname + "已成功导出，下载链接已发送至您的邮箱！\n请于" + expiredTime + "前下载，逾期将失效。\nPC端下载地址：" + url;
        final String emailSub = "文芽：" + userInfo.getNickname() + "，您的" + objname + "已成功导出！";
        final String emailText = "您的" + objname + "已成功导出，请于" + expiredTime + "前下载，逾期下载链接将失效。" +
                "<a href=\"" + url + "\" target=\"_bank\">立即下载</a><br/> - 文芽团队";
        final String emailTo = userInfo.getMailbox();
        final long userId = userInfo.getUserId();
        new Thread() {
            @Override
            public void run() {
                report(new ReportInfo(userId, imText, emailSub, emailText, emailTo, true, true));
            }
        }.start();
    }

    public static void report(ReportInfo reportInfo) {
        if (reportInfo.isSendImMsg()) {
            try {
                IMUtils.sendMsg(PropConfig.OFFICER_USERID, reportInfo.getUserId(), reportInfo.getImText(), IMsgType.Text.getType());
                reportInfo.setSendImMsg(false);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        if (reportInfo.isSendEmailMsg()) {
            try {
                Message msg = getMessage(PropConfig.EMAIL_FROM, reportInfo.getEmailTo());
                msg.setSubject(reportInfo.getEmailSub());
                msg.setContent(reportInfo.getEmailText(), "text/html;charset=utf-8");
                msg.saveChanges();
                Transport.send(msg);
                reportInfo.setSendEmailMsg(false);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        //add to task set if exist fail
        if (reportInfo.isSendImMsg() || reportInfo.isSendEmailMsg()) {
            ReportExpTask.reportSet.add(reportInfo);
        }
    }

    public static void sendEmailAttach(final String filePath, final String emailTo) {
        try {
            Message msg = getMessage(PropConfig.EMAIL_FROM, emailTo);
            msg.setSubject("subject");
            MimeMultipart mailContent = new MimeMultipart("mixed");
            msg.setContent(mailContent);
            MimeBodyPart attach = new MimeBodyPart();
            attach.setFileName(MimeUtility.encodeText("filename"));
            attach.setDataHandler(new DataHandler(new FileDataSource(PropConfig.UPLOAD_PATH + filePath)));
            mailContent.addBodyPart(attach);
            msg.saveChanges();
            Transport.send(msg);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static Message getMessage(String emailFrom, String emailTo) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", PropConfig.EMAIL_HOSTNAME);
            props.put("mail.smtp.port", PropConfig.EMAIL_PORT);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(PropConfig.EMAIL_USERNAME, PropConfig.EMAIL_PASSWORD);
                }
            });
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailFrom, "NoReply"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
            return msg;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
