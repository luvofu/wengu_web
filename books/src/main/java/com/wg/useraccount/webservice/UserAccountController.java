package com.wg.useraccount.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.common.Constant;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.useraccount.DealStatus;
import com.wg.common.Enum.useraccount.PayType;
import com.wg.common.ResponseContent;
import com.wg.common.utils.DecimalUtils;
import com.wg.user.domain.UserToken;
import com.wg.useraccount.domain.ThirdPay;
import com.wg.useraccount.domain.UserAccount;
import com.wg.useraccount.domain.UserBill;
import com.wg.useraccount.model.request.UserAccountRequest;
import com.wg.useraccount.model.response.BookOrderBillDetailResponse;
import com.wg.useraccount.model.response.BookOrderBillResponse;
import com.wg.useraccount.model.response.UserAccountChargeResponse;
import com.wg.useraccount.utils.AlipayUtils;
import com.wg.useraccount.utils.UserAccUtils;
import com.wg.useraccount.utils.WexinPayUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2017/3/12 0012.
 */
@Controller
public class UserAccountController {

    //个人账单
    @Transactional
    @RequestMapping(value = "/api/userAccount/bill", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String bill(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, UserAccountRequest userAccountRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userAccountRequest.getToken());
        List<BookOrderBillResponse> bookOrderBillResponseList = new ArrayList<BookOrderBillResponse>();
        Slice<UserBill> billSlice = userBillDao.findByUserIdAndIsShowOrderByCreatedTimeDesc(
                userToken.getUserId(), true, new PageRequest(userAccountRequest.getPage(), Constant.PAGE_NUM_LARGE));
        for (UserBill userBill : billSlice.getContent()) {
            bookOrderBillResponseList.add(new BookOrderBillResponse(userBill));
        }
        responseContent.putData("billList", bookOrderBillResponseList);
        return JSON.toJSONString(responseContent);
    }

    //账单详情
    @Transactional
    @RequestMapping(value = "/api/userAccount/billDetail", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String billDetail(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, UserAccountRequest userAccountRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userAccountRequest.getToken());
        long billId = userAccountRequest.getBillId();
        UserBill userBill = userBillDao.findOne(billId);
        if (userBill != null) {
            if (userToken.getUserId() == userBill.getUserId()) {
                responseContent.putData("billDetail", new BookOrderBillDetailResponse(userBill));
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //充值下单（支付宝、微信）
    @Transactional
    @RequestMapping(value = "/api/userAccount/charge", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String charge(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, UserAccountRequest userAccountRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userAccountRequest.getToken());
        int payType = userAccountRequest.getPayType();
        BigDecimal billGold = DecimalUtils.newDecimal(userAccountRequest.getBillGold());
        if (DecimalUtils.greaterThanZero(billGold) && (payType == PayType.Alipay.getType() || payType == PayType.Weixin.getType())) {
            UserBill userBill = UserAccUtils.chargePlaceBill(userToken, payType, billGold);
            if (userBill != null) {
                if (payType == PayType.Alipay.getType()) {
                    String orderString = AlipayUtils.charge(userBill);
                    responseContent.setData(new UserAccountChargeResponse(userBill, orderString));
                } else if (payType == PayType.Weixin.getType()) {
                    Map<String, String> map = WexinPayUtils.charge(userBill);
                    if (map != null) {
                        responseContent.setData(new UserAccountChargeResponse(userBill, map));
                    } else {
                        responseContent.update(ResponseCode.PLACE_ORDER_FAILED);
                    }
                }
            } else {
                responseContent.update(ResponseCode.CHARGE_PLACE_BILL_FAILED);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //充值同步验证（支付宝、微信）
    @Transactional
    @RequestMapping(value = "/api/userAccount/vertifyPaySyn", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String validPay(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, UserAccountRequest userAccountRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userAccountRequest.getToken());
        UserBill userBill = userBillDao.findOne(userAccountRequest.getBillId());
        String orderString = userAccountRequest.getOrderString();
        int payType = userBill.getPayType();
        if (userBill != null && userToken.getUserId() == userBill.getUserId()) {
            boolean paySuccess = false;
            if (payType == PayType.Alipay.getType()) {
                paySuccess = AlipayUtils.vertifyPaySyn(userBill, orderString);
            } else if (payType == PayType.Weixin.getType()) {
                paySuccess = WexinPayUtils.vertifyPaySyn(userBill);
            }
            if (paySuccess) {
                userBill.setShow(true);
                userBill = userBillDao.save(userBill);
                responseContent.putData("paySuccess", true);
            } else {
                responseContent.putData("paySuccess", false);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //充值异步验证（支付宝）
    @Transactional
    @RequestMapping(value = "/api/userAccount/notifyAliPay", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String notifyAliPay(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        Map<String, String> map = AlipayUtils.getNotifyRequestParam(request);
        if (map.containsKey("out_trade_no")) {
            UserBill userBill = userBillDao.findByTradeNumber(map.get("out_trade_no"));
            if (userBill != null && userBill.getDealStatus() != DealStatus.Success.getStatus() && userBill.getSerialNumber() == null) {
                if (AlipayUtils.vertifyPayAsyn(userBill, map)) {
                    AlipayUtils.dealChargeBill(userBill, map);
                }
            }
        }
        return "success";
    }

    //充值异步验证（微信）
    @Transactional
    @RequestMapping(value = "/api/userAccount/notifyWexinPay", produces = "application/xml"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String notifyWeChatPay(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        Map<String, String> map = WexinPayUtils.getNotifyRequestParam(request);
        if (map != null && map.containsKey("out_trade_no")) {
            UserBill userBill = userBillDao.findByTradeNumber(map.get("out_trade_no"));
            if (userBill != null && userBill.getDealStatus() != DealStatus.Success.getStatus()
                    && userBill.getDealTime() == null && userBill.getSerialNumber() == null) {
                if (WexinPayUtils.vertifyPayAsyn(userBill, map)) {
                    WexinPayUtils.dealChargeBill(userBill, map);
                }
            }
        }
        return WexinPayUtils.returnXML(WexinPayUtils.NOTIFY_SUCCESS);
    }

    //提现下单
    @Transactional
    @RequestMapping(value = "/api/userAccount/withdraw", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String withdraw(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, UserAccountRequest userAccountRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userAccountRequest.getToken());
        BigDecimal billGold = DecimalUtils.newDecimal(userAccountRequest.getBillGold());
        ThirdPay thirdPay = thirdPayDao.findOne(userAccountRequest.getThirdId());
        if (DecimalUtils.greaterThanZero(billGold) && thirdPay != null
                && (thirdPay.getPayType() == PayType.Alipay.getType() && billGold.compareTo(DecimalUtils.newDecimal(0.10)) > -1)
                || thirdPay.getPayType() == PayType.Weixin.getType() && billGold.compareTo(DecimalUtils.newDecimal(1.00)) > -1) {
            if (thirdPay.getUserId() == userToken.getUserId()) {
                UserAccount userAccount = userAccountDao.findByUserId(userToken.getUserId());
                if (userAccount != null && userAccount.getTotalGold().subtract(userAccount.getFrozenGold()).compareTo(billGold) > -1) {
                    UserBill userBill = UserAccUtils.withdrawPlaceBill(userToken, thirdPay, billGold);
                    if (userBill != null) {
                        responseContent.putData("billId", userBill.getBillId());
                    } else {
                        responseContent.update(ResponseCode.WITHDRAW_PLACE_BILL_FAILED);
                    }
                } else {
                    responseContent.update(ResponseCode.REMAINGOLD_NOT_ENOUGH);
                }
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }
}
