package com.wg.notebook.model.request;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/12/8 0008.
 */
public class StorylineRequest {
    private String token;
    private long notebookId = -1;
    private long storylineId = -1;
    private String node;
    private String story;
    private String characters;
    private String places;

    private int page=0;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
