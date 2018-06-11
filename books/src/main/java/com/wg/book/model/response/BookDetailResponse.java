package com.wg.book.model.response;

import com.wg.book.domain.Book;
import com.wg.common.Enum.common.CollectType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserCollectionUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午2:24
 * To change this template use File | Settings | File Templates.
 */
public class BookDetailResponse {
    private long bookId;
    private long communityId;
    private String title;
    private String subTitle;
    private String author;
    private float rating = 0;
    private String tag;
    private String category;
    private String summary;
    private String price;
    private String cover;
    private String pages;
    private String authorInfo;
    private String translator;
    private String publisher;
    private String pubDate;
    private long collectionNum = 0;
    private String isbn10;
    private String isbn13;
    private String binding;
    private Date updatedTime;

    private boolean isCollect = false;

    private long remarkNum;

    public BookDetailResponse(Book book, UserToken userToken) {
        this.bookId = book.getBookId();
        this.communityId = book.getCommunityId();
        this.title = book.getTitle();
        this.subTitle = book.getSubTitle();
        this.author = book.getAuthor();
        this.rating = book.getRating();
        this.tag = book.getTag();
        this.category = book.getCategory();
        this.summary = book.getSummary();
        this.price = book.getPrice();
        this.cover = Utils.getUrl(book.getCover());

        this.pages = book.getPages();
        this.authorInfo = book.getAuthorInfo();
        this.translator = book.getTranslator();
        this.publisher = book.getPublisher();
        this.pubDate = book.getPubDate();
        this.collectionNum = book.getCollectionNum();
        this.isbn10 = book.getIsbn10();
        this.isbn13 = book.getIsbn13();
        this.binding = book.getBinding();
        this.updatedTime = book.getUpdatedTime();

        if (userToken != null) {
            isCollect = UserCollectionUtils.isUserCollect(book.getBookId(), userToken.getUserId(), CollectType.Book.getType());
        }

        this.remarkNum = DaoUtils.bookRemarkDao.countByBookId(book.getBookId());
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public long getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(long collectionNum) {
        this.collectionNum = collectionNum;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
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

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public long getRemarkNum() {
        return remarkNum;
    }

    public void setRemarkNum(long remarkNum) {
        this.remarkNum = remarkNum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
