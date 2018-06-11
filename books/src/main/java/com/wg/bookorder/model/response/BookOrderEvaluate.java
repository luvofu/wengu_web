package com.wg.bookorder.model.response;

import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.bookorder.domain.OrderEvaluate;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/2/16 0016.
 */
public class BookOrderEvaluate {
    long evaluateId;//评价id
    long userId;//用户id
    String avatar;//头像
    String nickname;//昵称
    String content;//评价
    double rating;//评分
    Date createdTime;//创建时间

    public BookOrderEvaluate(OrderEvaluate orderEvaluate) {
        this.evaluateId = orderEvaluate.getEvaluateId();
        UserInfo userInfo = DaoUtils.userInfoDao.findOne(orderEvaluate.getUserId());
        this.userId = userInfo.getUserId();
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.content = orderEvaluate.getContent();
        this.rating = orderEvaluate.getRating();
        this.createdTime = orderEvaluate.getCreatedTime();
    }

    public long getEvaluateId() {
        return evaluateId;
    }

    public void setEvaluateId(long evaluateId) {
        this.evaluateId = evaluateId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
