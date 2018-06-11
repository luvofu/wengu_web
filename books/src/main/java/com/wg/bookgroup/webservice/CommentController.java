package com.wg.bookgroup.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.bookgroup.domain.GroupReply;
import com.wg.bookgroup.model.request.CommunityCommentRequest;
import com.wg.bookgroup.model.response.BookCommunityCommentResponse;
import com.wg.bookgroup.model.response.CommentReplyResponse;
import com.wg.common.Constant;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.ResponseContent;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.user.domain.UserToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-3
 * Time: 下午1:47
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class CommentController {

    @Transactional
    @RequestMapping(value = "/api/comment/lib", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String lib(HttpServletRequest request,
                      HttpServletResponse response, ModelMap modelMap, CommunityCommentRequest communityCommentRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = null;
        if (communityCommentRequest.getToken() != null) {
            userToken = userTokenDao.findByToken(communityCommentRequest.getToken());
        }
        Pageable pageable = new PageRequest(communityCommentRequest.getPage(), Constant.PAGE_NUM_LARGE);
        Slice<GroupComment> communityCommentSlice = groupCommentDao.findAllByOrderByCreatedTimeDesc(pageable);

        List<BookCommunityCommentResponse> bookCommunityCommentResponseList = new ArrayList<BookCommunityCommentResponse>();
        for (GroupComment groupComment : communityCommentSlice.getContent()) {
            bookCommunityCommentResponseList.add(new BookCommunityCommentResponse(groupComment, userToken));
        }
        responseContent.putData("communityCommentList", bookCommunityCommentResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/comment/detail", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String head(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, CommunityCommentRequest communityCommentRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityCommentRequest.getToken());
        GroupComment groupComment = groupCommentDao.findOne(communityCommentRequest.getCommentId());
        if (groupComment != null) {
            BookCommunityCommentResponse bookCommunityCommentResponse = new BookCommunityCommentResponse(groupComment, userToken);

            responseContent.setData(bookCommunityCommentResponse);
        } else {
            responseContent.update(ResponseCode.COMMENT_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/comment/reply", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String commentDetail(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap, CommunityCommentRequest communityCommentRequest) {
        ResponseContent responseContent = new ResponseContent();
        GroupComment groupComment = groupCommentDao.findOne(communityCommentRequest.getCommentId());
        if (groupComment != null) {
            Pageable pageable = new PageRequest(communityCommentRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<GroupReply> communityReplySlice = groupReplyDao.findByCommentIdOrderByCreatedTimeDesc(communityCommentRequest.getCommentId(), pageable);
            List<CommentReplyResponse> commentReplyResponseList = new ArrayList<CommentReplyResponse>();
            for (GroupReply groupReply : communityReplySlice.getContent()) {
                commentReplyResponseList.add(new CommentReplyResponse(groupReply));
            }
            responseContent.putData("commentReplyList", commentReplyResponseList);
        } else {
            responseContent.update(ResponseCode.COMMENT_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/comment/addReply", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String reply(HttpServletRequest request,
                        HttpServletResponse response, ModelMap modelMap, CommunityCommentRequest communityCommentRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityCommentRequest.getToken());
        long commentId = communityCommentRequest.getCommentId();
        String content = communityCommentRequest.getContent();
        int replyType = communityCommentRequest.getReplyType();
        long replyObjId = communityCommentRequest.getReplyObjId();
        GroupComment groupComment = groupCommentDao.findOne(commentId);
        if (groupComment != null) {
            if (replyObjId != Constant.ID_NOT_EXIST) {
                GroupReply groupReply = AddUtils.addCommunityReply(
                        groupComment, userToken.getUserId(), content, replyType, replyObjId);
                responseContent.setData(new CommentReplyResponse(groupReply));
            } else {
                responseContent.update(ResponseCode.CANT_NOT_REPLY_GOOD);
            }
        } else {
            responseContent.update(ResponseCode.COMMENT_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }
}
