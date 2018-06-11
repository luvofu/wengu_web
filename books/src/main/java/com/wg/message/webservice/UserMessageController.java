package com.wg.message.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.bookcircle.domain.BookCircleDynamic;
import com.wg.bookcircle.model.response.BookCircleDynamicResponse;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.bookgroup.model.response.BookCommunityCommentResponse;
import com.wg.common.Constant;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.message.DealStatus;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.MsgOptType;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.ResponseContent;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.message.domain.UserMessage;
import com.wg.message.model.request.UserMessageRequest;
import com.wg.message.model.response.CommentReToMeResponse;
import com.wg.message.model.response.DynamicReToMeResponse;
import com.wg.message.utils.UserMsgUtils;
import com.wg.user.domain.UserFriend;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/9/11.
 */
@Controller
public class UserMessageController {

    @Transactional
    @RequestMapping(value = "/api/userMessage/invite", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String invite(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        responseContent.putData("userMessageList", UserMsgUtils.getMessages(
                userToken, MsgOptType.ByTypes.getType(), String.valueOf(MessageType.FriendInvite.getType()), userMessageRequest.getPage()));
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/new", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String newMsg(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        responseContent.setData(UserMsgUtils.getMessages(userToken, MsgOptType.NotRead.getType(), "", userMessageRequest.getPage()));
        userMessageDao.setUserMsgsRead(userToken.getUserId(), ReadStatus.Read.getStatus());
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/delete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        UserMessage userMessage = userMessageDao.findOne(userMessageRequest.getMessageId());
        if (userMessage.getAcceptUserId() == userToken.getUserId()) {
            userMessageDao.delete(userMessage);
        } else {
            responseContent.update(ResponseCode.ILLEGAL_USER);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/friendInvite", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String friendInvite(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        UserInfo friendUserInfo = userInfoDao.findOne(userMessageRequest.getAcceptUserId());
        if (friendUserInfo != null) {
            UserFriend userFriend = userFriendDao.findByUserIdAndFriendId(userToken.getUserId(), friendUserInfo.getUserId());
            UserFriend friendUser = userFriendDao.findByUserIdAndFriendId(friendUserInfo.getUserId(), userToken.getUserId());
            if (userFriend == null && friendUser == null) {
                List<Integer> msgTypes = new ArrayList<Integer>();
                msgTypes.add(MessageType.FriendInvite.getType());
                UserMsgUtils.deleteUserMessage(friendUserInfo.getUserId(), userToken.getUserId(), msgTypes);
                AddUtils.addUserMessage(
                        friendUserInfo.getUserId(),
                        userToken.getUserId(),
                        userMessageRequest.getContent(),
                        MessageType.FriendInvite.getType(),
                        userToken.getUserId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            } else {
                responseContent.update(ResponseCode.WAS_FRIEND);
            }
        } else {
            responseContent.update(ResponseCode.USER_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/friendDelete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String friendDelete(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        UserInfo friend = userInfoDao.findOne(userMessageRequest.getAcceptUserId());
        if (friend != null) {
            UserFriend myUf = userFriendDao.findByUserIdAndFriendId(userToken.getUserId(), friend.getUserId());
            UserFriend friendUf = userFriendDao.findByUserIdAndFriendId(friend.getUserId(), userToken.getUserId());
            if (myUf != null && friendUf != null) {
                //delete userfriend
                userFriendDao.delete(myUf);
                userFriendDao.delete(friendUf);
                //delete message
                List<Integer> msgTypes = new ArrayList<Integer>();
                msgTypes.add(MessageType.FriendInvite.getType());
                msgTypes.add(MessageType.AgreeFriendInvite.getType());
                UserMsgUtils.deleteUserMessage(myUf.getUserId(), friendUf.getUserId(), msgTypes);
                UserMsgUtils.deleteUserMessage(friendUf.getUserId(), myUf.getUserId(), msgTypes);
            } else {
                responseContent.update(ResponseCode.NOT_FRIEND);
            }
        } else {
            responseContent.update(ResponseCode.USER_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/agreeFriendInvite", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String agreeFriendInvite(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        UserMessage userMessage = userMessageDao.findOne(userMessageRequest.getMessageId());
        if (userMessage.getAcceptUserId() == userToken.getUserId()) {
            UserFriend myUf = userFriendDao.findByUserIdAndFriendId(userMessage.getAcceptUserId(), userMessage.getSendUserId());
            UserFriend friendUf = userFriendDao.findByUserIdAndFriendId(userMessage.getSendUserId(), userMessage.getAcceptUserId());
            if (myUf == null && friendUf == null) {
                myUf = new UserFriend();
                myUf.setUserId(userMessage.getAcceptUserId());
                myUf.setFriendId(userMessage.getSendUserId());
                myUf = userFriendDao.save(myUf);

                friendUf = new UserFriend();
                friendUf.setUserId(userMessage.getSendUserId());
                friendUf.setFriendId(userMessage.getAcceptUserId());
                friendUf = userFriendDao.save(friendUf);
            }
            userMessage.setDealStatus(DealStatus.Accept.getStatus());
            userMessage = userMessageDao.save(userMessage);
            //add message
            AddUtils.addUserMessage(
                    userMessage.getSendUserId(),
                    userMessage.getAcceptUserId(),
                    null,
                    MessageType.AgreeFriendInvite.getType(),
                    userToken.getUserId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        } else {
            responseContent.update(ResponseCode.ILLEGAL_USER);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/dynamicPublish", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String dynamicPublish(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        Pageable pageable = new PageRequest(userMessageRequest.getPage(), Constant.PAGE_NUM_MEDIUM);
        Slice<BookCircleDynamic> bookCircleDynamicSlice = bookCircleDynamicDao.findByUserIdOrderByCreatedTimeDesc(userToken.getUserId(), pageable);
        List<BookCircleDynamicResponse> bookCircleDynamicResponseList = new ArrayList<BookCircleDynamicResponse>();
        for (BookCircleDynamic bookCircleDynamic : bookCircleDynamicSlice.getContent()) {
            bookCircleDynamicResponseList.add(new BookCircleDynamicResponse(bookCircleDynamic, userToken));
        }
        responseContent.putData("dynamicPublishList", bookCircleDynamicResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/dynamicRelativeToMe", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String dynamicRelativeToMe(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        List<DynamicReToMeResponse> dynamicReToMeResponseList = new ArrayList<DynamicReToMeResponse>();
        List<Integer> messageTypeList = new ArrayList<Integer>();
        messageTypeList.add(MessageType.BookCircleReply.getType());
        messageTypeList.add(MessageType.Good_Dynamic.getType());
        Pageable pageable = new PageRequest(userMessageRequest.getPage(), Constant.PAGE_NUM_SMALL);
        Slice<UserMessage> userMessageSlice = userMessageDao.findByAcceptUserIdAndMessageTypeInOrderByCreatedTimeDesc(userToken.getUserId(), messageTypeList, pageable);
        for (UserMessage userMessage : userMessageSlice.getContent()) {
            DynamicReToMeResponse dynamicReToMeResponse = new DynamicReToMeResponse(userMessage, userToken);
            if (dynamicReToMeResponse.getDirData() == null) {
                dynamicReToMeResponseList.add(dynamicReToMeResponse);
            } else {
                userMessageDao.delete(userMessage);
            }
        }
        responseContent.putData("dynamicRelativeToMeList", dynamicReToMeResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/commentPublish", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String commentPublish(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        Pageable pageable = new PageRequest(userMessageRequest.getPage(), Constant.PAGE_NUM_LARGE);
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        List<BookCommunityCommentResponse> bookCommunityCommentResponseList = new ArrayList<BookCommunityCommentResponse>();
        Slice<GroupComment> communityCommentSlice = groupCommentDao.findByUserIdOrderByCreatedTimeDesc(userToken.getUserId(), pageable);
        for (GroupComment groupComment : communityCommentSlice.getContent()) {
            bookCommunityCommentResponseList.add(new BookCommunityCommentResponse(groupComment, userToken));
        }
        responseContent.putData("commentPublishList", bookCommunityCommentResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/commentRelativeToMe", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String commentRelativeToMe(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        List<CommentReToMeResponse> commentReToMeResponseList = new ArrayList<CommentReToMeResponse>();
        List<Integer> msgTypes = new ArrayList<Integer>();
        msgTypes.add(MessageType.CommunityReply.getType());
        msgTypes.add(MessageType.Good_Comment.getType());
        Pageable pageable = new PageRequest(userMessageRequest.getPage(), Constant.PAGE_NUM_SMALL);
        Slice<UserMessage> userMessageSlice = userMessageDao.findByAcceptUserIdAndMessageTypeInOrderByCreatedTimeDesc(
                userToken.getUserId(), msgTypes, pageable);
        for (UserMessage userMessage : userMessageSlice.getContent()) {
            CommentReToMeResponse commentReToMeResponse = new CommentReToMeResponse(userMessage, userToken);
            if (commentReToMeResponse.getDirData() == null) {
                commentReToMeResponseList.add(commentReToMeResponse);
            } else {
                userMessageDao.delete(userMessage);
            }
        }
        responseContent.putData("commentRelativeToMeList", commentReToMeResponseList);
        return JSON.toJSONString(responseContent);
    }

    /*version 102**version 102**version 102**version 102**version 102**version 102**version 102**version 102**version 102*/
    @Transactional
    @RequestMapping(value = "/api/userMessage/personal", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String personal(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        String messageTypes = userMessageRequest.getMessageTypes();
        if (StringUtils.isNotBlank(messageTypes)) {
            responseContent.putData("userMessageList", UserMsgUtils.getMessages(
                    userToken, MsgOptType.ByTypes.getType(), messageTypes, userMessageRequest.getPage()));
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/new_v102", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String new_102(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        responseContent.setData(UserMsgUtils.getMessages(userToken, MsgOptType.NotRead.getType(), "", userMessageRequest.getPage()));
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userMessage/read", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String read(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        String messageTypes = userMessageRequest.getMessageTypes();
        if (messageTypes != null) {
            UserMsgUtils.readMessages(userToken, MsgOptType.ByTypes.getType(), messageTypes);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //读消息
    @Transactional
    @RequestMapping(value = "/api/userMessage/readEx", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String readEx(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        int msgOptType = userMessageRequest.getMsgReadType();
        String params = msgOptType == MsgOptType.ByIds.getType() ?
                userMessageRequest.getMessageIds() : userMessageRequest.getMessageTypes();
        if (params != null) {
            UserMsgUtils.readMessages(userToken, msgOptType, params);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //取消息
    @Transactional
    @RequestMapping(value = "/api/userMessage/personalEx", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String personalEx(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        int msgOptType = userMessageRequest.getMsgGetType();
        String params = msgOptType == MsgOptType.ByIds.getType() ?
                userMessageRequest.getMessageIds() : userMessageRequest.getMessageTypes();
        if (params != null) {
            responseContent.putData("userMessageList", UserMsgUtils.getMessages(
                    userToken, msgOptType, params, userMessageRequest.getPage()));
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //////////////////////////////////////////////////////version 202///////////////////////////////////////////////////
    //用户动态消息
    @Transactional
    @RequestMapping(value = "/api/userMessage/dynamicMsgs", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String dynamicMsgs(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        responseContent.putData("userMessageList", UserMsgUtils.getMessages(
                userToken, MsgOptType.DynamicMsgs.getType(), "", userMessageRequest.getPage()));
        return JSON.toJSONString(responseContent);
    }

    //系统通知消息
    @Transactional
    @RequestMapping(value = "/api/userMessage/sysMsgs", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String sysMsgs(HttpServletRequest request, HttpServletResponse response, UserMessageRequest userMessageRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userMessageRequest.getToken());
        responseContent.putData("userMessageList", UserMsgUtils.getMessages(
                userToken, MsgOptType.SysMsgs.getType(), "", userMessageRequest.getPage()));
        return JSON.toJSONString(responseContent);
    }

}
