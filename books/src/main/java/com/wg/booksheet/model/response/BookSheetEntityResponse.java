package com.wg.booksheet.model.response;

import com.wg.booksheet.domain.BookSheet;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class BookSheetEntityResponse {
    private long sheetId;
    private String name;
    private String cover;
    private long bookNum;
    private long collectionNum;
    private String description;

    private long userId;
    private String nickname;

    public BookSheetEntityResponse(BookSheet bookSheet) {
        this.sheetId = bookSheet.getSheetId();
        this.name = bookSheet.getName();
        this.cover = Utils.getUrl(bookSheet.getCover());
        this.bookNum = bookSheet.getBookNum();
        this.collectionNum = bookSheet.getCollectionNum();
        this.description = bookSheet.getDescription();
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(bookSheet.getUserId());
        this.userId = userInfo.getUserId();
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
    }

    public long getSheetId() {
        return sheetId;
    }

    public void setSheetId(long sheetId) {
        this.sheetId = sheetId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
