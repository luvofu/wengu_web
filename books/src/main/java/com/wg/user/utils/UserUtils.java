package com.wg.user.utils;

import com.wg.common.Constant;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.user.ConcernStatus;
import com.wg.common.Enum.user.ThirdType;
import com.wg.common.Enum.user.UseCondition;
import com.wg.common.model.NearbyRange;
import com.wg.common.utils.ImageUtils;
import com.wg.common.utils.LngLatUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.message.utils.IMUtils;
import com.wg.message.utils.MsgPushUtils;
import com.wg.user.domain.*;
import com.wg.user.model.request.UserRequest;
import com.wg.user.model.response.UserEntityResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/9/20.
 */
public class UserUtils {
    public static final int TOKEN_VALID_DAY = 15;
    public static final long OCR_DAY_NUM = 3;
    public static final long EXPORT_MOTH_NUM = 5;
    public static final String WENYA_PY = "wenya";
    public static final String WENYA_CN = "文芽";
    public static final String WENYA_MASK = "**";
    public static final String AUTOGRAPHY = "这人很懒,什么都没留下！";


    //判断手机号
    public static boolean isPhone(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            return Pattern.matches("^1[0-9]{10}$", phone);
        } else {
            return false;
        }
    }

    //判断邮箱
    public static boolean isEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            return Pattern.matches("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$", email);
        } else {
            return false;
        }
    }

    //注册
    public static UserLogin regist(String regMobile, String password) {
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(getUniqueNickname(regMobile));
        userInfo.setAvatar(Constant.USER_AVATAR_DEFAULT);
        userInfo.setBackground(Constant.USER_BACKGROUND_DEFAULT);
        userInfo.setAutograph(AUTOGRAPHY);
        userInfo = UpdateUtils.updateUserInfo(userInfo);
        UserLogin userLogin = new UserLogin();
        userLogin.setUserId(userInfo.getUserId());
        userLogin.setRegMobile(regMobile);
        userLogin.setPassword(password);
        userLogin = userLoginDao.save(userLogin);
        return userLogin;
    }

    //do something when user regist
    public static void registDo(final long userId) {
        final UserInfo userInfo = userInfoDao.findOne(userId);
        //add user account
        AddUtils.addUserAccount(userId);
        //sign im
        new Thread() {
            @Override
            public void run() {
                IMUtils.sign(userInfo);
            }
        }.start();
    }

    //登录
    public static UserEntityResponse login(UserLogin userLogin, UserRequest userRequest) {
        //更新token和过期时间
        UserToken userToken = updateToken(userLogin.getUserId(), userRequest);
        //绑定平台消息推送别名
        MsgPushUtils.updateAliasBind(userRequest.getClientId(), userToken.getUserId(), userToken.getPlatform());
        return new UserEntityResponse(userToken, userLogin, userInfoDao.findOne(userLogin.getUserId()));
    }

    //屏蔽昵称中 文芽 wenya
    public static String maskNickname(String nickname) {
        if (nickname != null) {
            return nickname.replaceAll(WENYA_PY, WENYA_MASK).replaceAll(WENYA_CN, WENYA_MASK);
        }
        return "";
    }

    //获取唯一昵称
    public static String getUniqueNickname(String nickname) {
        nickname = maskNickname(nickname);
        if (userInfoDao.findByNickname(nickname) == null) {
            return nickname;
        } else {
            int tryNum = 1;
            String newNickname = nickname + tryNum;
            while (userInfoDao.findByNickname(newNickname) != null) {
                tryNum++;
                newNickname = nickname + tryNum;
            }
            return newNickname;
        }
    }

    //保存第三方头像
    public static UserInfo setThirdAvatar(UserInfo userInfo, String avatar) {
        if (avatar != null) {
            String path = ImageUtils.saveImage(Constant.USER_AVATAR_FOLDER, userInfo.getUserId(), avatar);
            if (path != null) {
                userInfo.setAvatar(path);
            } else {
                userInfo.setAvatar(Constant.USER_AVATAR_DEFAULT);
            }
            userInfo = UpdateUtils.updateUserInfo(userInfo);
        }
        return userInfo;
    }

    //更新login第三方信息（uid、nickname）
    public static UserLogin updateThirdInfo(UserLogin userLogin, int thirdType, String uid, String nickname) {
        if (userLogin != null) {
            if (thirdType == ThirdType.Weixin.getType()) {
                userLogin.setWeixinId(uid);
                userLogin.setWeixinNickname(nickname);
            } else if (thirdType == ThirdType.Weibo.getType()) {
                userLogin.setWeiboId(uid);
                userLogin.setWeiboNickname(nickname);
            }
            userLogin = userLoginDao.save(userLogin);
        }
        return userLogin;
    }

    //获取token过期时间
    public static Date getTokenExpireDate() {
        return TimeUtils.getModifyDate(new Date(), TOKEN_VALID_DAY, null, null, null);
    }

    //更新token和时间
    public static UserToken updateToken(long userId, UserRequest userRequest) {
        UserToken userToken = userTokenDao.findByUserIdAndPlatform(userId, userRequest.getPlatform());
        if (userToken == null) {
            userToken = new UserToken();
            userToken.setUserId(userId);
            userToken.setPlatform(userRequest.getPlatform());
        }
        userToken.setToken(Utils.getUuidString());
        userToken.setExpireTime(UserUtils.getTokenExpireDate());
        userToken.setDeviceToken(userRequest.getDeviceToken());
        userToken.setVersion(userRequest.getVersion());
        userToken = userTokenDao.save(userToken);
        return userToken;
    }

    //删除用户所有token
    public static void disableAllToken(long userId) {
        List<UserToken> userTokenList = userTokenDao.findByUserId(userId);
        for (UserToken userToken : userTokenList) {
            DeleteUtils.deleteUserToken(userToken);
        }
    }

    //获取某天零点时间
    public static Date getDayZeroTime(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day.getTime();
    }

    //获取某月零点时间
    public static Date getMothZeroTime(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.DAY_OF_MONTH, 0);
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day.getTime();
    }

    //获取安全昵称
    public static String getSafeNickname(String nickname) {
        if (isPhone(nickname)) {
            return nickname.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        } else {
            return nickname;
        }
    }

    //发送验证码
    public static ResponseCode sendValicode(String deviceToken, String regMobile, UseCondition useCondition) {
        Valid valid = validDao.findByDeviceTokenAndRegMobileAndUseCondition(deviceToken, regMobile, useCondition.getCondition());
        if (valid != null) {
            if (valid.getUpdatedTime().before(UserUtils.getDayZeroTime(new Date()))) {
                valid.setRestSendNum(ValidUtils.VALID_REQUEST_NUM);
            }
        } else {
            valid = AddUtils.addValid(deviceToken, regMobile, useCondition.getCondition());
        }
        if (valid.getRestSendNum() > 0) {
            String validcode = ValidUtils.sendValidcode(regMobile);
            if (validcode != null) {
                valid.setValidcode(validcode);
                valid.setRestSendNum(valid.getRestSendNum() - 1);
                valid = validDao.save(valid);
                return ResponseCode.SUCCESS;
            } else {
                return ResponseCode.SEND_VALIDCODE_FAIL;
            }
        } else {
            return ResponseCode.EMPTY_SEND_VALIDCODE_NUM;
        }
    }

    //获取第三方uid
    public static String getThirdUid(UserLogin userLogin, int thirdType) {
        if (thirdType == ThirdType.Weixin.getType()) {
            return userLogin.getWeixinId();
        } else if (thirdType == ThirdType.Weibo.getType()) {
            return userLogin.getWeiboId();
        }
        return null;
    }

    //第三方uid获取login
    public static UserLogin getUserLoginByThird(String uid, int thirdType) {
        if (thirdType == ThirdType.Weixin.getType()) {
            return userLoginDao.findByWeixinId(uid);
        } else if (thirdType == ThirdType.Weibo.getType()) {
            return userLoginDao.findByWeiboId(uid);
        }
        return null;
    }

    public static Integer getConcernStatus(UserToken userToken, long userId) {
        if (userToken != null && userToken.getUserId() != userId) {
            UserFriend userFriend = userFriendDao.findByUserIdAndFriendId(userToken.getUserId(), userId);
            UserFriend friendUser = userFriendDao.findByUserIdAndFriendId(userId, userToken.getUserId());
            if (userFriend == null) {
                return friendUser == null ? ConcernStatus.NOEachConcern.getStatus() : ConcernStatus.SingleBeConcerned.getStatus();
            } else {
                return friendUser == null ? ConcernStatus.SingleConcern.getStatus() : ConcernStatus.MutualConcern.getStatus();
            }
        }
        return ConcernStatus.NOEachConcern.getStatus();
    }

    //用户距离
    public static double getDistance(UserToken userToken, UserInfo userInfo) {
        UserInfo user = userInfoDao.findOne(userToken.getUserId());
        return LngLatUtils.getDistance(user.getLongitude(), user.getLatitude(), userInfo.getLongitude(), userInfo.getLatitude());
    }

    //附近的人
    public static List<UserInfo> getNearbyUser(double longitude, double latitude, int page) {
        NearbyRange range = LngLatUtils.getNearbyRange(longitude, latitude, LngLatUtils.NEARBY_RADIUS);
        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        List objectArray = userInfoDao.findNearbyUsers(
                range.getLongitude(), range.getLatitude(), range.getFromLng(), range.getToLng(), range.getFromLat(), range.getToLat(), pageable);
        for (int index = 0; index < objectArray.size(); index++) {
            Object[] objects = (Object[]) objectArray.get(index);
            userInfoList.add((UserInfo) objects[0]);
        }
        return userInfoList;
    }

    //连接地址
    public static String combinAddress(UserInfo userInfo, boolean isFullAdd) {
        String address = "";
        if (isFullAdd) {
            if (StringUtils.isNotBlank(userInfo.getProvince()) && !userInfo.getProvince().equals(userInfo.getCity())) {
                address += userInfo.getProvince();
            }
            if (StringUtils.isNotBlank(userInfo.getCity())) {
                address += userInfo.getCity();
            }
        }
        if (StringUtils.isNotBlank(userInfo.getCounty())) {
            address += userInfo.getCounty();
        }
        if (StringUtils.isNotBlank(userInfo.getStreet())) {
            address += userInfo.getStreet();
        }
        return address;
    }


    public static long consumeOrc(UserInfo userInfo, Date consumeDate) {
        long restOrcNum = userInfo.getOcrRecogNum() - 1;
        userInfo.setOcrRecogNum(restOrcNum > 0 ? restOrcNum : 0);
        userInfo.setOcrRecogTime(consumeDate);
        userInfo = UpdateUtils.updateUserInfo(userInfo);
        return userInfo.getOcrRecogNum();
    }

    public static long consumeExport(UserInfo userInfo, Date consumeDate) {
        long restExportNum = userInfo.getExportNum() - 1;
        userInfo.setExportNum(restExportNum > 0 ? restExportNum : 0);
        userInfo.setExportTime(consumeDate);
        userInfo = UpdateUtils.updateUserInfo(userInfo);
        return userInfo.getExportNum();
    }

    //update reset data if need
    public static UserInfo updateReset(UserInfo userInfo) {
        Date currDate = TimeUtils.getCurrentDate();
        Date dayZeroTime = getDayZeroTime(currDate);
        Date mothZeroTime = getMothZeroTime(currDate);
        boolean update = false;
        if (userInfo.getOcrRecogTime() == null || userInfo.getOcrRecogTime().before(dayZeroTime)) {
            userInfo.setOcrRecogNum(OCR_DAY_NUM);
            userInfo.setOcrRecogTime(currDate);
            update = true;
        }
        if (userInfo.getExportTime() == null || userInfo.getExportTime().before(mothZeroTime)) {
            userInfo.setExportNum(EXPORT_MOTH_NUM);
            userInfo.setExportTime(currDate);
            update = true;
        }
        if (update) {
            userInfo = UpdateUtils.updateUserInfo(userInfo);
        }
        return userInfo;
    }
}
