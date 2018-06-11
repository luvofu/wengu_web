package com.wg.user.domain;

import com.wg.common.utils.TimeUtils;
import com.wg.user.utils.UserUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/2/2016.
 */
//用户资料表
@Cacheable
@Entity
@Table(name = "user_info", indexes = {
        @Index(name = "ukey_nickname", columnList = "nickname", unique = true),
        @Index(name = "key_lng_lat", columnList = "longitude,latitude"),
        @Index(name = "key_updatedTime", columnList = "updatedTime")})
public class UserInfo {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long userId;
    private String mailbox;
    private String nickname;
    private int sex = 2;
    private String avatar;
    private String background;
    @Column(length = 10000)
    private String autograph;
    private String country;
    private String province;
    private String city;
    private String county;
    private String street;
    private String birthday;
    private String tag;
    private long concernNum;
    private long fanNum;
    private long bookNum;
    private long leaseNum;
    private long saleNum;
    private long notebookNum;
    private long bookSheetNum;
    private long ocrRecogNum = UserUtils.OCR_DAY_NUM;
    private Date ocrRecogTime;
    private long exportNum = UserUtils.EXPORT_MOTH_NUM;
    private Date exportTime;
    private double longitude;
    private double latitude;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public long getOcrRecogNum() {
        return ocrRecogNum;
    }

    public void setOcrRecogNum(long ocrRecogNum) {
        this.ocrRecogNum = ocrRecogNum;
    }

    public Date getOcrRecogTime() {
        return ocrRecogTime;
    }

    public void setOcrRecogTime(Date ocrRecogTime) {
        this.ocrRecogTime = ocrRecogTime;
    }

    public long getExportNum() {
        return exportNum;
    }

    public void setExportNum(long exportNum) {
        this.exportNum = exportNum;
    }

    public Date getExportTime() {
        return exportTime;
    }

    public void setExportTime(Date exportTime) {
        this.exportTime = exportTime;
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

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }
}
