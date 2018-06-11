package com.wg.useraccount.utils;

import com.wg.common.Enum.useraccount.AccLogType;
import com.wg.common.Enum.useraccount.DealStatus;
import com.wg.common.Enum.useraccount.PayType;
import com.wg.common.utils.HttpUtils;
import com.wg.common.utils.LogUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.useraccount.domain.UserBill;
import com.wg.useraccount.utils.wexinpay.WexinPayConfig;
import com.wg.useraccount.utils.wexinpay.sign.WXMD5;
import com.wg.useraccount.utils.wexinpay.util.ClientSSL;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.wg.common.utils.dbutils.DaoUtils.userBillDao;
import static com.wg.common.utils.Utils.logger;

/**
 * Created by Administrator on 2017/2/28 0028.
 */
public class WexinPayUtils {
    public static final String NOTIFY_SUCCESS = "success";

    //下单
    public static Map<String, String> charge(UserBill userBill) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", WexinPayConfig.APPID);//appid
        params.put("mch_id", WexinPayConfig.MCH_ID);//商户号
        params.put("nonce_str", Utils.getRandomStringByLength(8));//随机串
        params.put("body", UserAccUtils.chargeDes(userBill));//商品描述
        params.put("out_trade_no", userBill.getTradeNumber());//订单号
        params.put("total_fee", String.valueOf((int) (userBill.getBillGold().doubleValue() * 100)));//总金额
        params.put("spbill_create_ip", WexinPayConfig.SPBILL_CREATE_IP);//终端ip
        params.put("notify_url", Utils.getUrl(WexinPayConfig.NOTIFY_URL));//异步通知地址
        params.put("trade_type", WexinPayConfig.TRADE_TYPE);//交易类型
        params.put("sign", WXMD5.getSign(params));//签名

        HttpPost httppost = new HttpPost(WexinPayConfig.UNIFIEDORDER_SERVICE);
        httppost.setEntity(new StringEntity(Utils.parseParamToXml(params), ContentType.TEXT_XML));
        String xmlStr = HttpUtils.requestString(httppost);

        //接收结果
        if (StringUtils.isNotBlank(xmlStr) && !xmlStr.contains("FAIL")) {
            try {
                Map<String, String> resultMap = Utils.getParamFromXML(xmlStr);// 解析返回值
                boolean isSuccess = resultMap != null &&
                        resultMap.containsKey("return_code") &&
                        resultMap.get("return_code").equals(WexinPayConfig.RETURN_SUCCESS) &&
                        resultMap.containsKey("appid") &&
                        resultMap.containsKey("mch_id") &&
                        resultMap.containsKey("nonce_str") &&
                        resultMap.containsKey("sign") &&
                        resultMap.containsKey("result_code") &&
                        resultMap.get("result_code").equals(WexinPayConfig.RESULT_SUCCESS) &&
                        resultMap.containsKey("prepay_id");
                if (isSuccess) {
                    return resultMap;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //同步验证支付成功
    public static boolean vertifyPaySyn(UserBill userBill) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", WexinPayConfig.APPID);//appid
        params.put("mch_id", WexinPayConfig.MCH_ID);//商户号
        params.put("out_trade_no", userBill.getTradeNumber());//订单号
        params.put("nonce_str", Utils.getRandomStringByLength(8));//随机串
        params.put("sign", WXMD5.getSign(params));//签名

        HttpPost httppost = new HttpPost(WexinPayConfig.ORDERQUERY_SERVICE);
        httppost.setEntity(new StringEntity(Utils.parseParamToXml(params), ContentType.TEXT_XML));
        String xmlStr = HttpUtils.requestString(httppost);

        if (StringUtils.isNotBlank(xmlStr) && !xmlStr.contains("FAIL")) {
            try {
                Map<String, String> resultMap = Utils.getParamFromXML(xmlStr);// 解析返回值
                boolean isSuccess = resultMap != null &&
                        resultMap.containsKey("return_code") &&
                        resultMap.get("return_code").equals(WexinPayConfig.RETURN_SUCCESS) &&
                        resultMap.containsKey("appid") &&
                        resultMap.containsKey("mch_id") &&
                        resultMap.containsKey("nonce_str") &&
                        resultMap.containsKey("sign") &&
                        resultMap.containsKey("result_code") &&
                        resultMap.get("result_code").equals(WexinPayConfig.RESULT_SUCCESS) &&
                        resultMap.containsKey("openid") &&
                        resultMap.containsKey("trade_type") &&
                        resultMap.containsKey("trade_state") &&
                        resultMap.get("trade_state").equals(WexinPayConfig.TRADE_STATE_SUCCESS) &&
                        resultMap.containsKey("bank_type") &&
                        resultMap.containsKey("total_fee") &&
                        resultMap.containsKey("cash_fee") &&
                        resultMap.containsKey("transaction_id") &&
                        resultMap.containsKey("out_trade_no") &&
                        resultMap.containsKey("time_end");
                return isSuccess;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //异步通知验证
    public static boolean vertifyPayAsyn(UserBill userBill, Map<String, String> map) {
        try {
            //status success & origin data
            boolean isSuccess = map.containsKey("return_code") &&
                    map.get("return_code").equals(WexinPayConfig.RETURN_SUCCESS) &&
                    map.containsKey("appid") &&
                    map.get("appid").equals(WexinPayConfig.APPID) &&
                    map.containsKey("mch_id") &&
                    map.get("mch_id").equals(WexinPayConfig.MCH_ID) &&
                    map.containsKey("nonce_str") &&
                    map.containsKey("sign") &&
                    map.containsKey("result_code") &&
                    map.containsKey("openid") &&
                    map.containsKey("total_fee") &&
                    map.get("total_fee").equals(String.valueOf((int) (userBill.getBillGold().doubleValue() * 100))) &&
                    map.containsKey("transaction_id") &&
                    map.containsKey("out_trade_no") &&
                    map.get("out_trade_no").equals(userBill.getTradeNumber()) &&
                    map.containsKey("time_end");
            //vertify sign
            boolean isSignOk = isSuccess && WXMD5.getSign(map).equals(map.get("sign"));
            //log
            LogUtils.logAcc(AccLogType.ChargeWeixinVer, userBill, null, map, isSignOk);
            return isSignOk;
        } catch (Exception e) {
            return false;
        }
    }

    //充值业务处理
    public static void dealChargeBill(UserBill userBill, Map<String, String> weixinMap) {
        if (weixinMap.get("result_code").equals(WexinPayConfig.RESULT_SUCCESS)) {
            //success
            UserAccUtils.chargeBill(userBill, weixinMap);
        } else if (weixinMap.get("result_code").equals(WexinPayConfig.RESULT_FAIL)) {
            //fail
            userBill.setDealStatus(DealStatus.Failed.getStatus());
            userBill.setDealTime(TimeUtils.getCurrentDate());
            userBill.setDesCode(WexinPayConfig.RESULT_FAIL);
            userBill = userBillDao.save(userBill);
        }
    }

    //获取异步通知返回参数
    public static Map<String, String> getNotifyRequestParam(HttpServletRequest request) {
        try {
            BufferedReader reader = null;
            reader = request.getReader();
            String line = "";
            String xmlString = null;
            StringBuffer inputString = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                inputString.append(line);
            }
            xmlString = inputString.toString();
            return Utils.getParamFromXML(xmlString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    //异步通知返回
    public static String returnXML(String return_code) {
        return "<xml>" +
                "<return_code><![CDATA[" + return_code + "]]></return_code>" +
                "<return_msg><![CDATA[OK]]></return_msg>" +
                "</xml>";
    }

    //提现
    public static void fund(UserBill userBill) {
        if (userBill.getPayType() == PayType.Weixin.getType()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("mch_appid", WexinPayConfig.APPID);//appid
            params.put("mchid", WexinPayConfig.MCH_ID);//商户号
            params.put("nonce_str", Utils.getRandomStringByLength(8));//随机串
            params.put("partner_trade_no", userBill.getTradeNumber());//订单号
            params.put("openid", userBill.getAccount());//用户微信账号
            params.put("check_name", WexinPayConfig.USER_NAME_CHECK_TYPE);//用户微信实名校验类型
            params.put("re_user_name", userBill.getName());//用户微信实名
            params.put("amount", String.valueOf((int) (userBill.getBillGold().doubleValue() * 100)));//总金额
            params.put("desc", UserAccUtils.fundDes(userBill, PayType.Weixin.getType()));//交易描述
            params.put("spbill_create_ip", WexinPayConfig.SPBILL_CREATE_IP);//终端ip
            params.put("sign", WXMD5.getSign(params));//签名

            HttpPost httppost = new HttpPost(WexinPayConfig.FUND_SERVICE);
            httppost.setEntity(new StringEntity(Utils.parseParamToXml(params), ContentType.TEXT_XML));
            String xmlStr = ClientSSL.request(httppost);
            //提现同步返回
            try {
                Map<String, String> resultMap = Utils.getParamFromXML(xmlStr);// 解析返回值
                LogUtils.logAcc(AccLogType.FundWeixinRes, userBill, null, resultMap, null);
                if (resultMap != null) {
                    boolean success = resultMap.containsKey("return_code")
                            && resultMap.get("return_code").equals(WexinPayConfig.RETURN_SUCCESS) && resultMap.containsKey("result_code");
                    if (success) {
                        if (resultMap.get("result_code").equals(WexinPayConfig.RESULT_SUCCESS)) {
                            //success
                            userBill = UserAccUtils.withdrawBill(userBill);
                        } else if (resultMap.get("result_code").equals(WexinPayConfig.RESULT_FAIL)) {
                            //failed roll back
                            userBill = UserAccUtils.rollBackBill(userBill, resultMap.get("err_code"));
                        }
                    } else if (resultMap.get("return_code").equals(WexinPayConfig.RETURN_FAIL)) {
                        //query fund status
                        String status = fundQuery(userBill);
                        if (status.equals(WexinPayConfig.FUND_STATUS_SUCCESS)) {
                            //success
                            userBill = UserAccUtils.withdrawBill(userBill);
                        } else if (fundFailed(status)) {
                            //failed roll back
                            userBill = UserAccUtils.rollBackBill(userBill, resultMap.get(status));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("wexinpay fund exception,billId:" + userBill.getBillId());
            }
        }
    }

    public static boolean fundFailed(String status) {
        if (!status.equals(WexinPayConfig.NOT_FOUND)
                && !status.equals(WexinPayConfig.SYSTEMERROR)
                && !status.equals(WexinPayConfig.FUND_STATUS_SUCCESS)
                && !status.equals(WexinPayConfig.FUND_STATUS_PROCESSING)) {
            return true;
        }
        return false;
    }

    //查询第三方转账状态
    public static String fundQuery(UserBill userBill) {
        if (userBill.getPayType() == PayType.Weixin.getType()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("appid", WexinPayConfig.APPID);//appid
            params.put("mch_id", WexinPayConfig.MCH_ID);//商户号
            params.put("nonce_str", Utils.getRandomStringByLength(8));//随机串
            params.put("partner_trade_no", userBill.getTradeNumber());//订单号
            params.put("sign", WXMD5.getSign(params));//签名

            HttpPost httppost = new HttpPost(WexinPayConfig.FUNDQUERY_SERVICE);
            httppost.setEntity(new StringEntity(Utils.parseParamToXml(params), ContentType.TEXT_XML));
            String xmlStr = ClientSSL.request(httppost);

            try {
                Map<String, String> resultMap = Utils.getParamFromXML(xmlStr);// 解析返回值
                //log fund qury
                LogUtils.logAcc(AccLogType.FundWeixinQury, userBill, null, resultMap, null);
                if (resultMap != null) {
                    boolean success = resultMap.containsKey("return_code") &&
                            resultMap.get("return_code").equals(WexinPayConfig.RETURN_SUCCESS) &&
                            resultMap.containsKey("result_code");
                    if (success) {
                        if (resultMap.get("result_code").equals(WexinPayConfig.RESULT_SUCCESS)) {
                            return resultMap.get("status");
                        } else if (resultMap.get("result_code").equals(WexinPayConfig.RESULT_FAIL)) {
                            return resultMap.get("err_code");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("wexinpay fund query exception,billId:" + userBill.getBillId());
            }
        }
        return WexinPayConfig.FUND_STATUS_PROCESSING;
    }
}
