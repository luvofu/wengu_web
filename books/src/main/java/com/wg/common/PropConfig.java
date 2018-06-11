package com.wg.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.wg.common.utils.Utils.logger;

/**
 * 全局属性配置
 */
@Component
public class PropConfig {
    @Value("#{config['officer.uid']}")
    private String officerUserId;

    @Value("#{config['officer.email']}")
    private String officerEmail;

    @Value("#{config['server']}")
    private String serverUrl;

    @Value("#{config['upload.path']}")
    private String uploadPath;

    @Value("#{config['account.logpath']}")
    private String accLogPath;

    @Value("#{config['solr.server']}")
    private String solrServerUrl;

    @Value("#{config['messagepush.appid']}")
    private String messagePushAppId;
    @Value("#{config['messagepush.appkey']}")
    private String messagePushAppKey;
    @Value("#{config['messagepush.mastersecret']}")
    private String messagePushMasterSecret;
    @Value("#{config['messagepush.host']}")
    private String messagePushHost;

    @Value("#{config['email.hostname']}")
    private String emailHostName;
    @Value("#{config['email.port']}")
    private int emailPort;
    @Value("#{config['email.username']}")
    private String emailUsername;
    @Value("#{config['email.password']}")
    private String emailPassword;
    @Value("#{config['email.from']}")
    private String emailFrom;

    @Value("#{config['im.appkey']}")
    private String imAppKey;
    @Value("#{config['im.secret']}")
    private String imSecret;
    @Value("#{config['im.host']}")
    private String imHost;

    @Value("#{config['alipay.gateway']}")
    private String alipayGateway;
    @Value("#{config['alipay.appid']}")
    private String alipayAppId;
    @Value("#{config['alipay.sellerid']}")
    private String alipaySellerId;
    @Value("#{config['alipay.publickey']}")
    private String alipayPublickey;
    @Value("#{config['alipay.privatekey']}")
    private String alipayPrivatekey;
    @Value("#{config['alipay.notifyurl']}")
    private String alipayNotifyurl;

    @Value("#{config['weixinpay.unifiedorder.service']}")
    private String weixinpayUnifiedorderService;
    @Value("#{config['weixinpay.orderquery.service']}")
    private String weixinpayOrderqueryService;
    @Value("#{config['weixinpay.fund.service']}")
    private String weixinpayFundService;
    @Value("#{config['weixinpay.fund.query.service']}")
    private String weixinpayFundqueryService;
    @Value("#{config['weixinpay.appid']}")
    private String weixinpayAppid;
    @Value("#{config['weixinpay.mch_id']}")
    private String weixinpayMchId;
    @Value("#{config['weixinpay.notifyurl']}")
    private String weixinpayNotifyurl;
    @Value("#{config['weixinpay.ip']}")
    private String weixinpayIp;
    @Value("#{config['weixinpay.key']}")
    private String weixinpayKey;

    //official userId sheetname
    public static long OFFICER_USERID;
    //officer email
    public static String OFFICER_EMAIL;
    //server
    public static String SERVER_URL;
    //server.folder
    public static String UPLOAD_PATH;
    //account logAcc path
    public static String ACC_LOGPATH;
    //solr.server
    public static String SOLR_SERVER_URL;
    //message.push
    public static String MP_APPID;
    public static String MP_APPKEY;
    public static String MP_SECRET;
    public static String MP_HOST;

    public static String EMAIL_HOSTNAME;
    public static int EMAIL_PORT;
    public static String EMAIL_USERNAME;
    public static String EMAIL_PASSWORD;
    public static String EMAIL_FROM;

    public static String IM_APPKEY;
    public static String IM_SECRET;
    public static String IM_HOST;

    public static String ALIPAY_SELLER_ID;
    public static String ALIPAY_PUBLICKEY;
    public static String ALIPAY_PRIVATEKEY;
    public static String ALIPAY_NOTIFYURL;
    public static String ALIPAY_GATEWAY;
    public static String ALIPAY_APPID;

    public static String WEIXINPAY_UNIFIEDORDER_SERVICE;
    public static String WEIXINPAY_ORDERQUERY_SERVICE;
    public static String WEIXINPAY_FUND_SERVICE;
    public static String WEIXINPAY_FUNDQUERY_SERVICE;
    public static String WEIXINPAY_APPID;
    public static String WEIXINPAY_MCHID;
    public static String WEIXINPAY_NOTIFYURL;
    public static String WEIXINPAY_IP;
    public static String WEIXINPAY_KEY;

    @PostConstruct
    public void init() {
        logger.info("PropConfig construct");

        OFFICER_USERID = Long.valueOf(this.officerUserId);
        OFFICER_EMAIL = this.officerEmail;
        SERVER_URL = this.serverUrl;
        UPLOAD_PATH = this.uploadPath;
        ACC_LOGPATH = this.accLogPath;
        SOLR_SERVER_URL = this.solrServerUrl;
        MP_APPID = this.messagePushAppId;
        MP_APPKEY = this.messagePushAppKey;
        MP_SECRET = this.messagePushMasterSecret;
        EMAIL_HOSTNAME = this.emailHostName;
        EMAIL_PORT = this.emailPort;
        EMAIL_USERNAME = this.emailUsername;
        EMAIL_PASSWORD = this.emailPassword;
        EMAIL_FROM = this.emailFrom;
        IM_HOST = this.imHost;
        IM_APPKEY = this.imAppKey;
        IM_SECRET = this.imSecret;
        ALIPAY_SELLER_ID = this.alipaySellerId;
        ALIPAY_PUBLICKEY = this.alipayPublickey;
        ALIPAY_PRIVATEKEY = this.alipayPrivatekey;
        ALIPAY_NOTIFYURL = this.alipayNotifyurl;
        ALIPAY_GATEWAY = this.alipayGateway;
        ALIPAY_APPID = this.alipayAppId;
        WEIXINPAY_UNIFIEDORDER_SERVICE = this.weixinpayUnifiedorderService;
        WEIXINPAY_ORDERQUERY_SERVICE = this.weixinpayOrderqueryService;
        WEIXINPAY_FUND_SERVICE = this.weixinpayFundService;
        WEIXINPAY_FUNDQUERY_SERVICE = this.weixinpayFundqueryService;
        WEIXINPAY_APPID = this.weixinpayAppid;
        WEIXINPAY_MCHID = this.weixinpayMchId;
        WEIXINPAY_NOTIFYURL = this.weixinpayNotifyurl;
        WEIXINPAY_IP = this.weixinpayIp;
        WEIXINPAY_KEY = this.weixinpayKey;
    }
}
