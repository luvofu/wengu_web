package com.wg.useraccount.utils;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.wg.common.Enum.useraccount.AccLogType;
import com.wg.common.Enum.useraccount.DealStatus;
import com.wg.common.Enum.useraccount.PayType;
import com.wg.common.utils.DecimalUtils;
import com.wg.common.utils.LogUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.useraccount.domain.UserBill;
import com.wg.useraccount.utils.alipay.AlipayConfig;
import com.wg.useraccount.utils.alipay.PaySynRes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.wg.common.utils.dbutils.DaoUtils.userBillDao;
import static com.wg.common.utils.Utils.logger;

/**
 * Created by Administrator on 2017/2/27 0027.
 */
public class AlipayUtils {
    public static AlipayClient alipayClient = new DefaultAlipayClient(
            AlipayConfig.ALIPAY_GATEWAY, AlipayConfig.APP_ID, AlipayConfig.PRIVATE_KEY, AlipayConfig.FORMAT,
            AlipayConfig.INPUT_CHARSET, AlipayConfig.PUBLIC_KEY, AlipayConfig.SIGN_TYPE);

    //sign client pay
    public static String charge(UserBill userBill) {
        String orderString = "";
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(UserAccUtils.chargeDes(userBill));
        model.setSubject(UserAccUtils.chargeDes(userBill));
        model.setOutTradeNo(userBill.getTradeNumber());
        model.setTotalAmount(String.valueOf(userBill.getBillGold()));
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(Utils.getUrl(AlipayConfig.NOTIFY_URL));
        try {
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            orderString = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("alipay sign pay excetion,billId:" + userBill.getBillId());
        }
        return orderString;
    }

    //valid pay
    public static boolean vertifyPaySyn(UserBill userBill, String orderString) {
        try {
            PaySynRes paySynRes = JSON.parseObject(orderString, PaySynRes.class);
            LinkedHashMap<String, String> resultMap = paySynRes.getAlipay_trade_app_pay_response();
            String content = JSON.toJSONString(resultMap);
            String sign = paySynRes.getSign();
            boolean success = AlipaySignature.rsa256CheckContent(
                    content, sign, AlipayConfig.PUBLIC_KEY, resultMap.get("charset"));
            success = success && resultMap.containsKey("code") &&
                    resultMap.get("code").equals("10000") &&
                    resultMap.containsKey("out_trade_no") &&
                    resultMap.get("out_trade_no").equals(userBill.getTradeNumber()) &&
                    resultMap.containsKey("total_amount") &&
                    resultMap.get("total_amount").equals(DecimalUtils.goldFormat(userBill.getBillGold())) &&
                    resultMap.containsKey("seller_id") &&
                    resultMap.get("seller_id").equals(AlipayConfig.SELLER_ID) &&
                    resultMap.containsKey("app_id") &&
                    resultMap.get("app_id").equals(AlipayConfig.APP_ID);
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("alipay syn vertify exeption,billId:" + userBill.getBillId());
        }
        return false;
    }

    //获取异步通知返回参数
    public static Map<String, String> getNotifyRequestParam(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Set keSet = request.getParameterMap().entrySet();
        for (Iterator itr = keSet.iterator(); itr.hasNext(); ) {
            Map.Entry me = (Map.Entry) itr.next();
            map.put((String) me.getKey(), ((String[]) me.getValue())[0]);
        }
        return map;
    }

    //pay notify vertify asyn
    public static boolean vertifyPayAsyn(UserBill userBill, Map<String, String> requestMap) {
        try {
            //vertify trade sign
            boolean success = AlipaySignature.rsaCheckV1(Utils.copyMap(requestMap),
                    AlipayConfig.PUBLIC_KEY, requestMap.get("charset"), requestMap.get("sign_type"));
            //veritfy trade data
            success = success && requestMap.containsKey("out_trade_no") &&
                    requestMap.get("out_trade_no").equals(userBill.getTradeNumber()) &&
                    requestMap.containsKey("total_amount") &&
                    requestMap.get("total_amount").equals(DecimalUtils.goldFormat(userBill.getBillGold())) &&
                    requestMap.containsKey("seller_id") &&
                    requestMap.get("seller_id").equals(AlipayConfig.SELLER_ID) &&
                    requestMap.containsKey("app_id") &&
                    requestMap.get("app_id").equals(AlipayConfig.APP_ID) &&
                    requestMap.containsKey("trade_status");
            LogUtils.logAcc(AccLogType.ChargeAlipayVer, userBill, null, requestMap, success);
            return success;
        } catch (Exception e) {
            return false;
        }
    }

    //充值业务处理
    public static void dealChargeBill(UserBill userBill, Map<String, String> alimap) {
        if (alimap.get("trade_status").equals(AlipayConfig.WAIT_BUYER_PAY)) {
            //wait pay
        } else if (alimap.get("trade_status").equals(AlipayConfig.TRADE_SUCCESS)
                || alimap.get("trade_status").equals(AlipayConfig.TRADE_FINISHED)) {
            //pay success
            UserAccUtils.chargeBill(userBill, alimap);
        } else if (alimap.get("trade_status").equals(AlipayConfig.TRADE_CLOSED)) {
            //status close
            userBill.setDealStatus(DealStatus.Failed.getStatus());
            userBill.setDealTime(TimeUtils.getCurrentDate());
            userBill.setDesCode(AlipayConfig.TRADE_CLOSED);
            userBill = userBillDao.save(userBill);
        }
    }


    //fund bizcontent
    public static String bizContentFund(UserBill userBill) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("out_biz_no", userBill.getTradeNumber());
        map.put("payee_type", "ALIPAY_LOGONID");
        map.put("payee_account", userBill.getAccount());
        map.put("amount", DecimalUtils.goldFormat(userBill.getBillGold()));
        map.put("payer_real_name", "上海文维网络技术有限公司");
        map.put("payer_show_name", "文芽提现");
        map.put("payee_real_name", userBill.getName());
        map.put("remark", "文芽提现");
        Map<String, String> tmap = new HashMap<String, String>();
        tmap.put("order_title", "文芽-提现" + DecimalUtils.goldFormat(userBill.getBillGold()) + "元");
        map.put("ext_param", JSON.toJSONString(tmap));
        return JSON.toJSONString(map);
    }

    //fund query biz content
    private static String bizContentFundQury(UserBill userBill) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("out_biz_no", userBill.getTradeNumber());
        return JSON.toJSONString(map);
    }

    //fund
    public static void fund(UserBill userBill) {
        if (userBill.getPayType() == PayType.Alipay.getType()) {
            AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
            request.setBizContent(bizContentFund(userBill));
            AlipayFundTransToaccountTransferResponse response = null;
            try {
                response = alipayClient.execute(request);
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
            String result = response.getBody();
            if (result != null) {
                Map<String, String> resultMap = getResParam(result);
                LogUtils.logAcc(AccLogType.FundAlipayRes, userBill, null, resultMap, null);
                if (response.isSuccess() && resultMap.get("code").equals("10000")) {
                    //success
                    userBill = UserAccUtils.withdrawBill(userBill);
                } else if (resultMap.get("code").equals("20000")
                        || resultMap.get("code").equals("40004") && resultMap.get("sub_code").equals(AlipayConfig.SYSTEM_ERROR)) {
                    //query fund status
                    String status = fundQuery(userBill);
                    if (status.equals(AlipayConfig.FUND_STATUS_SUCCESS)) {
                        //success
                        userBill = UserAccUtils.withdrawBill(userBill);
                    } else if (AlipayUtils.fundFailed(status)) {
                        //failed roll back
                        userBill = UserAccUtils.rollBackBill(userBill, status);
                    }
                } else {
                    //failed roll back
                    userBill = UserAccUtils.rollBackBill(userBill, resultMap.get("sub_code"));
                }
            }
        }
    }

    public static boolean fundFailed(String sub_code) {
        if (!sub_code.equals(AlipayConfig.ORDER_NOT_EXIST)
                && !sub_code.equals(AlipayConfig.SYSTEM_ERROR)) {
            return true;
        }
        return false;
    }

    //fund query
    public static String fundQuery(UserBill userBill) {
        try {
            if (userBill.getPayType() == PayType.Alipay.getType()) {
                AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
                request.setBizContent(bizContentFundQury(userBill));
                AlipayFundTransOrderQueryResponse response = alipayClient.execute(request);
                String result = response.getBody();
                if (result != null) {
                    Map<String, String> resultMap = getResParam(result);
                    LogUtils.logAcc(AccLogType.FundAlipayQury, userBill, null, resultMap, null);
                    if (response.isSuccess()) {
                        if (resultMap.containsKey("status")) {
                            return resultMap.get("status");
                        }
                    } else {
                        if (resultMap.containsKey("sub_code")) {
                            return resultMap.get("sub_code");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("alipay fund query exception,billId:" + userBill.getBillId());
        }
        return AlipayConfig.FUND_STATUS_DEALING;
    }


    //获取返回值
    public static Map<String, String> getResParam(String result) {
        Map<String, String> commonMap = Utils.getParamFromJson(result);
        List<String> keys = new ArrayList<String>(commonMap.keySet());
        String response = null;
        for (String key : keys) {
            if (key.contains("response")) {
                response = commonMap.get(key);
            }
        }
        Map<String, String> subMap = Utils.getParamFromJson(response);
        if (commonMap.containsKey("sign")) {
            subMap.put("sign", commonMap.get("sign"));
        }
        if (commonMap.containsKey("sign_type")) {
            subMap.put("sign_type", commonMap.get("sign_type"));
        }
        return subMap;
    }
}
