package com.wg.book.model.response;

import com.wg.book.domain.Book;
import com.wg.common.utils.Utils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午2:55
 * To change this template use File | Settings | File Templates.
 */
public class BookEntityResponse {
    private long bookId;
    private String title;
    private String author;
    private String isbn;
    private float rating;
    private String cover;
    private String translator;
    private String publisher;
    private String pubDate;
    private Date updatedTime;

    private Boolean contain;

    public BookEntityResponse(Book book) {
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.isbn = book.getIsbn13();
        this.rating = book.getRating();
        this.cover = Utils.getUrl(book.getCover());
        this.translator = book.getTranslator();
        this.publisher = book.getPublisher();
        this.pubDate = book.getPubDate();
    }

    public BookEntityResponse(Book book, boolean contain) {
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.isbn = book.getIsbn13();
        this.rating = book.getRating();
        this.cover = Utils.getUrl(book.getCover());
        this.translator = book.getTranslator();
        this.publisher = book.getPublisher();
        this.pubDate = book.getPubDate();
        this.contain = contain;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Boolean getContain() {
        return contain;
    }

    public void setContain(Boolean contain) {
        this.contain = contain;
    }
}
