package com.wg.userbook.model.request;

/**
 * Created by Administrator on 2016/9/7.
 */
public class UserBookMarkRequest {
    //user
    private String token;
    private long userBookId = -1;
    private long bookmarkId = -1;
    private int pages = -1;
    private int totalPage = -1;
    private String keyword;
    private int page=0;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(long userBookId) {
        this.userBookId = userBookId;
    }

    public long getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(long bookmarkId) {
        this.bookmarkId = bookmarkId;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
