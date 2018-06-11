package com.wg.user.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.user.ThirdType;
import com.wg.common.Enum.user.UseCondition;
import com.wg.common.ResponseContent;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.message.utils.IMUtils;
import com.wg.solr.modle.QueryRes;
import com.wg.solr.utils.UserInfoSolr;
import com.wg.user.domain.*;
import com.wg.user.model.request.UserRequest;
import com.wg.user.model.response.NewEntityResponse;
import com.wg.user.model.response.ReadingReportResponse;
import com.wg.user.model.response.UserEntityResponse;
import com.wg.user.model.response.UserProfileResponse;
import com.wg.user.utils.UserUtils;
import com.wg.user.utils.ValidUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;


/**
 * Created by wzhonggo on 8/4/2016.
 */
@Controller
public class UserController {

    @Transactional
    @RequestMapping(value = "/api/user/search", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String search(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        String keyword = userRequest.getKeyword();
        if (keyword != null) {
            List<UserProfileResponse> UserProfileResponseList = new ArrayList<UserProfileResponse>();
            long total = 0;
            UserLogin userLogin = null;
            if (UserUtils.isPhone(userRequest.getKeyword())) {
                userLogin = userLoginDao.findByRegMobile(userRequest.getKeyword());
                if (userLogin != null) {
                    UserProfileResponseList.add(new UserProfileResponse(userToken, userInfoDao.findOne(userLogin.getUserId())));
                    total += 1;
                }
            }
            QueryRes<UserInfo> queryRes = UserInfoSolr.findByNicknameContains(userRequest.getKeyword(), userRequest.getPage());
            for (UserInfo userInfo : queryRes.getDocList()) {
                if (userLogin == null || userLogin.getUserId() != userInfo.getUserId()) {
                    UserInfo ui = userInfoDao.findOne(userInfo.getUserId());
                    if (ui != null) {
                        UserProfileResponseList.add(new UserProfileResponse(userToken, ui));
                    }
                } else {
                    total -= 1;
                }
            }
            responseContent.putData("total", total + queryRes.getTotalRes());
            responseContent.putData("userList", UserProfileResponseList);
        } else {
            responseContent.update(ResponseCode.NULL_KEYWORD);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 用户注册,登录
     */
    @Transactional
    @RequestMapping(value = "/api/user/signup", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String signup(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        String regMobile = userRequest.getRegMobile();
        String password = userRequest.getPassword();
        if (UserUtils.isPhone(regMobile) && StringUtils.isNotBlank(password)) {
            UserLogin userLogin = userLoginDao.findByRegMobile(regMobile);
            if (userLogin == null) {
                if (ValidUtils.checkValidcode(
                        userRequest.getDeviceToken(), regMobile, UseCondition.Signup.getCondition(), userRequest.getValidcode())) {
                    userLogin = UserUtils.regist(regMobile, password);
                    //by regist
                    UserUtils.registDo(userLogin.getUserId());
                    UserEntityResponse userEntityResponse = UserUtils.login(userLogin, userRequest);
                    responseContent.setData(userEntityResponse);
                } else {
                    responseContent.update(ResponseCode.ERROR_VALIDCODE);
                }
            } else {
                responseContent.update(ResponseCode.REGED_MOBILE);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 登录
     */
    @Transactional
    @RequestMapping(value = "/api/user/login", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String login(HttpServletRequest request,
                        HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        String userName = userRequest.getUserName();
        String password = userRequest.getPassword();
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            UserLogin userLogin;
            if (UserUtils.isPhone(userName)) {
                userLogin = userLoginDao.findByRegMobile(userName);
            } else {
                userLogin = userLoginDao.findByUserName(userName);
            }
            if (userLogin != null && password.equals(userLogin.getPassword())) {
                UserEntityResponse userEntityResponse = UserUtils.login(userLogin, userRequest);
                responseContent.setData(userEntityResponse);
            } else {
                responseContent.update(ResponseCode.ERROR_LOGIN);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 第三方登录
     */
    @Transactional
    @RequestMapping(value = "/api/user/thirdPlatLogin", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String register(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        int thirdType = userRequest.getThirdType();
        String uid = userRequest.getUid();
        String nickname = userRequest.getNickname();
        if (StringUtils.isNotBlank(uid)) {
            UserLogin userLogin = null;
            if (thirdType == ThirdType.Weixin.getType()) {
                userLogin = userLoginDao.findByWeixinId(uid);
            } else if (thirdType == ThirdType.Weibo.getType()) {
                userLogin = userLoginDao.findByWeiboId(uid);
            }
            if (userLogin == null) {
                userLogin = UserUtils.regist(nickname, null);
                UserInfo userInfo = userInfoDao.findOne(userLogin.getUserId());
                userInfo.setSex(userRequest.getSex());
                userInfo.setAutograph(userRequest.getAutograph());
                userInfo.setBirthday(userRequest.getBirthday());
                userInfo = UserUtils.setThirdAvatar(userInfo, userRequest.getAvatar());
                //by regist
                UserUtils.registDo(userLogin.getUserId());
            }
            userLogin = UserUtils.updateThirdInfo(userLogin, thirdType, uid, nickname);

            UserEntityResponse userEntityResponse = UserUtils.login(userLogin, userRequest);
            responseContent.setData(userEntityResponse);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }


    /**
     * 绑定第三方
     */
    @Transactional
    @RequestMapping(value = "/api/user/bindThird", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bindThrid(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        int thirdType = userRequest.getThirdType();
        String uid = userRequest.getUid();
        String nickname = userRequest.getNickname();
        if ((thirdType == ThirdType.Weixin.getType() || thirdType == ThirdType.Weibo.getType()) && StringUtils.isNotBlank(uid)) {
            if (thirdType == ThirdType.Weixin.getType() && userLoginDao.findByWeixinId(uid) == null
                    || thirdType == ThirdType.Weibo.getType() && userLoginDao.findByWeiboId(uid) == null) {
                UserLogin userLogin = userLoginDao.findByUserId(userToken.getUserId());
                UserUtils.updateThirdInfo(userLogin, thirdType, uid, nickname);
                UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
                UserEntityResponse userEntityResponse = new UserEntityResponse(userToken, userLogin, userInfo);
                responseContent.setData(userEntityResponse);
            } else {
                responseContent.update(ResponseCode.BINDED_THIRD_ACC);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 解绑第三方
     */
    @Transactional
    @RequestMapping(value = "/api/user/unbindThird", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String unbindThird(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        int thirdType = userRequest.getThirdType();
        String uid = userRequest.getUid();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        UserLogin userLogin = userLoginDao.findByUserId(userToken.getUserId());
        UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
        if (thirdType == ThirdType.Weixin.getType() && uid.equals(userLogin.getWeixinId())
                || thirdType == ThirdType.Weibo.getType() && uid.equals(userLogin.getWeiboId())) {
            if (StringUtils.isNotBlank(userLogin.getRegMobile()) || StringUtils.isNotBlank(userLogin.getUserName())) {
                if (thirdType == ThirdType.Weixin.getType()) {
                    userLogin.setWeixinId(null);
                    userLogin.setWeixinNickname(null);
                } else {
                    userLogin.setWeiboId(null);
                    userLogin.setWeiboNickname(null);
                }
                userLogin = userLoginDao.save(userLogin);

                UserEntityResponse userEntityResponse = new UserEntityResponse(userToken, userLogin, userInfo);
                responseContent.setData(userEntityResponse);
            } else {
                responseContent.update(ResponseCode.NOT_SET_ACC);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 自动登录
     */
    @Transactional
    @RequestMapping(value = "/api/user/autoLogin", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String autoLogin(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        if (userToken.getExpireTime().after(TimeUtils.getCurrentDate())) {
            UserLogin userLogin = userLoginDao.findByUserId(userToken.getUserId());
            userToken.setVersion(userRequest.getVersion());
            userToken.setUpdatedTime(TimeUtils.getCurrentDate());
            userToken = userTokenDao.save(userToken);
            responseContent.setData(new UserEntityResponse(userToken, userLogin, userLogin.getUserInfo()));
        } else {
            DeleteUtils.deleteUserToken(userToken);
            responseContent.update(ResponseCode.EXPIRETIME_TOKEN);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 请求验证码
     */
    @Transactional
    @RequestMapping(value = "/api/user/sendValidcode", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String sendValidcode(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        String token = userRequest.getToken();
        String regMobile = userRequest.getRegMobile();
        int useCondition = userRequest.getUseCondition();
        int thirdType = userRequest.getThirdType();
        UserToken userToken = userTokenDao.findByToken(token);
        UserLogin userLogin = userLoginDao.findByRegMobile(regMobile);
        if (useCondition == UseCondition.ThirdBindLogin.getCondition() && (thirdType == ThirdType.Weixin.getType() || thirdType == ThirdType.Weibo.getType())) {
            if (userLogin == null || UserUtils.getThirdUid(userLogin, thirdType) == null) {
                responseContent.update(UserUtils.sendValicode(userRequest.getDeviceToken(), regMobile, UseCondition.ThirdBindLogin));
            } else {
                responseContent.update(ResponseCode.MOBILE_BIND_THIRD);
            }
        } else if (useCondition == UseCondition.Signup.getCondition()) {
            if (userLogin == null) {
                responseContent.update(UserUtils.sendValicode(userRequest.getDeviceToken(), regMobile, UseCondition.Signup));
            } else {
                responseContent.update(ResponseCode.REGED_MOBILE);
            }
        } else if (useCondition == UseCondition.ForgetPsw.getCondition()) {
            if (userLogin != null) {
                responseContent.update(UserUtils.sendValicode(userRequest.getDeviceToken(), regMobile, UseCondition.ForgetPsw));
            } else {
                responseContent.update(ResponseCode.NOT_REGED_MOBILE);
            }
        } else if (useCondition == UseCondition.CheckMobile.getCondition() && userToken != null) {
            if (userLogin != null) {
                responseContent.update(UserUtils.sendValicode(userRequest.getDeviceToken(), regMobile, UseCondition.CheckMobile));
            } else {
                responseContent.update(ResponseCode.NOT_BIND_MOBILE);
            }
        } else if (useCondition == UseCondition.BindMobile.getCondition() && userToken != null) {
            if (userLogin == null) {
                responseContent.update(UserUtils.sendValicode(userRequest.getDeviceToken(), regMobile, UseCondition.BindMobile));
            } else {
                responseContent.update(ResponseCode.BINDED_MOBILE);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 设置用户名
     */
    @Transactional
    @RequestMapping(value = "/api/user/setUserName", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String setUserName(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        String userName = userRequest.getUserName();
        if (StringUtils.isNotBlank(userName)) {
            userName = userName.trim();
            UserLogin userLogin = userLoginDao.findByUserId(userToken.getUserId());
            UserLogin tempUserLogin = userLoginDao.findByUserName(userName);
            if (tempUserLogin == null && userLogin != null) {
                userLogin.setUserName(userName);
                userLoginDao.save(userLogin);
                UserInfo userInfo = userInfoDao.findOne(userLogin.getUserId());
                UserEntityResponse userEntityResponse = new UserEntityResponse(userToken, userLogin, userInfo);
                responseContent.setData(userEntityResponse);
            } else {
                responseContent.update(ResponseCode.EXIST_ACC);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 设置密码
     */
    @Transactional
    @RequestMapping(value = "/api/user/setpsw", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String setpsw(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());//用户令牌信息
        if (userToken != null && StringUtils.isNotBlank(userRequest.getPassword())) {
            UserLogin userLogin = userLoginDao.findByUserId(userToken.getUserId());//用户登录信息
            if (userLogin.getPassword() == null) {
                userLogin.setPassword(userRequest.getPassword());//设置密码
                userLoginDao.save(userLogin);
                UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());//用户资料信息

                UserEntityResponse userEntityResponse = new UserEntityResponse(userToken, userLogin, userInfo);
                responseContent.setData(userEntityResponse);
            } else {
                responseContent.update(ResponseCode.ALREADY_SET_PSW);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 设置密码
     */
    @Transactional
    @RequestMapping(value = "/api/user/resetpsw", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String resetpsw(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        UserLogin userLogin = userLoginDao.findByUserId(userToken.getUserId());
        String password = userLogin.getPassword();
        String currPassword = userRequest.getPassword();
        String newPassword = userRequest.getNewPassword();
        if (StringUtils.isNotBlank(newPassword)) {
            if (StringUtils.isNotBlank(password) && !password.equals(currPassword)) {
                responseContent.update(ResponseCode.ERROR_ORIGIN_PSW);
            } else if (StringUtils.isNotBlank(password) && newPassword.equals(currPassword)) {
                responseContent.update(ResponseCode.EQUAL_ORIGIN_PSW);
            } else {
                userLogin.setPassword(newPassword);
                userLogin = userLoginDao.save(userLogin);
                UserUtils.disableAllToken(userLogin.getUserId());
                UserEntityResponse userEntityResponse = UserUtils.login(userLogin, userRequest);
                responseContent.setData(userEntityResponse);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 忘记密码,修改密码登陆
     */
    @Transactional
    @RequestMapping(value = "/api/user/forgotpsw", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String forgotpsw(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        String regMobile = userRequest.getRegMobile();
        String validcode = userRequest.getValidcode();
        String password = userRequest.getPassword();
        if (StringUtils.isNotBlank(regMobile) && StringUtils.isNotBlank(validcode) && StringUtils.isNotBlank(password)) {
            UserLogin userLogin = userLoginDao.findByRegMobile(regMobile);
            if (userLogin == null) {
                responseContent.update(ResponseCode.ACC_NOT_EXIST);
            } else if (ValidUtils.checkValidcode(userRequest.getDeviceToken(), regMobile, UseCondition.ForgetPsw.getCondition(), validcode)) {
                userLogin.setPassword(password);
                userLogin = userLoginDao.save(userLogin);
                UserInfo userInfo = userInfoDao.findOne(userLogin.getUserId());
                UserUtils.disableAllToken(userInfo.getUserId());
                UserEntityResponse userEntityResponse = UserUtils.login(userLogin, userRequest);
                responseContent.setData(userEntityResponse);
            } else {
                responseContent.update(ResponseCode.ERROR_VALIDCODE);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 设置用户资料
     */
    @Transactional
    @RequestMapping(value = "/api/user/editProfile", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String editProfile(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        UserLogin userLogin = userLoginDao.findByUserId(userToken.getUserId());
        String nickname = userRequest.getNickname();
        UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
        boolean nicknameOK = true;
        if (StringUtils.isNotBlank(nickname)) {
            nickname = nickname.trim();
            if (nickname.equals(UserUtils.maskNickname(nickname))) {
                if (userInfoDao.findByNickname(nickname) == null) {
                    userInfo.setNickname(nickname);
                    IMUtils.update(userInfo);
                } else if (!nickname.equals(userInfo.getNickname())) {
                    nicknameOK = false;
                    responseContent.update(ResponseCode.EXIST_NICKNAME);
                }
            } else {
                nicknameOK = false;
                responseContent.update(ResponseCode.NK_CONTAIN_WENYA);
            }
        }
        if (nicknameOK) {
            if (userRequest.getTag() != null) {
                userInfo.setTag(userRequest.getTag().trim());
            }
            if (StringUtils.isNotBlank(userRequest.getAutograph())) {
                userInfo.setAutograph(userRequest.getAutograph().trim());
            }
            if (userRequest.getSex() >= 0) {
                userInfo.setSex(userRequest.getSex());
            }
            if (userRequest.getBirthday() != null) {
                userInfo.setBirthday(userRequest.getBirthday());
            }
            if (userRequest.getMailbox() != null) {
                userInfo.setMailbox(userRequest.getMailbox().trim());
            }
            if (userRequest.getCountry() != null) {
                userInfo.setCountry(userRequest.getCountry().trim());
            }
            if (userRequest.getProvince() != null) {
                userInfo.setProvince(userRequest.getProvince().trim());
            }
            if (userRequest.getCity() != null) {
                userInfo.setCity(userRequest.getCity().trim());
            }
            if (userRequest.getCounty() != null) {
                userInfo.setCounty(userRequest.getCounty().trim());
            }
            if (userRequest.getStreet() != null) {
                userInfo.setStreet(userRequest.getStreet());
            }
            if (userRequest.getLongitude() >= 0 && userRequest.getLatitude() >= 0) {
                userInfo.setLongitude(userRequest.getLongitude());
                userInfo.setLatitude(userRequest.getLatitude());
            }

            userInfo = UpdateUtils.updateUserInfo(userInfo);

            UserEntityResponse userEntityResponse = new UserEntityResponse(userToken, userLogin, userInfo);
            responseContent.setData(userEntityResponse);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 绑定手机号
     */
    @Transactional
    @RequestMapping(value = "/api/user/bindMobilephone", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bindMobilephone(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        String token = userRequest.getToken();
        String regMobile = userRequest.getRegMobile();
        if (StringUtils.isNotBlank(token)) {
            UserToken userToken = userTokenDao.findByToken(token);
            UserLogin userLogin = userLoginDao.findByUserId(userToken.getUserId());
            UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
            UserLogin tempUserLogin = userLoginDao.findByRegMobile(regMobile);
            if (tempUserLogin != null) {
                responseContent.update(ResponseCode.BINDED_MOBILE);
            } else if (!ValidUtils.checkValidcode(userRequest.getDeviceToken(), regMobile, UseCondition.BindMobile.getCondition(), userRequest.getValidcode())) {
                responseContent.update(ResponseCode.ERROR_VALIDCODE);
            } else {
                userLogin.setRegMobile(regMobile);
                userLogin = userLoginDao.save(userLogin);

                UserEntityResponse userEntityResponse = new UserEntityResponse(userToken, userLogin, userInfo);
                responseContent.setData(userEntityResponse);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 换绑手机号
     */
    @Transactional
    @RequestMapping(value = "/api/user/changeMobilephone", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String changeMobilephone(HttpServletRequest request,
                                    HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        String token = userRequest.getToken();
        String newRegMobile = userRequest.getNewRegMobile();
        UserToken userToken = userTokenDao.findByToken(token);
        if (userToken != null && StringUtils.isNotBlank(token) && StringUtils.isNotBlank(newRegMobile)) {
            if (ValidUtils.checkValidcode(userRequest.getDeviceToken(), newRegMobile, UseCondition.BindMobile.getCondition(), userRequest.getValidcode())) {
                UserLogin userLogin = userLoginDao.findByRegMobile(newRegMobile);
                if (userLogin == null) {
                    userLogin = userLoginDao.findByUserId(userToken.getUserId());
                    userLogin.setRegMobile(newRegMobile);
                    userLogin = userLoginDao.save(userLogin);
                    UserInfo userInfo = userInfoDao.findOne(userLogin.getUserId());
                    UserEntityResponse userEntityResponse = new UserEntityResponse(userToken, userLogin, userInfo);
                    responseContent.setData(userEntityResponse);
                } else {
                    responseContent.update(ResponseCode.BINDED_MOBILE);
                }
            } else {
                responseContent.update(ResponseCode.ERROR_VALIDCODE);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 手机号验证
     */
    @Transactional
    @RequestMapping(value = "/api/user/checkMobilephone", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String checkMobilephone(HttpServletRequest request,
                                   HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        UserLogin userLogin = userLoginDao.findByUserId(userToken.getUserId());
        String regMobile = userRequest.getRegMobile();
        if (regMobile == null) {
            regMobile = userLogin.getRegMobile();
        }
        if (regMobile != null) {
            if (userLogin != null && ValidUtils.checkValidcode(
                    userRequest.getDeviceToken(), regMobile, UseCondition.CheckMobile.getCondition(), userRequest.getValidcode())) {
            } else {
                responseContent.update(ResponseCode.ERROR_VALIDCODE);
            }
        } else {
            responseContent.update(ResponseCode.NOT_BIND_MOBILE);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 用户资料
     */
    @Transactional
    @RequestMapping(value = "/api/user/profile", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String profile(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        UserInfo userInfo = userInfoDao.findOne(userRequest.getUserId());
        if (userInfo != null) {
            int permission = Utils.getPermission(userToken, userRequest.getUserId());
            UserProfileResponse userProfileResponse = new UserProfileResponse(userToken, userInfo);
            responseContent.putData("relationType", permission);
            responseContent.putData("userProfile", userProfileResponse);
        } else {
            responseContent.update(ResponseCode.USER_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 用户好友
     */
    @Transactional
    @RequestMapping(value = "/api/user/myFriends", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String myFriends(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        if (StringUtils.isNotBlank(userRequest.getToken())) {
            UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
            List<UserProfileResponse> friendResponseList = new ArrayList<UserProfileResponse>();
            List<UserFriend> userFriendList = userFriendDao.findByUserIdOrderByCreatedTimeDesc(userToken.getUserId());
            for (UserFriend userFriend : userFriendList) {
                UserInfo userInfo = userInfoDao.findOne(userFriend.getFriendId());
                friendResponseList.add(new UserProfileResponse(userToken, userInfo));
            }
            responseContent.setData(friendResponseList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 用户反馈
     */
    @Transactional
    @RequestMapping(value = "/api/user/feedback", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String feedback(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        Feedback feedback = new Feedback();
        feedback.setUserId(userToken.getUserId());
        feedback.setContent(userRequest.getContent());
        feedback.setConnection(userRequest.getConnection());
        feedback = feedbackDao.save(feedback);
        return JSON.toJSONString(responseContent);
    }

    /**
     * 用户未登录
     */
    @Transactional
    @RequestMapping(value = "/api/user/notlogin", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String notlogin(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        responseContent.update(ResponseCode.NOT_LOGIN);
        return JSON.toJSONString(responseContent);
    }

    /**
     * 账号异常
     */
    @Transactional
    @RequestMapping(value = "/api/user/exceptAcc", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String exceptAcc(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        responseContent.update(ResponseCode.EXCEPT_ACC);
        return JSON.toJSONString(responseContent);
    }

    /**
     * version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103
     **/

    @Transactional
    @RequestMapping(value = "/api/user/loginOut", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String loginOut(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        //delete current token
        DeleteUtils.deleteUserToken(userToken);
        return JSON.toJSONString(responseContent);
    }

    /**
     * 第三方登录
     **/
    @Transactional
    @RequestMapping(value = "/api/user/thirdLogin_v103", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String thirdLogin_v103(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        int thirdType = userRequest.getThirdType();
        String uid = userRequest.getUid();
        String nickname = userRequest.getNickname();
        if (StringUtils.isNotBlank(uid)) {
            UserLogin userLogin = null;
            if (thirdType == ThirdType.Weixin.getType()) {
                userLogin = userLoginDao.findByWeixinId(uid);
            } else if (thirdType == ThirdType.Weibo.getType()) {
                userLogin = userLoginDao.findByWeiboId(uid);
            }
            if (userLogin != null) {
                userLogin = UserUtils.updateThirdInfo(userLogin, thirdType, uid, nickname);
                UserEntityResponse userEntityResponse = UserUtils.login(userLogin, userRequest);
                responseContent.setData(userEntityResponse);
            } else {
                responseContent.update(ResponseCode.UNREG_THIRD_ACC);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * 第三方绑定登录（手机号注册过,则绑定第三方,否则,注册新账号并绑定第三方）
     **/
    @Transactional
    @RequestMapping(value = "/api/user/thirdBindLogin", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String thirdBindLogin(HttpServletRequest request,
                                 HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        String validcode = userRequest.getValidcode();
        int thirdType = userRequest.getThirdType();
        String uid = userRequest.getUid();
        String regMobile = userRequest.getRegMobile();
        String nickname = userRequest.getNickname();
        if (StringUtils.isNotBlank(regMobile) && StringUtils.isNotBlank(validcode) && StringUtils.isNotBlank(uid)
                && (thirdType == ThirdType.Weixin.getType() && userLoginDao.findByWeixinId(uid) == null
                || thirdType == ThirdType.Weibo.getType() && userLoginDao.findByWeiboId(uid) == null)) {
            if (ValidUtils.checkValidcode(userRequest.getDeviceToken(), regMobile, UseCondition.ThirdBindLogin.getCondition(), validcode)) {
                UserLogin userLogin = userLoginDao.findByRegMobile(regMobile);
                if (userLogin == null) {
                    userLogin = UserUtils.regist(regMobile, null);
                    UserInfo userInfo = userInfoDao.findOne(userLogin.getUserId());
                    if (StringUtils.isNotBlank(nickname) && !regMobile.equals(nickname)) {
                        userInfo.setNickname(UserUtils.getUniqueNickname(nickname));
                    }
                    userInfo.setSex(userRequest.getSex());
                    if (userRequest.getAutograph() != null) {
                        userInfo.setAutograph(userRequest.getAutograph());
                    }
                    userInfo.setBirthday(userRequest.getBirthday());
                    userInfo = UserUtils.setThirdAvatar(userInfo, userRequest.getAvatar());
                    //by regist
                    UserUtils.registDo(userLogin.getUserId());
                }
                if ((thirdType == ThirdType.Weixin.getType() && userLogin.getWeixinId() == null
                        || thirdType == ThirdType.Weibo.getType() && userLogin.getWeiboId() == null)) {
                    userLogin = UserUtils.updateThirdInfo(userLogin, thirdType, uid, nickname);
                    UserEntityResponse userEntityResponse = UserUtils.login(userLogin, userRequest);
                    responseContent.setData(userEntityResponse);
                } else {
                    responseContent.update(ResponseCode.ALREADY_BIND_THIRD);
                }
            } else {
                responseContent.update(ResponseCode.ERROR_VALIDCODE);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /* version 112 * version 112 * version 112 * version 112 * version 112 * version 112 * version 112 * version 112 * version 112 */
    @Transactional
    @RequestMapping(value = "/api/user/concern", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String concern(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
        long userId = userInfo.getUserId();
        long friendId = userRequest.getFriendId();
        if (friendId != -1) {
            if (userId != friendId) {
                UserFriend userFriend = userFriendDao.findByUserIdAndFriendId(userId, friendId);
                if (userFriend == null) {
                    AddUtils.addUserFriend(userId, friendId);
                } else {
                    DeleteUtils.deleteUserFriend(userFriend);
                }
                responseContent.putData("concernStatus", UserUtils.getConcernStatus(userToken, friendId));
                responseContent.putData("concernNum", userInfo.getConcernNum());
                responseContent.putData("fanNum", userInfo.getFanNum());
            } else {
                responseContent.update(ResponseCode.CONCERN_SELF);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/user/concerns", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String concerns(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        long userId = userRequest.getUserId();
        if (userId != -1) {
            List<UserFriend> userFriendList = userFriendDao.findByUserIdOrderByCreatedTimeDesc(userId);
            List<UserProfileResponse> myConcernList = new ArrayList<UserProfileResponse>();
            for (UserFriend userFriend : userFriendList) {
                UserInfo userInfo = userInfoDao.findOne(userFriend.getFriendId());
                myConcernList.add(new UserProfileResponse(userToken, userInfo));
            }
            responseContent.putData("concernList", myConcernList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/user/fans", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String fans(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        long userId = userRequest.getUserId();
        if (userId != -1) {
            List<UserProfileResponse> myFanList = new ArrayList<UserProfileResponse>();
            List<UserFriend> userFriendList = userFriendDao.findByFriendIdOrderByCreatedTimeDesc(userId);
            for (UserFriend userFriend : userFriendList) {
                UserInfo userInfo = userInfoDao.findOne(userFriend.getUserId());
                myFanList.add(new UserProfileResponse(userToken, userInfo));
            }
            responseContent.putData("fanList", myFanList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/user/imSign", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String imSign(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
        if (!IMUtils.sign(userInfo)) {
            responseContent.update(ResponseCode.IM_SIGN_FAIL);
        }
        return JSON.toJSONString(responseContent);
    }

    /* version 200 * version 200 * version 200 * version 200 * version 200 * version 200 * version 200 * version 200 * version 200 */

    //附近书友
    @Transactional
    @RequestMapping(value = "/api/user/nearby", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String nearby(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        double longitude = userRequest.getLongitude();
        double latitude = userRequest.getLatitude();
        List<UserProfileResponse> userList = new ArrayList<UserProfileResponse>();
        List<UserInfo> userInfoList = UserUtils.getNearbyUser(longitude, latitude, userRequest.getPage());
        for (UserInfo userInfo : userInfoList) {
            if (userToken == null || userToken.getUserId() != userInfo.getUserId()) {
                userList.add(new UserProfileResponse(userToken, userInfo));
            }
        }
        responseContent.putData("userList", userList);
        return JSON.toJSONString(responseContent);
    }

    //用户最新重要信息
    @Transactional
    @RequestMapping(value = "/api/user/newEntity", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String newEntity(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        responseContent.putData("newEntity", new NewEntityResponse(userToken));
        return JSON.toJSONString(responseContent);
    }

    //////////////////////////////////////////////////////version 202///////////////////////////////////////////////////////////////
    @Transactional
    @RequestMapping(value = "/api/user/consumeOcrNum", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String consumeOcrNum(HttpServletRequest request, HttpServletResponse response, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userRequest.getToken());
        UserInfo userInfo = UserUtils.updateReset(userToken.getUserInfo());
        responseContent.putData("ocrRecogNum", UserUtils.consumeOrc(userInfo, TimeUtils.getCurrentDate()));
        return JSON.toJSONString(responseContent);
    }

    //////////////////////////////////////////////////////version 203///////////////////////////////////////////////////////////////
    @Transactional
    @RequestMapping(value = "/api/user/readingReport", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String readingReport(HttpServletRequest request, HttpServletResponse response, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        long userId = userRequest.getUserId();
        if (userId != -1) {
            responseContent.putData("readingReport", new ReadingReportResponse(userId, 2));
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/user/readingReport_v204", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String readingReport_v204(HttpServletRequest request, HttpServletResponse response, UserRequest userRequest) {
        ResponseContent responseContent = new ResponseContent();
        long userId = userRequest.getUserId();
        if (userId != -1) {
            responseContent.putData("readingReport", new ReadingReportResponse(userId, 3));
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

}

