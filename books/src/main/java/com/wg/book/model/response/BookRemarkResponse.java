package com.wg.book.model.response;

import com.wg.book.domain.BookRemark;
import com.wg.common.utils.Utils;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/13.
 */
public class BookRemarkResponse {
        private long userId;
        private String nickname;
        private String avatar;
        private String remark;
        private Date createdTime;

        public BookRemarkResponse(BookRemark bookRemark, UserInfo userInfo) {
            this.userId = userInfo.getUserId();
            this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
            this.avatar = Utils.getUrl(userInfo.getAvatar());
            this.remark = bookRemark.getRemark();
            this.createdTime = bookRemark.getCreatedTime();
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
