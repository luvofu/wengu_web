package com.wg.notebook.model.response;

import com.wg.book.domain.Book;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.notebook.domain.Notebook;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/5.
 */
public class NotebookDetailResponse {
    private long notebookId;
    private String title;
    private String name;
    private String cover;
    private long noteNum;
    private int permission;
    private Date createdTime;

    public NotebookDetailResponse(Notebook notebook) {
        this.notebookId = notebook.getNotebookId();
        this.name = notebook.getName();
        this.noteNum = notebook.getNoteNum();
        this.permission = notebook.getPermission();
        this.createdTime = notebook.getCreatedTime();
        Book book = DaoUtils.bookDao.findOne(notebook.getBookId());
        this.title = book.getTitle();
        this.cover = Utils.getUrl(book.getCover());
    }

    public long getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getNoteNum() {
        return noteNum;
    }

    public void setNoteNum(long noteNum) {
        this.noteNum = noteNum;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
