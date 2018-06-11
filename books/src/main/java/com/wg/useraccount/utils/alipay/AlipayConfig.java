package com.wg.useraccount.utils.alipay;

/* *
 *类名:AlipayConfig
 *功能:基础配置类
 *详细:设置帐户有关信息及返回路径
 *版本:1.0
 *日期:2016-06-06
 *说明:
 *以下代码只是为了方便商户测试而提供的样例代码,商户可以根据自己网站的需要,按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用,只是提供一个参考。
 */

import com.wg.common.Constant;
import com.wg.common.PropConfig;

public class AlipayConfig {
    // 支付宝open api url
    public static final String ALIPAY_GATEWAY = PropConfig.ALIPAY_GATEWAY;
    //appid
    public static final String APP_ID = PropConfig.ALIPAY_APPID;
    //企业支付宝账号或partner
    public static final String SELLER_ID = PropConfig.ALIPAY_SELLER_ID;
    //私钥
    public static final String PRIVATE_KEY = PropConfig.ALIPAY_PRIVATEKEY;
    //公钥
    public static final String PUBLIC_KEY = PropConfig.ALIPAY_PUBLICKEY;
    //format
    public static final String FORMAT = "json";
    // 签名方式
    public static final String SIGN_TYPE = "RSA2";
    // 字符编码
    public static final String INPUT_CHARSET = Constant.CHAR_SET_UTF8;
    //通知接口
    public static final String NOTIFY_URL = PropConfig.ALIPAY_NOTIFYURL;
    //错误码
    public static final String ORDER_NOT_EXIST = "ORDER_NOT_EXIST";
    public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
    //转账状态
    public static final String FUND_STATUS_SUCCESS = "SUCCESS";
    public static final String FUND_STATUS_FAIL = "FAIL";
    public static final String FUND_STATUS_INIT = "INIT";
    public static final String FUND_STATUS_DEALING = "DEALING";
    public static final String FUND_STATUS_REFUND = "REFUND";
    public static final String FUND_STATUS_UNKNOWN = "UNKNOWN";
    //支付状态
    public static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    public static final String TRADE_FINISHED = "TRADE_FINISHED";
    public static final String TRADE_CLOSED = "TRADE_CLOSED";
}

