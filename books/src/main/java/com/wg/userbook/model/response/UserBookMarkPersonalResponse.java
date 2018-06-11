package com.wg.userbook.model.response;

import com.wg.common.utils.Utils;
import com.wg.userbook.domain.UserBookmark;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午6:47
 * To change this template use File | Settings | File Templates.
 */
public class UserBookMarkPersonalResponse {
    private long bookmarkId;
    private String name;
    private int pages;
    private int totalPage;
    private String cover;
    private Date updatedTime;

    public UserBookMarkPersonalResponse(UserBookmark userBookmark) {
        this.bookmarkId = userBookmark.getBookmarkId();
        this.name = userBookmark.getName();
        this.pages = userBookmark.getPages();
        this.totalPage = userBookmark.getTotalPage();
        this.cover = Utils.getUrl(userBookmark.getUserBook().getBook().getCover());
        this.updatedTime = userBookmark.getUpdatedTime();
    }


    public long getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(long bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
