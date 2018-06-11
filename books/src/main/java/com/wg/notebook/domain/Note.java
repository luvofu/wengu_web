package com.wg.notebook.domain;

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
@Cacheable
@Entity
@Table(name = "note",indexes = { @Index(name = "key_notebookId" , columnList ="notebookId" )})
public class Note {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long noteId;
    private long notebookId;
    private String chapter;
    private int pages;
    private String otherLocation;
    private double sort;
    private String image;
    @Column(length = 10000)
    private String content;
    @Column(length = 10000)
    private String originText;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="notebookId",insertable = false, updatable = false)
    private Notebook notebook;

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public long getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getOtherLocation() {
        return otherLocation;
    }

    public void setOtherLocation(String otherLocation) {
        this.otherLocation = otherLocation;
    }

    public double getSort() {
        return sort;
    }

    public void setSort(double sort) {
        this.sort = sort;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOriginText() {
        return originText;
    }

    public void setOriginText(String originText) {
        this.originText = originText;
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

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }
}
