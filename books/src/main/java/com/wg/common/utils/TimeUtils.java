package com.wg.common.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public class TimeUtils {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_EX = "yyyy年 MM月 dd日";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_EX = "yyyy.MM.dd HH:mm";
    public static final String YYDDMMHHMMSSSSS = "yyMMddHHmmssSSS";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss:SSS";


    //获得修改后时间date
    public static Date getModifyDate(Date date,
                                     Integer day,
                                     Integer hour,
                                     Integer minute,
                                     Integer second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (day != null) calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + day);
        if (hour != null) calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);
        if (minute != null) calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + minute);
        if (second != null) calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + second);
        return calendar.getTime();
    }

    //获取当前时间date
    public static Date getCurrentDate() {
        return new Date();
    }

    //获取当前时间戳
    public static Long getCurrTimestamp() {
        return new Date().getTime();
    }

    //时间戳long转date
    public static Date getDate(Long timestamp) {
        return new Date(timestamp);
    }

    //时间转字符串
    public static String formatDate(Date date, String formate) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(formate);
            return sdf.format(date);
        } else {
            return null;
        }
    }

    //字符串系列化时间
    public static Date parseDate(String date, String formate) {
        try {
            return DateUtils.parseDate(date, formate);
        } catch (Exception e) {
            return null;
        }
    }

    //获得描述时间
    public static String describDate(Date date) {
        String desDate;
        Date beforeYes = getZeroTime(getModifyDate(getCurrentDate(), -2, null, null, null));
        Date yesZero = getZeroTime(getModifyDate(getCurrentDate(), -1, null, null, null));
        Date currZero = getZeroTime(getCurrentDate());
        Date tommZero = getZeroTime(getModifyDate(getCurrentDate(), 1, null, null, null));
        if (date.after(currZero) && date.before(tommZero)) {
            desDate = "今天 " + formatDate(date, "HH:mm");
        } else if (date.after(yesZero) && date.before(currZero)) {
            desDate = "昨天 " + formatDate(date, "HH:mm");
        } else if (date.after(beforeYes) && date.before(yesZero)) {
            desDate = "前天 " + formatDate(date, "HH:mm");
        } else {
            desDate = formatDate(date, YYYY_MM_DD_HH_MM);
        }
        return desDate;
    }

    //获取零点时间
    public static Date getZeroTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}
