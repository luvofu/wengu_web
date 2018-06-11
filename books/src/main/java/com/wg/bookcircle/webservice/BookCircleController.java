package com.wg.bookcircle.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.bookcircle.domain.BookCircleDynamic;
import com.wg.bookcircle.model.request.BookCircleRequest;
import com.wg.bookcircle.model.response.BookCircleDynamicResponse;
import com.wg.common.Constant;
import com.wg.common.Enum.bookcircle.DynamicType;
import com.wg.common.Enum.common.Permission;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.PropConfig;
import com.wg.common.ResponseContent;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.user.domain.UserToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-10
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class BookCircleController {

    @Transactional
    @RequestMapping(value = "/api/bookCircle/dynamic", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String dynamic(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap, BookCircleRequest bookCircleRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookCircleRequest.getToken());
        if (userInfoDao.findOne(bookCircleRequest.getUserId()) != null) {
            int permission = Utils.getPermission(userToken, bookCircleRequest.getUserId());
            Pageable pageable = new PageRequest(bookCircleRequest.getPage(), Constant.PAGE_NUM_MEDIUM);
            Slice<BookCircleDynamic> bookCircleDynamicPage;
            if (permission == Permission.Personal.getType()) {
                HashSet<Long> ids = new HashSet<Long>(userFriendDao.getFriendUserIds(userToken.getUserId()));
                ids.add(userToken.getUserId());
                ids.add(PropConfig.OFFICER_USERID);
                bookCircleDynamicPage = bookCircleDynamicDao.findByUserIdInAndPermissionLessThanOrderByCreatedTimeDesc(
                        ids, permission, pageable);
            } else {
                bookCircleDynamicPage = bookCircleDynamicDao.findByUserIdAndPermissionLessThanOrderByCreatedTimeDesc(
                        bookCircleRequest.getUserId(), permission + 1, pageable);
            }
            List<BookCircleDynamicResponse> bookCircleDynamicResponseList = new ArrayList<BookCircleDynamicResponse>();
            for (BookCircleDynamic bookCircleDynamic : bookCircleDynamicPage.getContent()) {
                bookCircleDynamicResponseList.add(new BookCircleDynamicResponse(bookCircleDynamic, userToken));
            }
            responseContent.putData("bookCircleDynamicList", bookCircleDynamicResponseList);
            responseContent.putData("relationType", permission);
        } else {
            responseContent.update(ResponseCode.USER_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookCircle/addDynamic", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String add(HttpServletRequest request,
                      HttpServletResponse response, ModelMap modelMap, BookCircleRequest bookCircleRequest, @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookCircleRequest.getToken());
        BookCircleDynamic bookCircleDynamic = AddUtils.addBookCircleDynamic(
                userToken.getUserId(),
                bookCircleRequest.getContent(),
                bookCircleRequest.getLinkId(),
                bookCircleRequest.getLinkType(),
                bookCircleRequest.getLocation(),
                file,
                bookCircleRequest.getPermission(),
                DynamicType.Personal.getType());
        if (bookCircleDynamic == null) {
            responseContent.update(ResponseCode.CREATE_FAILD);
        }
        return JSON.toJSONString(responseContent);
    }
}
