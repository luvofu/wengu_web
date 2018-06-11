package com.wg.bookcircle.model.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.book.domain.Book;
import com.wg.bookcircle.domain.BookCircleDynamic;
import com.wg.bookcircle.domain.BookCircleReply;
import com.wg.booksheet.domain.BookSheet;
import com.wg.common.Enum.bookcircle.DynamicType;
import com.wg.common.Enum.bookcircle.LinkType;
import com.wg.common.Enum.bookcircle.ReplyType;
import com.wg.common.Enum.common.GoodType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserGoodUtils;
import com.wg.user.utils.UserUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/10.
 */
public class BookCircleDynamicResponse {
    //dynamic
    private long dynamicId;
    private String content;
    private String image;
    private String location;
    //    0:(默认)用户自定义动态；1:系统推送动态
    private int dynamicType = DynamicType.Personal.getType();
    private long goodNum = 0;
    private long replyNum = 0;
    private Date createdTime;

    //user
    private long userId;
    private String nickname;
    private String avatar;

    //linkcontent
    //    类型（0:普通图文/1:书籍/2:书单/3:评论）
    private int linkType;
    private long linkId;
    //book
    private String title;
    private String subTitle;
    private String author;
    private String bookCover;
    //booksheet
    private String name;
    private String bookSheetCover;
    //comment
    private long commentUserId;
    private String commentNickname;
    private long commentId;
    private String commentContent;
    private long communityId;
    private String communityTitle;
    private String communitySubTitle;
    private String communityAuthor;

    //replylist
    private JSONArray dynamicReplyList = new JSONArray();

    private boolean isGood = false;

    public BookCircleDynamicResponse(BookCircleDynamic bookCircleDynamic, UserToken userToken) {
        //dynamic
        this.dynamicId = bookCircleDynamic.getDynamicId();
        this.content = bookCircleDynamic.getContent();
        this.image = Utils.getUrl(bookCircleDynamic.getImage());
        this.location = bookCircleDynamic.getLocation();
        this.dynamicType = bookCircleDynamic.getDynamicType();
        this.goodNum = bookCircleDynamic.getGoodNum();
        this.replyNum = bookCircleDynamic.getReplyNum();
        this.createdTime = bookCircleDynamic.getCreatedTime();
        //dynamic user
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(bookCircleDynamic.getUserId());
        this.userId = userInfo.getUserId();
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.avatar = Utils.getUrl(userInfo.getAvatar());

        //link
        this.linkType = bookCircleDynamic.getLinkType();
        this.linkId = bookCircleDynamic.getLinkId();
        if (this.linkType == LinkType.Book.getType()) {
            Book book = DaoUtils.bookDao.findOne(linkId);
            if (book != null) {
                this.title = book.getTitle();
                this.subTitle = book.getSubTitle();
                this.author = book.getAuthor();
                this.bookCover = Utils.getUrl(book.getCover());
            } else {
                this.linkId = -1;
            }
        } else if (this.linkType == LinkType.BookSheet.getType()) {
            BookSheet bookSheet = DaoUtils.bookSheetDao.findOne(linkId);
            if (bookSheet != null) {
                this.name = bookSheet.getName();
                this.bookSheetCover = Utils.getUrl(bookSheet.getCover());
            } else {
                this.linkId = -1;
            }
        } else if (this.linkType == LinkType.Comment.getType()) {
            GroupComment groupComment = DaoUtils.groupCommentDao.findOne(linkId);
            if (groupComment != null) {
                this.commentId = groupComment.getCommentId();
                this.commentContent = groupComment.getContent();
                UserInfo commentUserInfo = DaoUtils.userInfoDao.findOne(groupComment.getUserId());
                if (commentUserInfo != null) {
                    this.commentUserId = commentUserInfo.getUserId();
                    this.commentNickname = UserUtils.getSafeNickname(commentUserInfo.getNickname());
                }
                BookGroup bookGroup = DaoUtils.bookGroupDao.findOne(groupComment.getCommunityId());
                if (bookGroup != null) {
                    this.communityId = groupComment.getCommunityId();
                    this.communityTitle = bookGroup.getTitle();
                    this.communitySubTitle = bookGroup.getSubTitle();
                    this.communityAuthor = bookGroup.getAuthor();
                }
            } else {
                this.linkId = -1;
            }
        }

        HashMap<Long, UserInfo> userMap = new HashMap<Long, UserInfo>();
        List<BookCircleReply> bookCircleReplyList = DaoUtils.bookCircleReplyDao.findByDynamicIdOrderByCreatedTimeAsc(bookCircleDynamic.getDynamicId());
        for (BookCircleReply bookCircleReply : bookCircleReplyList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("replyId", bookCircleReply.getReplyId());
            jsonObject.put("replyType", bookCircleReply.getReplyType());
            jsonObject.put("content", bookCircleReply.getContent());
            jsonObject.put("createdTime", bookCircleReply.getCreatedTime());
            UserInfo replyUserInfo = DaoUtils.userInfoDao.findOne(bookCircleReply.getUserId());
            userMap.put(bookCircleReply.getReplyId(), replyUserInfo);
            jsonObject.put("userId", replyUserInfo.getUserId());
            jsonObject.put("nickname", UserUtils.getSafeNickname(replyUserInfo.getNickname()));
            jsonObject.put("avatar", Utils.getUrl(replyUserInfo.getAvatar()));
            if (bookCircleReply.getReplyType() == ReplyType.Reply.getType()) {
                UserInfo replyObjUserInfo = userMap.get(bookCircleReply.getReplyObjId());
                JSONObject replyObj = new JSONObject();
                BookCircleReply replyObjReply = DaoUtils.bookCircleReplyDao.findOne(bookCircleReply.getReplyObjId());
                replyObj.put("replyId", replyObjReply.getReplyId());
                replyObj.put("content", replyObjReply.getContent());
                replyObj.put("userId", replyObjUserInfo.getUserId());
                replyObj.put("nickname", UserUtils.getSafeNickname(replyObjUserInfo.getNickname()));

                jsonObject.put("replyObj", replyObj);
            }
            this.dynamicReplyList.add(jsonObject);
        }

        if (userToken != null) {
            isGood = UserGoodUtils.isUserGood(bookCircleDynamic.getDynamicId(), userToken.getUserId(), GoodType.Dynamic.getType());
        }
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDynamicType() {
        return dynamicType;
    }

    public void setDynamicType(int dynamicType) {
        this.dynamicType = dynamicType;
    }

    public long getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(long goodNum) {
        this.goodNum = goodNum;
    }

    public long getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(long replyNum) {
        this.replyNum = replyNum;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
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

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBookSheetCover() {
        return bookSheetCover;
    }

    public void setBookSheetCover(String bookSheetCover) {
        this.bookSheetCover = bookSheetCover;
    }

    public long getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(long commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getCommentNickname() {
        return commentNickname;
    }

    public void setCommentNickname(String commentNickname) {
        this.commentNickname = commentNickname;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityTitle() {
        return communityTitle;
    }

    public void setCommunityTitle(String communityTitle) {
        this.communityTitle = communityTitle;
    }

    public String getCommunitySubTitle() {
        return communitySubTitle;
    }

    public void setCommunitySubTitle(String communitySubTitle) {
        this.communitySubTitle = communitySubTitle;
    }

    public String getCommunityAuthor() {
        return communityAuthor;
    }

    public void setCommunityAuthor(String communityAuthor) {
        this.communityAuthor = communityAuthor;
    }

    public JSONArray getDynamicReplyList() {
        return dynamicReplyList;
    }

    public void setDynamicReplyList(JSONArray dynamicReplyList) {
        this.dynamicReplyList = dynamicReplyList;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
}
