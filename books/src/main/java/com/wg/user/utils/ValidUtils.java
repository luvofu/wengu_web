package com.wg.user.utils;

import com.wg.common.Enum.message.SMsgTemplate;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.message.utils.SMsgUtils;
import com.wg.user.domain.Valid;

import java.util.Date;

import static com.wg.common.utils.dbutils.DaoUtils.validDao;

/**
 * Created by Administrator on 2016/9/20.
 */
public class ValidUtils {
    //devicetoken regmobile conditon use 5 times for one day
    public static final int VALID_REQUEST_NUM = 5;
    //validcode use time 30 minute
    public static final int VALIDCODE_MINUTE = 30;

    //发送验证码
    public static String sendValidcode(String mobile) {
        //request message to send valicode for user
        if (UserUtils.isPhone(mobile)) {
            //生成六位验证码
            String validcode = Utils.generRandCode(6);
            if (SMsgUtils.sendMsg(mobile, SMsgTemplate.ValidCode, validcode)) {
                return validcode;
            }
        }
        return null;
    }

    //获取验证码过期时间
    public static Date getValidcodeExpireDate(Date updatedTime) {
        return TimeUtils.getModifyDate(updatedTime, null, null, VALIDCODE_MINUTE, null);
    }

    //验证码校验
    public static boolean checkValidcode(String deviceToken, String regMobile, int useCondition, String validcode) {
        Valid valid = validDao.findByDeviceTokenAndRegMobileAndUseCondition(deviceToken, regMobile, useCondition);
        if (valid != null && valid.getValidcode() != null) {
            if (valid.getValidcode().equals(validcode)) {
                if (ValidUtils.getValidcodeExpireDate(valid.getUpdatedTime()).after(TimeUtils.getCurrentDate())) {
                    valid.setValidcode(null);//校验完毕,验证码置空失效
                    valid = validDao.save(valid);
                    return true;
                }
            }
        }
        return false;
    }
}
