package com.wg.useraccount.utils.wexinpay.sign;

import com.wg.common.utils.Utils;
import com.wg.useraccount.utils.wexinpay.WexinPayConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/1 0001.
 */
public class WXMD5 {
    //微信MD5加密(key排序,过滤,链接,utf-8,加密)
    public static String getSign(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (null != value && !"".equals(value) && !"sign".equals(key) && !"key".equals(key)) {
                prestr += key + "=" + value + "&";
            }
        }
        prestr += "key=" + WexinPayConfig.KEY;
        return Utils.MD5(prestr).toUpperCase();
    }
}
