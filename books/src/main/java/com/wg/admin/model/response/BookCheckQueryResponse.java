package com.wg.admin.model.response;

import com.wg.common.utils.Utils;
import com.wg.userbook.domain.BookCheck;
import com.wg.common.Enum.userbook.CheckStatus;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-11-6
 * Time: 下午3:51
 * To change this template use File | Settings | File Templates.
 */
public class BookCheckQueryResponse {
    private String nickname;

    private long bookCheckId;
    private int checkStatus;
    private String checkInfo;
    private String title;
    private String originTitle;
    private String subTitle;
    private String author;
    private String summary;
    private String price;
    private String cover;
    private String pages;
    private String authorInfo;
    private String translator;
    private String publisher;
    private String pubDate;
    private String isbn13;
    private String binding;
    private String createdTime;
    private String updatedTime;
    private String extendInfo;
    private String checkStatusMsg;

    public BookCheckQueryResponse(BookCheck bookCheck, String nickname) {
        this.nickname = nickname;
        this.bookCheckId = bookCheck.getBookCheckId();
        this.checkStatus = bookCheck.getCheckStatus();
        if (this.checkStatus == CheckStatus.InCheck.getStatus()) {
            this.checkStatusMsg = "审核中";
        } else if (this.checkStatus == CheckStatus.NotPass.getStatus()) {
            this.checkStatusMsg = "未通过";
        } else if (this.checkStatus == CheckStatus.Pass.getStatus()) {
            this.checkStatusMsg = "通  过";
        }
        this.checkInfo = bookCheck.getCheckInfo() != null ? bookCheck.getCheckInfo() : "暂无";
        this.title = bookCheck.getTitle();
        this.originTitle = bookCheck.getOriginTitle();
        this.subTitle = bookCheck.getSubTitle();
        this.author = bookCheck.getAuthor();
        this.summary = bookCheck.getSummary();
        this.price = bookCheck.getPrice();
        this.cover = Utils.getUrl(bookCheck.getCover());
        this.pages = bookCheck.getPages();
        this.authorInfo = bookCheck.getAuthorInfo();
        this.translator = bookCheck.getTranslator();
        this.publisher = bookCheck.getPublisher();
        this.pubDate = bookCheck.getPubDate();
        this.isbn13 = bookCheck.getIsbn13() != null ? bookCheck.getIsbn13() : "无";
        this.binding = bookCheck.getBinding();
        this.createdTime = bookCheck.getCreatedTime().toString();
        this.updatedTime = bookCheck.getUpdatedTime().toString();
        this.extendInfo = bookCheck.getExtendInfo();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getBookCheckId() {
        return bookCheckId;
    }

    public void setBookCheckId(long bookCheckId) {
        this.bookCheckId = bookCheckId;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckInfo() {
        return checkInfo;
    }

    public void setCheckInfo(String checkInfo) {
        this.checkInfo = checkInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getAuthorInfo() {
        return authorInfo;
    }

    public void setAuthorInfo(String authorInfo) {
        this.authorInfo = authorInfo;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    public String getCheckStatusMsg() {
        return checkStatusMsg;
    }

    public void setCheckStatusMsg(String checkStatusMsg) {
        this.checkStatusMsg = checkStatusMsg;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }
}
