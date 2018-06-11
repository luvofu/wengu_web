package com.wg.booksheet.model.request;

import com.wg.common.Enum.booksheet.SortType;

/**
 * Created by wzhonggo on 8/31/2016.
 */
public class BookSheetRequest {
    private long sheetId = -1;
    private long bookId = -1;
    private String name;
    private String token;
    private long userId = -1;
    private long sheetBookId = -1;
    private String description;
    private String tag;
    private int sortType = SortType.collectionNum.getType();
    private String filterType;
    private int page = 0;
    private String keyword;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getSheetBookId() {
        return sheetBookId;
    }

    public void setSheetBookId(long sheetBookId) {
        this.sheetBookId = sheetBookId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getSheetId() {
        return sheetId;
    }

    public void setSheetId(long sheetId) {
        this.sheetId = sheetId;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
