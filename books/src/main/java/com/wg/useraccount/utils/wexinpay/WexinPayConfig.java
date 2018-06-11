package com.wg.useraccount.utils.wexinpay;

import com.wg.common.PropConfig;

/**
 * Created by Administrator on 2017/2/28 0028.
 */
public class WexinPayConfig {
    //下单接口
    public static final String UNIFIEDORDER_SERVICE = PropConfig.WEIXINPAY_UNIFIEDORDER_SERVICE;
    //订单查询接口
    public static final String ORDERQUERY_SERVICE = PropConfig.WEIXINPAY_ORDERQUERY_SERVICE;
    //转账url
    public static final String FUND_SERVICE = PropConfig.WEIXINPAY_FUND_SERVICE;
    //转账查询url
    public static final String FUNDQUERY_SERVICE = PropConfig.WEIXINPAY_FUNDQUERY_SERVICE;
    //应用id,微信开放平台审核通过的应用APPID
    public static final String APPID = PropConfig.WEIXINPAY_APPID;
    //商户号,微信支付分配的商户号
    public static final String MCH_ID = PropConfig.WEIXINPAY_MCHID;
    //通知接口
    public static final String NOTIFY_URL = PropConfig.WEIXINPAY_NOTIFYURL;
    //交易类型
    public static final String TRADE_TYPE = "APP";
    //扩展字段
    public static final String _PACKAGE = "Sign=WXPay";
    //服务ip
    public static final String SPBILL_CREATE_IP = PropConfig.WEIXINPAY_IP;
    //api密钥
    public static final String KEY = PropConfig.WEIXINPAY_KEY;
    //实名校验类型：有则检验
    public static final String USER_NAME_CHECK_TYPE = "OPTION_CHECK";
    //错误码
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String SYSTEMERROR = "SYSTEMERROR";
    //return code
    public static final String RETURN_SUCCESS = "SUCCESS";
    public static final String RETURN_FAIL = "FAIL";
    //result code
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAIL = "FAIL";
    //fund status
    public static final String FUND_STATUS_SUCCESS = "SUCCESS";
    public static final String FUND_STATUS_FAILED = "FAILED";
    public static final String FUND_STATUS_PROCESSING = "PROCESSING";
    //pay state
    public static final String TRADE_STATE_SUCCESS = "SUCCESS";

}
