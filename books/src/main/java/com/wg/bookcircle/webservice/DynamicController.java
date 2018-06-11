package com.wg.bookcircle.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.bookcircle.domain.BookCircleDynamic;
import com.wg.bookcircle.domain.BookCircleReply;
import com.wg.bookcircle.model.request.DynamicRequest;
import com.wg.bookcircle.model.response.BookCircleDynamicResponse;
import com.wg.bookcircle.model.response.DynamicReplyResponse;
import com.wg.common.Constant;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.ResponseContent;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.user.domain.UserToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.wg.common.utils.dbutils.DaoUtils.bookCircleDynamicDao;
import static com.wg.common.utils.dbutils.DaoUtils.userTokenDao;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-10
 * Time: 下午6:30
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class DynamicController {

    @Transactional
    @RequestMapping(value = "/api/dynamic/detail", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String detail(HttpServletRequest request,
                         HttpServletResponse response, DynamicRequest dynamicRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(dynamicRequest.getToken());
        BookCircleDynamic bookCircleDynamic = bookCircleDynamicDao.findOne(dynamicRequest.getDynamicId());
        BookCircleDynamicResponse bookCircleDynamicResponse = new BookCircleDynamicResponse(bookCircleDynamic, userToken);
        responseContent.setData(bookCircleDynamicResponse);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/dynamic/addReply", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String reply(HttpServletRequest request,
                        HttpServletResponse response, DynamicRequest dynamicRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(dynamicRequest.getToken());
        long dynamicId = dynamicRequest.getDynamicId();
        String content = dynamicRequest.getContent();
        int replyType = dynamicRequest.getReplyType();
        long replyObjId = dynamicRequest.getReplyObjId();
        BookCircleDynamic bookCircleDynamic = bookCircleDynamicDao.findOne(dynamicId);
        if (bookCircleDynamic != null) {
            if (replyObjId != Constant.ID_NOT_EXIST) {
                BookCircleReply bookCircleReply = AddUtils.addBookCircleReply(
                        bookCircleDynamic, userToken.getUserId(), content, replyType, replyObjId);
                responseContent.setData(new DynamicReplyResponse(bookCircleReply));
            } else {
                responseContent.update(ResponseCode.CANT_NOT_REPLY_GOOD);
            }
        } else {
            responseContent.update(ResponseCode.DYNAMIC_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }
}
