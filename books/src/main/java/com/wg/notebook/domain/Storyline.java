package com.wg.notebook.domain;

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/8 0008.
 */
@Cacheable
@Entity
@Table(name = "storyline", indexes = {@Index(name = "key_notebookId", columnList = "notebookId")})
public class Storyline {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long storylineId;
    private long notebookId;
    //节点
    private String node;
    //情节
    private String story;
    //角色
    private String characters;
    //地点
    private String places;
    private double sort;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notebookId", insertable = false, updatable = false)
    private Notebook notebook;

    public long getStorylineId() {
        return storylineId;
    }

    public void setStorylineId(long storylineId) {
        this.storylineId = storylineId;
    }

    public long getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public double getSort() {
        return sort;
    }

    public void setSort(double sort) {
        this.sort = sort;
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

    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }
}
