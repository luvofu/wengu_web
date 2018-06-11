package com.wg.bookorder.model.response;

import com.wg.book.domain.Book;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;
import com.wg.userbook.domain.UserBook;

/**
 * Created by Administrator on 2017/2/16 0016.
 */
public class BookOrderTradeInfoResponse {
    long userId;//拥有人id
    String avatar;//头像
    String nickname;//昵称
    double longitude;//经度
    double latitude;//维度
    String address;//地址
    long userBookId;//藏书id
    long bookId;//书籍id
    String cover;//封面
    String title;//书名
    String author;//作者
    String translator;//译者
    String publisher;//出版社
    String pubDate;//出版日期
    double rating;//评分
    double evaluation;//估价
    double dayRentGold;//日租金
    boolean isLease;//可租
    boolean isSale;//可售
    String bookDescription;

    public BookOrderTradeInfoResponse(UserBook userBook) {
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(userBook.getUserId());
        this.userId = userInfo.getUserId();
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.longitude = userInfo.getLongitude();
        this.latitude = userInfo.getLatitude();
        this.address = UserUtils.combinAddress(userInfo, true);
        this.userBookId = userBook.getUserBookId();
        Book book = DaoUtils.bookDao.findOne(userBook.getBookId());
        this.bookId = book.getBookId();
        this.cover = Utils.getUrl(book.getCover());
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.translator = book.getTranslator();
        this.publisher = book.getTranslator();
        this.pubDate = book.getPubDate();
        this.rating = book.getRating();
        this.evaluation = userBook.getEvaluation();
        this.dayRentGold = userBook.getDayRentGold();
        this.isLease = userBook.isLease();
        this.isSale = userBook.isSale();
        this.bookDescription = userBook.getDescription();
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(long userBookId) {
        this.userBookId = userBookId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    public double getDayRentGold() {
        return dayRentGold;
    }

    public void setDayRentGold(double dayRentGold) {
        this.dayRentGold = dayRentGold;
    }

    public boolean isLease() {
        return isLease;
    }

    public void setLease(boolean lease) {
        isLease = lease;
    }

    public boolean isSale() {
        return isSale;
    }

    public void setSale(boolean sale) {
        isSale = sale;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }
}
