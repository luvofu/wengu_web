package com.wg.notebook.model.response;

import com.wg.common.utils.ImageUtils;
import com.wg.notebook.domain.Note;
import com.wg.notebook.domain.Storyline;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/5.
 */
public class NotebookStorylineResponse {
    private long storylineId;
    private String node;
    private String story;
    private String characters;
    private String places;
    private Date updatedTime;

    public NotebookStorylineResponse(Storyline storyline) {
        this.storylineId = storyline.getStorylineId();
        this.node = storyline.getNode();
        this.story=storyline.getStory();
        this.characters = storyline.getCharacters();
        this.places = storyline.getPlaces();
        this.updatedTime = storyline.getUpdatedTime();
    }

    public long getStorylineId() {
        return storylineId;
    }

    public void setStorylineId(long storylineId) {
        this.storylineId = storylineId;
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

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
