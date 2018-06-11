package com.wg.user.model.response;

import com.wg.common.utils.Utils;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserUtils;

/**
 * Created by Administrator on 2016/9/17.
 */
public class UserProfileResponse {
    private long userId;
    private String avatar;
    private String nickname;
    private String background;
    private String autograph;
    private String province;
    private String city;
    private String county;
    private String street;
    private String birthday;
    private int sex;

    private int concernStatus;

    private long concernNum;
    private long fanNum;
    private long bookNum;
    private long leaseNum;
    private long saleNum;
    private long notebookNum;
    private long bookSheetNum;
    private double longitude;
    private double latitude;

    public UserProfileResponse(UserToken userToken, UserInfo userInfo) {
        this.userId = userInfo.getUserId();
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.sex = userInfo.getSex();
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.background = Utils.getUrl(userInfo.getBackground());
        this.autograph = userInfo.getAutograph();
        this.province = userInfo.getProvince();
        this.city = userInfo.getCity();
        this.county = userInfo.getCounty();
        this.street = userInfo.getStreet();
        this.birthday = userInfo.getBirthday();
        this.concernNum = userInfo.getConcernNum();
        this.fanNum = userInfo.getFanNum();
        this.concernStatus = UserUtils.getConcernStatus(userToken, userInfo.getUserId());
        this.bookNum = userInfo.getBookNum();
        this.leaseNum = userInfo.getLeaseNum();
        this.saleNum = userInfo.getSaleNum();
        this.notebookNum = userInfo.getNotebookNum();
        this.bookSheetNum = userInfo.getBookSheetNum();
        this.longitude = userInfo.getLongitude();
        this.latitude = userInfo.getLatitude();
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

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getAutograph() {
        return autograph;
    }

    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getConcernStatus() {
        return concernStatus;
    }

    public void setConcernStatus(int concernStatus) {
        this.concernStatus = concernStatus;
    }

    public long getConcernNum() {
        return concernNum;
    }

    public void setConcernNum(long concernNum) {
        this.concernNum = concernNum;
    }

    public long getFanNum() {
        return fanNum;
    }

    public void setFanNum(long fanNum) {
        this.fanNum = fanNum;
    }

    public long getBookNum() {
        return bookNum;
    }

    public void setBookNum(long bookNum) {
        this.bookNum = bookNum;
    }

    public long getLeaseNum() {
        return leaseNum;
    }

    public void setLeaseNum(long leaseNum) {
        this.leaseNum = leaseNum;
    }

    public long getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(long saleNum) {
        this.saleNum = saleNum;
    }

    public long getNotebookNum() {
        return notebookNum;
    }

    public void setNotebookNum(long notebookNum) {
        this.notebookNum = notebookNum;
    }

    public long getBookSheetNum() {
        return bookSheetNum;
    }

    public void setBookSheetNum(long bookSheetNum) {
        this.bookSheetNum = bookSheetNum;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
