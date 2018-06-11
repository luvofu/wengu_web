package com.wg.booksheet.domain;

import com.wg.book.domain.Book;
import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//书单书籍表
@Cacheable
@Entity
@Table(name = "booksheet_book",
        uniqueConstraints = {@UniqueConstraint(name = "ukey_sid_bid", columnNames = {"sheetId", "bookId"})},
        indexes = {@Index(name = "key_bookId", columnList = "bookId")})
public class BookSheetBook {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long sheetBookId;
    private long sheetId;
    private long bookId;
    @Column(length = 10000)
    private String recommend;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheetId", insertable = false, updatable = false)
    private BookSheet bookSheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", insertable = false, updatable = false)
    private Book book;

    public long getSheetBookId() {
        return sheetBookId;
    }

    public void setSheetBookId(long sheetBookId) {
        this.sheetBookId = sheetBookId;
    }

    public long getSheetId() {
        return sheetId;
    }

    public void setSheetId(long sheetId) {
        this.sheetId = sheetId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
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

    public BookSheet getBookSheet() {
        return bookSheet;
    }

    public void setBookSheet(BookSheet bookSheet) {
        this.bookSheet = bookSheet;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
