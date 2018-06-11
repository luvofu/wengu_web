package com.wg.community.model.response;

import com.wg.book.domain.Book;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.community.domain.Community;
import com.wg.user.domain.UserToken;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class CommBookInfoResponse {
    private long communityId;
    private long bookId;
    private String title;
    private String cover;
    private boolean isShare = false;

    public CommBookInfoResponse(Community community, Book book, UserToken userToken) {
        this.communityId = community.getCommunityId();
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.cover = Utils.getUrl(book.getCover());
        if (userToken != null) {
            this.isShare = DaoUtils.commBookDao.findByCommunityIdAndUserIdAndBookId(
                    community.getCommunityId(), userToken.getUserId(), book.getBookId()) != null;
        }
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean share) {
        isShare = share;
    }
}
