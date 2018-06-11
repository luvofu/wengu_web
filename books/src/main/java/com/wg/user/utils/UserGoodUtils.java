package com.wg.user.utils;

import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.user.domain.UserGood;

import static com.wg.common.utils.dbutils.DaoUtils.userGoodDao;

/**
 * Created by Administrator on 2016/9/28.
 */
public class UserGoodUtils {
    //删除点赞,通过点赞类型和对象id
    public static void deleteUserGood(long goodObjId, int goodType) {
        for (UserGood userGood : userGoodDao.findByGoodObjIdAndGoodType(goodObjId, goodType)) {
            DeleteUtils.deleteUserGood(userGood);
        }
    }

    //用户是否点赞
    public static boolean isUserGood(long goodObjId, long userId, int goodType) {
        if (userGoodDao.findByUserIdAndGoodObjIdAndGoodType(userId,goodObjId, goodType) != null) {
            return true;
        }
        return false;
    }
}
