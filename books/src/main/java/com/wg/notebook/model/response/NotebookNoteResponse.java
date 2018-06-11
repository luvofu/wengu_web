package com.wg.notebook.model.response;

import com.wg.common.utils.Utils;
import com.wg.notebook.domain.Note;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/5.
 */
public class NotebookNoteResponse {
    private long noteId;
    private String chapter;
    private int pages;
    private String otherLocation;
    private String image;
    private String content;
    private String originText;
    private Date updatedTime;

    public NotebookNoteResponse(Note note) {
        this.noteId = note.getNoteId();
        this.chapter = note.getChapter();
        this.pages = note.getPages();
        this.otherLocation = note.getOtherLocation();
        this.image = Utils.getUrl(note.getImage());
        this.content = note.getContent();
        this.originText = note.getOriginText();
        this.updatedTime = note.getUpdatedTime();
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
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
}
