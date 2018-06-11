package com.wg.useraccount.utils;

import com.wg.common.utils.DecimalUtils;
import com.wg.common.utils.Utils;
import com.wg.useraccount.domain.UserBill;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static com.wg.common.utils.dbutils.DaoUtils.userBillDao;

/**
 * Created by Administrator on 2017/3/14 0014.
 */
public class BillSignUtils {
    //bill remark map
    public static String signInfo(UserBill userBill) {
        return getSign(signMap(userBill));
    }

    //bill sign map
    public static Map<String, String> signMap(UserBill userBill) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("billId", String.valueOf(userBill.getBillId()));
        map.put("billType", String.valueOf(userBill.getBillType()));
        map.put("billGold", String.valueOf(DecimalUtils.goldFormat(userBill.getBillGold())));
        map.put("accGold", String.valueOf(DecimalUtils.goldFormat(userBill.getAccGold())));
        List<UserBill> userBillList = userBillDao.findByUserIdAndBillIdLessThanOrderByBillIdDesc(
                userBill.getUserId(), userBill.getBillId(), new PageRequest(0, 1));
        if (userBillList.size() == 1) {
            UserBill preBill = userBillList.get(0);
            map.put("prebillId", String.valueOf(preBill.getBillId()));
            map.put("preSign", preBill.getExtendInfo());
        }
        return map;
    }

    //veritfy sign
    public static boolean veritfy(UserBill userBill) {
        return getSign(signMap(userBill)).equals(userBill.getExtendInfo());
    }

    //微信MD5加密(key排序,过滤,链接,utf-8,加密)
    public static String getSign(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (null != value && !"".equals(value) && !"sign".equals(key)) {
                prestr += key + "=" + value + "&";
            }
        }
        return Utils.MD5(prestr).toUpperCase();
    }
}
