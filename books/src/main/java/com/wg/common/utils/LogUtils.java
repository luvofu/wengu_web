package com.wg.common.utils;

import com.alibaba.fastjson.JSON;
import com.wg.bookorder.domain.BookOrder;
import com.wg.common.Enum.useraccount.AccLogType;
import com.wg.common.PropConfig;
import com.wg.useraccount.domain.UserBill;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static com.wg.common.utils.dbutils.DaoUtils.userAccountDao;

/**
 * Created by Administrator on 2017/3/17 0017.
 */
public class LogUtils {

    //用户账户变动日志、第三方校验数据
    public static void logAcc(AccLogType accLogType, UserBill userBill, BookOrder bookOrder, Map<String, String> map, Boolean success) {
        try {
            String time = TimeUtils.formatDate(TimeUtils.getCurrentDate(), TimeUtils.YYYY_MM_DD_HH_MM_SS_SSS);
            String log = "LogTime:" + time + "\nAccLogType:" + accLogType.getType();
            long id = 0;
            if (userBill != null) {
                log += "\nUserAccount:" + JSON.toJSONString(userAccountDao.findByUserId(userBill.getUserId()));
                log += "\nUserBill:" + JSON.toJSONString(userBill);
                id = userBill.getBillId();
            }
            if (bookOrder != null) {
                log += "\nUserAccount:" + JSON.toJSONString(userAccountDao.findByUserId(bookOrder.getUserId()));
                log += "\nBookOrder:" + JSON.toJSONString(bookOrder);
                id = bookOrder.getOrderId();
            }
            if (map != null) {
                log += "\nThirdParam:" + JSON.toJSONString(map);
                log += "\nVertify:" + success;
            }
            String file = PropConfig.ACC_LOGPATH + time + "_" + accLogType.getType() + "_" + id + ".txt";
            log(log, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //日志
    public static void log(String sWord, String file) {
        FileWriter writer = null;
        try {
            if (StringUtils.isNotBlank(sWord) && StringUtils.isNotBlank(file)) {
                writer = new FileWriter(file);
                writer.write(sWord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
