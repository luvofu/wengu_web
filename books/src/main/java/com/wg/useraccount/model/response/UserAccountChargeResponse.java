package com.wg.useraccount.model.response;

import com.wg.common.utils.TimeUtils;
import com.wg.useraccount.domain.UserBill;
import com.wg.useraccount.utils.wexinpay.WexinPayConfig;
import com.wg.useraccount.utils.wexinpay.sign.WXMD5;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/28 0028.
 */
public class UserAccountChargeResponse {
    long billId;
    String appid;
    String partnerid;
    String prepayid;
    String _package;
    String noncestr;
    String timestamp;
    String sign;
    String orderString;

    public UserAccountChargeResponse(UserBill userBill, Map<String, String> map) {
        this.billId = userBill.getBillId();
        Map<String, String> signMap = new HashMap<String, String>();
        signMap.put("appid", map.get("appid"));
        signMap.put("partnerid", map.get("mch_id"));
        signMap.put("prepayid", map.get("prepay_id"));
        signMap.put("package", WexinPayConfig._PACKAGE);
        signMap.put("noncestr", map.get("nonce_str"));
        signMap.put("timestamp", String.valueOf(TimeUtils.getCurrentDate().getTime() / 1000));
        this.appid = signMap.get("appid");
        this.partnerid = signMap.get("partnerid");
        this.prepayid = signMap.get("prepayid");
        this._package = signMap.get("package");
        this.noncestr = signMap.get("noncestr");
        this.timestamp = signMap.get("timestamp");
        this.sign = WXMD5.getSign(signMap);
    }

    public UserAccountChargeResponse(UserBill userBill, String orderString) {
        this.billId = userBill.getBillId();
        this.orderString = orderString;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String get_package() {
        return _package;
    }

    public void set_package(String _package) {
        this._package = _package;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }
}
