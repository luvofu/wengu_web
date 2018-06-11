package com.wg.book.domain;

import com.wg.common.Enum.book.DownStatus;
import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

//书籍封面记录表
@Cacheable
@Entity
@Table(name = "temp_book_cover", indexes = {
        @Index(name = "ukey_bookId", columnList = "bookId"),
        @Index(name = "key_createdTime", columnList = "createdTime")})
public class TempBookCover {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long tempBookCoverId;
    private long bookId;
    private int downStatus = DownStatus.NotDown.getStatus();
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", insertable = false, updatable = false)
    private Book book;

    public long getTempBookCoverId() {
        return tempBookCoverId;
    }

    public void setTempBookCoverId(long tempBookCoverId) {
        this.tempBookCoverId = tempBookCoverId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getDownStatus() {
        return downStatus;
    }

    public void setDownStatus(int downStatus) {
        this.downStatus = downStatus;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
