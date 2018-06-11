package com.wg.notebook.model.response;

import com.wg.book.domain.Book;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.notebook.domain.Notebook;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/5.
 */
public class NotebookPersonalResponse {
    private long notebookId;
    private int permission;
    private String name;
    private long noteNum;
    private long storylineNum;
    private Date createdTime;
    private String cover;
    private String title;
    private String author;
    private double rating;

    public NotebookPersonalResponse(Notebook notebook) {
        this.notebookId = notebook.getNotebookId();
        this.permission = notebook.getPermission();
        this.name = notebook.getName();
        this.noteNum = notebook.getNoteNum();
        this.storylineNum = notebook.getStorylineNum();
        this.createdTime = notebook.getCreatedTime();
        Book book = DaoUtils.bookDao.findOne(notebook.getBookId());
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.rating = book.getRating();
        this.cover = Utils.getUrl(book.getCover());
    }

    public long getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNoteNum() {
        return noteNum;
    }

    public void setNoteNum(long noteNum) {
        this.noteNum = noteNum;
    }

    public long getStorylineNum() {
        return storylineNum;
    }

    public void setStorylineNum(long storylineNum) {
        this.storylineNum = storylineNum;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
