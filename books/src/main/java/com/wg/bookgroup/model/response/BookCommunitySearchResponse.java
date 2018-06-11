package com.wg.bookgroup.model.response;

import com.wg.bookgroup.domain.BookGroup;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午4:11
 * To change this template use File | Settings | File Templates.
 */
public class BookCommunitySearchResponse {
    private long communityId;
    private String title;
    private String subTitle;
    private String  author;
    private long commentNum=0;

    public BookCommunitySearchResponse(){

    }
    public BookCommunitySearchResponse(BookGroup bookGroup) {
        this.communityId= bookGroup.getCommunityId();
        this.title= bookGroup.getTitle();
        this.subTitle= bookGroup.getSubTitle();
        this.author= bookGroup.getAuthor();
        this.commentNum= bookGroup.getCommentNum();
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(long commentNum) {
        this.commentNum = commentNum;
    }
}

