package com.wg.user.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.common.Enum.common.CollectType;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.ResponseContent;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.user.domain.UserCollection;
import com.wg.user.domain.UserToken;
import com.wg.user.model.request.UserCollectionRequest;
import com.wg.user.utils.UserCollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.wg.common.utils.dbutils.DaoUtils.userCollectionDao;
import static com.wg.common.utils.dbutils.DaoUtils.userTokenDao;

/**
 * Created by Administrator on 2016/9/4.
 */
@Controller
public class UserCollectionController {

    @Transactional
    @RequestMapping(value = "api/userCollection/add", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String add(HttpServletRequest request,
                      HttpServletResponse response, ModelMap modelMap, UserCollectionRequest userCollectionRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userCollectionRequest.getToken());
        int collectType = userCollectionRequest.getCollectType();
        long collectObjId = userCollectionRequest.getCollectObjId();
        if (collectType == CollectType.Book.getType()
                || collectType == CollectType.BookSheet.getType()
                || collectType == CollectType.Picword.getType()) {
            AddUtils.addUserCollection(userToken.getUserId(), collectType, collectObjId);
        } else {
            responseContent.update(ResponseCode.UNKNOWN_COLLECTION_TYPE);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/userCollection/delete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserCollectionRequest userCollectionRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userCollectionRequest.getToken());
        int collectType = userCollectionRequest.getCollectType();
        long collectObjId = userCollectionRequest.getCollectObjId();
        UserCollection userCollection = userCollectionDao.findByUserIdAndCollectObjIdAndCollectType(userToken.getUserId(), collectObjId, collectType);
        if (userCollection != null) {
            if (userCollection.getUserId() == userToken.getUserId()) {
                DeleteUtils.deleteUserCollection(userCollection);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/userCollection/book", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String book(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, UserCollectionRequest userCollectionRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userCollectionRequest.getToken());
        responseContent.putData("total", userCollectionDao.countByUserIdAndCollectType(
                userToken.getUserId(), CollectType.Book.getType()));
        responseContent.putData("bookList", UserCollectionUtils.getUserCollectionResponse(
                CollectType.Book.getType(), userToken.getUserId(), userCollectionRequest.getPage(), userToken));
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/userCollection/bookSheet", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookSheet(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserCollectionRequest userCollectionRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userCollectionRequest.getToken());
        long userId = userCollectionRequest.getUserId();
        responseContent.putData("relationType", Utils.getPermission(userToken, userId));
        responseContent.putData("total", userCollectionDao.countByUserIdAndCollectType(
                userId, CollectType.BookSheet.getType()));
        responseContent.putData("bookSheetList", UserCollectionUtils.getUserCollectionResponse(
                CollectType.BookSheet.getType(), userId, userCollectionRequest.getPage(), userToken));
        return JSON.toJSONString(responseContent);
    }

    /* version 111 * version 111 * version 111 * version 111 * version 111 * version 111 * version 111 * version 111 * version 111 */
    @Transactional
    @RequestMapping(value = "api/userCollection/picword", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String picword(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap, UserCollectionRequest userCollectionRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userCollectionRequest.getToken());
        responseContent.putData("picwordList", UserCollectionUtils.getUserCollectionResponse(
                CollectType.Picword.getType(), userToken.getUserId(), userCollectionRequest.getPage(), userToken));
        return JSON.toJSONString(responseContent);
    }

}
