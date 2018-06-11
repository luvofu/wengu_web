package com.wg.user.model.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.common.Enum.user.ReadingLvl;
import com.wg.common.utils.DecimalUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;
import com.wg.userbook.model.CategoryStatis;
import com.wg.userbook.utils.UserBookUtils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.wg.common.utils.dbutils.DaoUtils.userInfoDao;

/**
 * Created by Administrator on 2017/4/26 0026.
 */
public class ReadingReportResponse {
    private long userId;//用户id
    private String nickname;//用户
    private long bookNum;
    private long categoryNum;
    private int level;//星级
    private String quality;//质量
    private String appraise;//评价
    private JSONArray statis = new JSONArray();

    public ReadingReportResponse(long userId, int n) {
        UserInfo userInfo = userInfoDao.findOne(userId);
        if (userInfo != null) {
            this.userId = userInfo.getUserId();
            this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
            this.bookNum = userInfo.getBookNum();
            List<CategoryStatis> categoryStatises = UserBookUtils.getNormalCategoryStatis(userId);
            this.categoryNum = categoryStatises.size();
            ReadingLvl rl = ReadingLvl.getLvl(bookNum, categoryNum);
            this.level = rl.getLeavel();
            this.quality = rl.getQuality();
            this.appraise = rl.getAppraise();
            double rPer = 1.0;
            int s = n == 2 ? 100 : 1;
            double t = n == 2 ? 0.01 : 0.001;
            Map<String, Double> map = new LinkedHashMap<String, Double>();
            for (CategoryStatis cs : categoryStatises) {
                double cPer = DecimalUtils.doubleFormat(cs.getStatis() * 1.0 / bookNum, n, BigDecimal.ROUND_DOWN);
                if (cPer > t) {
                    map.put(cs.getCategory(), cPer);
                    rPer -= cPer;
                }
            }
            for (String key : map.keySet()) {
                JSONObject object = new JSONObject();
                object.put("category", key);
                object.put("percent", (map.get(key) + rPer) * s);
                rPer = 0;
                statis.add(object);
            }
        }
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

    public long getBookNum() {
        return bookNum;
    }

    public void setBookNum(long bookNum) {
        this.bookNum = bookNum;
    }

    public long getCategoryNum() {
        return categoryNum;
    }

    public void setCategoryNum(long categoryNum) {
        this.categoryNum = categoryNum;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getAppraise() {
        return appraise;
    }

    public void setAppraise(String appraise) {
        this.appraise = appraise;
    }

    public JSONArray getStatis() {
        return statis;
    }

    public void setStatis(JSONArray statis) {
        this.statis = statis;
    }
}
