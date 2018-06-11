package com.wg.user.model.response;

import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserLogin;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserUtils;
import com.wg.useraccount.domain.UserAccount;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午7:00
 * To change this template use File | Settings | File Templates.
 */
public class UserEntityResponse {
    private long userId;
    private String avatar;
    private String nickname;
    private String background;
    private String autograph;
    private String country;
    private String province;
    private String city;
    private String county;
    private String street;
    private String birthday;
    private int sex;

    private String token;
    private String userName;
    private String regMobile;
    private String mailbox;
    private String weixinId;
    private String weixinNickname;
    private String weiboId;
    private String weiboNickname;

    private long concernNum;
    private long fanNum;
    private long bookNum;
    private long notebookNum;
    private long bookSheetNum;
    private long ocrRecogNum;
    private long exportNum;
    private double longitude;
    private double latitude;

    private double totalGold;
    private double frozenGold;

    public UserEntityResponse(UserToken userToken, UserLogin userLogin, UserInfo userInfo) {
        //update data like orcnum expnum
        userInfo = UserUtils.updateReset(userInfo);

        this.userId = userInfo.getUserId();
        this.mailbox = userInfo.getMailbox();
        this.nickname = userInfo.getNickname();

        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.background = Utils.getUrl(userInfo.getBackground());
        this.autograph = userInfo.getAutograph();
        this.country = userInfo.getCountry();
        this.province = userInfo.getProvince();
        this.city = userInfo.getCity();
        this.county = userInfo.getCounty();
        this.street = userInfo.getStreet();
        this.birthday = userInfo.getBirthday();
        this.sex = userInfo.getSex();

        this.token = userToken.getToken();
        this.userName = userLogin.getUserName();
        this.regMobile = userLogin.getRegMobile();
        this.weixinId = userLogin.getWeixinId();
        this.weixinNickname = userLogin.getWeixinNickname();
        this.weiboId = userLogin.getWeiboId();
        this.weiboNickname = userLogin.getWeiboNickname();

        this.concernNum = userInfo.getConcernNum();
        this.fanNum = userInfo.getFanNum();
        this.bookNum = userInfo.getBookNum();
        this.notebookNum = userInfo.getNotebookNum();
        this.bookSheetNum = userInfo.getBookSheetNum();
        this.ocrRecogNum = userInfo.getOcrRecogNum();
        this.exportNum = userInfo.getExportNum();
        this.longitude = userInfo.getLongitude();
        this.latitude = userInfo.getLatitude();

        UserAccount userAccount = DaoUtils.userAccountDao.findByUserId(userToken.getUserId());
        this.totalGold = userAccount.getTotalGold().doubleValue();
        this.frozenGold = userAccount.getFrozenGold().doubleValue();
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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRegMobile() {
        return regMobile;
    }

    public void setRegMobile(String regMobile) {
        this.regMobile = regMobile;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public String getWeixinId() {
        return weixinId;
    }

    public void setWeixinId(String weixinId) {
        this.weixinId = weixinId;
    }

    public String getWeixinNickname() {
        return weixinNickname;
    }

    public void setWeixinNickname(String weixinNickname) {
        this.weixinNickname = weixinNickname;
    }

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public String getWeiboNickname() {
        return weiboNickname;
    }

    public void setWeiboNickname(String weiboNickname) {
        this.weiboNickname = weiboNickname;
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

    public long getExportNum() {
        return exportNum;
    }

    public void setExportNum(long exportNum) {
        this.exportNum = exportNum;
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

    public double getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(double totalGold) {
        this.totalGold = totalGold;
    }

    public double getFrozenGold() {
        return frozenGold;
    }

    public void setFrozenGold(double frozenGold) {
        this.frozenGold = frozenGold;
    }
}
