package com.wg.userbook.model.response;

import com.wg.book.domain.Book;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;
import com.wg.userbook.domain.UserBook;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午6:41
 * To change this template use File | Settings | File Templates.
 */
public class UserBookInfoResponse {
    private long userBookId;

    private Long userId;
    private String avatar;
    private String nickname;
    private Double longitude;
    private Double latitude;
    private String address;

    private long bookId;
    private String cover;
    private String title;
    private String author;
    private int totalPage;//总页码
    private Boolean isLease;//可租借状态
    private Boolean isSale;//可出售状态

    public UserBookInfoResponse(UserBook userBook) {
        this.userBookId = userBook.getUserBookId();
        Book book = DaoUtils.bookDao.findOne(userBook.getBookId());
        if (book != null) {
            this.bookId = book.getBookId();
            this.cover = Utils.getUrl(book.getCover());
            this.title = book.getTitle();
            this.totalPage = Utils.getNumber(book.getPages());
            this.isLease = userBook.isLease();
            this.isSale = userBook.isSale();
        }
    }

    public UserBookInfoResponse(UserBook userBook, UserInfo userInfo) {
        this.userBookId = userBook.getUserBookId();
        if (userInfo != null) {
            this.userId = userInfo.getUserId();
            this.avatar = Utils.getUrl(userInfo.getAvatar());
            this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
            this.longitude = userInfo.getLongitude();
            this.latitude = userInfo.getLatitude();
            this.address = UserUtils.combinAddress(userInfo, false);
        }

        Book book = DaoUtils.bookDao.findOne(userBook.getBookId());
        if (book != null) {
            this.bookId = book.getBookId();
            this.cover = Utils.getUrl(book.getCover());
            this.title = book.getTitle();
            this.author = book.getAuthor();
            this.totalPage = Utils.getNumber(book.getPages());
            this.isLease = userBook.isLease();
            this.isSale = userBook.isSale();
        }
    }

    public long getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(long userBookId) {
        this.userBookId = userBookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public Boolean getLease() {
        return isLease;
    }

    public void setLease(Boolean lease) {
        isLease = lease;
    }

    public Boolean getSale() {
        return isSale;
    }

    public void setSale(Boolean sale) {
        isSale = sale;
    }
}
