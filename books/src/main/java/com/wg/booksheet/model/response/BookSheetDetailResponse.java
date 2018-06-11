package com.wg.booksheet.model.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.book.domain.Book;
import com.wg.booksheet.domain.BookSheet;
import com.wg.booksheet.domain.BookSheetBook;
import com.wg.common.Enum.common.CollectType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserCollectionUtils;
import com.wg.user.utils.UserUtils;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午3:25
 * To change this template use File | Settings | File Templates.
 */
public class BookSheetDetailResponse {
    private long sheetId;
    private String cover;
    private String description;
    private String tag;
    private long bookNum;
    private long collectionNum;
    private Date createdTime;
    private Date updatedTime;

    private long userId;
    private String nickname;
    private String avatar;
    private String name;

    private JSONArray sheetBookList = new JSONArray();

    private boolean isCollect = false;

    public BookSheetDetailResponse(BookSheet bookSheet, UserToken userToken) {
        //booksheet
        this.sheetId = bookSheet.getSheetId();
        this.name = bookSheet.getName();
        this.cover = Utils.getUrl(bookSheet.getCover());
        this.description = bookSheet.getDescription();
        this.tag = bookSheet.getTag();
        this.bookNum = bookSheet.getBookNum();
        this.collectionNum = bookSheet.getCollectionNum();
        this.createdTime = bookSheet.getCreatedTime();
        this.updatedTime = bookSheet.getUpdatedTime();

        //booksheet user
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(bookSheet.getUserId());
        this.userId = userInfo.getUserId();
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.avatar = Utils.getUrl(userInfo.getAvatar());

        //sheetbook
        List<BookSheetBook> bookSheetBookList = DaoUtils.bookSheetBookDao.findBySheetIdOrderByCreatedTimeDesc(bookSheet.getSheetId());
        for (BookSheetBook bookSheetBook : bookSheetBookList) {
            Book book = DaoUtils.bookDao.findOne(bookSheetBook.getBookId());
            JSONObject json = new JSONObject();
            json.put("sheetBookId", bookSheetBook.getSheetBookId());
            json.put("recommend", bookSheetBook.getRecommend());
            json.put("bookId", book.getBookId());
            json.put("cover", Utils.getUrl(book.getCover()));
            json.put("title", book.getTitle());
            json.put("subTitle", book.getSubTitle());
            json.put("author", book.getAuthor());
            json.put("rating", book.getRating());
            sheetBookList.add(json);
        }

        if (userToken != null) {
            this.isCollect = UserCollectionUtils.isUserCollect(bookSheet.getSheetId(), userToken.getUserId(), CollectType.BookSheet.getType());
        }
    }

    public long getSheetId() {
        return sheetId;
    }

    public void setSheetId(long sheetId) {
        this.sheetId = sheetId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public long getBookNum() {
        return bookNum;
    }

    public void setBookNum(long bookNum) {
        this.bookNum = bookNum;
    }

    public long getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(long collectionNum) {
        this.collectionNum = collectionNum;
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

    public JSONArray getSheetBookList() {
        return sheetBookList;
    }

    public void setSheetBookList(JSONArray sheetBookList) {
        this.sheetBookList = sheetBookList;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }
}
