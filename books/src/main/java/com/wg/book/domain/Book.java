package com.wg.book.domain;

/**
 * Created by wzhonggo on 8/4/2016.
 */

import com.wg.book.utils.BookUtils;
import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

//书籍表
@Cacheable
@Entity
@Table(name = "book", indexes = {
        @Index(name = "ukey_isbn10", columnList = "isbn10", unique = true),
        @Index(name = "ukey_isbn13", columnList = "isbn13", unique = true),
        @Index(name = "key_communityId", columnList = "communityId"),
        @Index(name = "key_updatedTime", columnList = "updatedTime"),
        @Index(name = "key_entryNum", columnList = "entryNum")})
public class Book {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long bookId;
    private long communityId;
    private String title;
    private String originTitle;
    private String subTitle;
    private String author;
    private float thirdRating = 0;
    private float rating = 0;
    @Column(length = 10000)
    private String tag;
    private String category = BookUtils.NULL_CATEGORY;
    @Column(length = 10000)
    private String summary;
    private String price;
    private String cover;
    @Column(length = 10000)
    private String catalog;
    private String pages;
    @Column(length = 10000)
    private String authorInfo;
    private String translator;
    private String publisher;
    private String pubDate;
    private long collectionNum = 0;
    private long entryNum = 0;
    private long ratingAllNum = 0;
    private String isbn10;
    private String isbn13;
    private String binding;
    private boolean isLocalCover = false;
    private int source;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

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

    public float getThirdRating() {
        return thirdRating;
    }

    public void setThirdRating(float thirdRating) {
        this.thirdRating = thirdRating;
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

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
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

    public long getEntryNum() {
        return entryNum;
    }

    public void setEntryNum(long entryNum) {
        this.entryNum = entryNum;
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

    public boolean isLocalCover() {
        return isLocalCover;
    }

    public void setLocalCover(boolean localCover) {
        isLocalCover = localCover;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getRatingAllNum() {
        return ratingAllNum;
    }

    public void setRatingAllNum(long ratingAllNum) {
        this.ratingAllNum = ratingAllNum;
    }
}
