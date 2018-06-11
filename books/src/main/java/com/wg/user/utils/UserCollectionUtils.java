package com.wg.user.utils;

import com.wg.book.model.response.BookEntityResponse;
import com.wg.booksheet.model.response.BookSheetEntityResponse;
import com.wg.common.Constant;
import com.wg.common.Enum.common.CollectType;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.picword.model.response.PicwordEntityResponse;
import com.wg.user.domain.UserCollection;
import com.wg.user.domain.UserToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/12/23 0023.
 */
public class UserCollectionUtils {
    //返回用户收藏的书、书单、图文
    public static List<Object> getUserCollectionResponse(int collectType, long userId, int page, UserToken userToken) {
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        Slice<UserCollection> userCollectionSlice = userCollectionDao.findByUserIdAndCollectTypeOrderByCreatedTimeDesc(
                userId, collectType, pageable);
        List<Object> collectObjectList = new ArrayList<Object>();
        if (collectType == CollectType.Book.getType()) {
            for (UserCollection userCollection : userCollectionSlice.getContent()) {
                collectObjectList.add(new BookEntityResponse(bookDao.findOne(userCollection.getCollectObjId())));
            }
        } else if (collectType == CollectType.BookSheet.getType()) {
            for (UserCollection userCollection : userCollectionSlice.getContent()) {
                collectObjectList.add(new BookSheetEntityResponse(bookSheetDao.findOne(userCollection.getCollectObjId())));
            }
        } else if (collectType == CollectType.Picword.getType()) {
            for (UserCollection userCollection : userCollectionSlice.getContent()) {
                collectObjectList.add(new PicwordEntityResponse(picwordDao.findOne(userCollection.getCollectObjId()), userToken));
            }
        }
        return collectObjectList;
    }

    //用户是否收藏
    public static boolean isUserCollect(long collectObjId, long userId, int collectType) {
        if (userCollectionDao.findByUserIdAndCollectObjIdAndCollectType(userId, collectObjId, collectType) != null) {
            return true;
        }
        return false;
    }

    //删除收藏,通过类型和对象
    public static void deleteUserCollection(long collectObjId, int collectType) {
        //delete collection
        for (UserCollection userCollection : userCollectionDao.findByCollectObjIdAndCollectType(collectObjId, collectType)) {
            DeleteUtils.deleteUserCollection(userCollection);
        }
    }
}
