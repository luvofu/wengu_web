package com.wg.useraccount.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.useraccount.PayType;
import com.wg.common.ResponseContent;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.user.domain.UserToken;
import com.wg.useraccount.domain.ThirdPay;
import com.wg.useraccount.model.request.ThirdPayRequest;
import com.wg.useraccount.model.response.ThirdPayResponse;
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

import static com.wg.common.utils.dbutils.DaoUtils.thirdPayDao;
import static com.wg.common.utils.dbutils.DaoUtils.userTokenDao;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
@Controller
public class ThirdPayController {

    //添加第三方支付账户
    @Transactional
    @RequestMapping(value = "api/userAccount/addThirdPay", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String addThirdPay(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap, ThirdPayRequest thirdPayRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(thirdPayRequest.getToken());
        int payType = thirdPayRequest.getPayType();
        String account = thirdPayRequest.getAccount();
        String name = thirdPayRequest.getName();
        String nickname = thirdPayRequest.getNickname();
        if ((payType == PayType.Alipay.getType() || payType == PayType.Weixin.getType())
                && StringUtils.isNotBlank(account) && StringUtils.isNotBlank(name)) {
            AddUtils.addThirdPay(userToken.getUserId(), payType, account, name, nickname);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //编辑第三方账户支付账户
    @Transactional
    @RequestMapping(value = "api/userAccount/editThirdPay", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String editThirdPay(HttpServletRequest request,
                               HttpServletResponse response, ModelMap modelMap, ThirdPayRequest thirdPayRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(thirdPayRequest.getToken());
        ThirdPay thirdPay = thirdPayDao.findOne(thirdPayRequest.getThirdId());
        int payType = thirdPayRequest.getPayType();
        String account = thirdPayRequest.getAccount();
        String name = thirdPayRequest.getName();
        String nickname = thirdPayRequest.getNickname();
        if (thirdPay != null) {
            if (userToken.getUserId() == thirdPay.getUserId()) {
                if (payType == PayType.Alipay.getType() || payType == PayType.Weixin.getType()) {
                    thirdPay.setPayType(payType);
                }
                if (StringUtils.isNotBlank(account)) {
                    thirdPay.setAccount(account);
                }
                if (StringUtils.isNotBlank(name)) {
                    thirdPay.setName(name);
                }
                if (StringUtils.isNotBlank(nickname)) {
                    thirdPay.setNickname(nickname);
                }
                thirdPay = thirdPayDao.save(thirdPay);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }


    //删除第三方账户支付账户
    @Transactional
    @RequestMapping(value = "api/userAccount/deleteThirdPay", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String deleteThirdPay(HttpServletRequest request,
                                 HttpServletResponse response, ModelMap modelMap, ThirdPayRequest thirdPayRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(thirdPayRequest.getToken());
        ThirdPay thirdPay = thirdPayDao.findOne(thirdPayRequest.getThirdId());
        if (thirdPay != null) {
            if (userToken.getUserId() == thirdPay.getUserId()) {
                thirdPayDao.delete(thirdPay);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        }
        return JSON.toJSONString(responseContent);
    }


    //用户第三方支付账户信息
    @Transactional
    @RequestMapping(value = "api/userAccount/userThirdPay", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String userThirdPay(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, ThirdPayRequest thirdPayRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(thirdPayRequest.getToken());
        List<ThirdPay> thirdPayList = thirdPayDao.findByUserId(userToken.getUserId());
        List<ThirdPayResponse> thirdPayResponseList = new ArrayList<ThirdPayResponse>();
        for (ThirdPay thirdPay : thirdPayList) {
            thirdPayResponseList.add(new ThirdPayResponse(thirdPay));
        }
        responseContent.putData("thirdPayList", thirdPayResponseList);
        return JSON.toJSONString(responseContent);
    }
}
