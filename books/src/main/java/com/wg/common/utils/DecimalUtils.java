package com.wg.common.utils;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/3/17 0017.
 */
public class DecimalUtils {
    public static final BigDecimal zero = BigDecimal.ZERO;

    //=0
    public static boolean equalZero(BigDecimal bd) {
        return zero.compareTo(bd) == 0;
    }

    //>0
    public static boolean greaterThanZero(BigDecimal bd) {
        return bd.compareTo(zero) == 1;
    }

    //<0
    public static boolean lessThanZero(BigDecimal bd) {
        return bd.compareTo(zero) == -1;
    }

    //new big decimal
    public static BigDecimal newDecimal(double val) {
        return new BigDecimal(val).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    //格式化保留两位输出
    public static String goldFormat(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    //保留n位小数,按某模式
    public static double doubleFormat(double val, int n, int mode) {
        return new BigDecimal(val).setScale(n, mode).doubleValue();
    }
}
