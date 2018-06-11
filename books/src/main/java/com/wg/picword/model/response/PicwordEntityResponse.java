package com.wg.picword.model.response;

import com.wg.book.domain.Book;
import com.wg.common.Enum.common.CollectType;
import com.wg.common.Enum.common.GoodType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.picword.domain.Picword;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserCollectionUtils;
import com.wg.user.utils.UserGoodUtils;
import com.wg.user.utils.UserUtils;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/23 0023.
 */
public class PicwordEntityResponse {
    private long picwordId;
    private String image;
    private long goodNum;
    private long collectionNum;
    private Date createdTime;
    private long userId;
    private String avatar;
    private String nickname;
    private Long bookId;
    private String cover;
    private String title;
    private String author;
    private boolean isGood = false;
    private boolean isCollect = false;

    public PicwordEntityResponse(Picword picword, UserToken userToken) {
        this.picwordId = picword.getPicwordId();
        this.image = Utils.getUrl(picword.getImage());
        this.goodNum = picword.getGoodNum();
        this.collectionNum = picword.getCollectionNum();
        this.createdTime = picword.getCreatedTime();
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(picword.getUserId());
        this.userId = userInfo.getUserId();
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        Book book = DaoUtils.bookDao.findOne(picword.getBookId());
        if (book != null) {
            this.bookId = book.getBookId();
            this.cover = Utils.getUrl(book.getCover());
            this.title = book.getTitle();
            this.author = book.getAuthor();
        }
        if (userToken != null) {
            this.isGood = UserGoodUtils.isUserGood(picword.getPicwordId(), userToken.getUserId(), GoodType.Picword.getType());
            this.isCollect = UserCollectionUtils.isUserCollect(picword.getPicwordId(), userToken.getUserId(), CollectType.Picword.getType());
        }
    }

    public long getPicwordId() {
        return picwordId;
    }

    public void setPicwordId(long picwordId) {
        this.picwordId = picwordId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(long goodNum) {
        this.goodNum = goodNum;
    }

    public long getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(long collectionNum) {
        this.collectionNum = collectionNum;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }
}
