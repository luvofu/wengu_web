package com.wg.message.model.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.book.domain.Book;
import com.wg.bookcircle.domain.BookCircleDynamic;
import com.wg.bookcircle.domain.BookCircleReply;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.booksheet.domain.BookSheet;
import com.wg.common.Constant;
import com.wg.common.Enum.bookcircle.LinkType;
import com.wg.common.Enum.bookcircle.ReplyType;
import com.wg.common.Enum.common.GoodType;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.utils.Utils;
import com.wg.message.domain.UserMessage;
import com.wg.message.utils.UserMsgUtils;
import com.wg.user.domain.UserGood;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserGoodUtils;
import com.wg.user.utils.UserUtils;

import java.util.HashSet;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/9/11.
 */
public class DynamicReToMeResponse {
    //new reply
    private JSONObject newReply = new JSONObject();

    //dynamic
    private JSONObject dynamic = new JSONObject();

    //replylist
    private JSONArray dynamicReplyList = new JSONArray();

    private Boolean isDirData;

    public DynamicReToMeResponse(UserMessage userMessage, UserToken userToken) {
        try {
            //msg sender
            UserInfo newReplyer = userInfoDao.findOne(userMessage.getSendUserId());
            newReply.put("userId", newReplyer.getUserId());
            newReply.put("nickname", newReplyer.getNickname());
            newReply.put("avatar", Utils.getUrl(newReplyer.getAvatar()));

            //msg content
            BookCircleReply newReply = null;
            long dynamicId = -1;
            if (userMessage.getMessageType() == MessageType.BookCircleReply.getType()) {
                newReply = bookCircleReplyDao.findOne(userMessage.getMessageObjId());
                dynamicId = newReply.getDynamicId();
                this.newReply.put("replyId", newReply.getReplyId());
                this.newReply.put("content", newReply.getContent());
                this.newReply.put("createdTime", newReply.getCreatedTime());
            } else if (userMessage.getMessageType() == MessageType.Good_Dynamic.getType()) {
                UserGood newGood = userGoodDao.findOne(userMessage.getMessageObjId());
                dynamicId = newGood.getGoodObjId();
                this.newReply.put("replyId", Constant.ID_NOT_EXIST);
                this.newReply.put("content", UserMsgUtils.loadMsgText(userMessage));
                this.newReply.put("createdTime", newGood.getCreatedTime());
            }
            //dynamic
            BookCircleDynamic dynamic = bookCircleDynamicDao.findOne(dynamicId);
            this.dynamic.put("dynamicId", dynamic.getDynamicId());
            this.dynamic.put("content", dynamic.getContent());
            this.dynamic.put("image", Utils.getUrl(dynamic.getImage()));
            this.dynamic.put("location", dynamic.getLocation());
            this.dynamic.put("dynamicType", dynamic.getDynamicType());
            this.dynamic.put("goodNum", dynamic.getGoodNum());
            this.dynamic.put("replyNum", dynamic.getReplyNum());
            this.dynamic.put("createdTime", dynamic.getCreatedTime());
            //dynamic user
            UserInfo dynamicUser = userInfoDao.findOne(dynamic.getUserId());
            this.dynamic.put("userId", dynamicUser.getUserId());
            this.dynamic.put("nickname", dynamicUser.getNickname());
            this.dynamic.put("avatar", Utils.getUrl(dynamicUser.getAvatar()));
            //link
            int linkType = dynamic.getLinkType();
            long linkId = dynamic.getLinkId();
            if (linkType == LinkType.Book.getType()) {
                Book book = bookDao.findOne(linkId);
                if (book != null) {
                    this.dynamic.put("title", book.getTitle());
                    this.dynamic.put("subTitle", book.getSubTitle());
                    this.dynamic.put("author", book.getAuthor());
                    this.dynamic.put("bookCover", Utils.getUrl(book.getCover()));
                } else {
                    linkId = -1;
                }
            } else if (linkType == LinkType.BookSheet.getType()) {
                BookSheet bookSheet = bookSheetDao.findOne(linkId);
                if (bookSheet != null) {
                    this.dynamic.put("name", bookSheet.getName());
                    this.dynamic.put("bookSheetCover", Utils.getUrl(bookSheet.getCover()));
                } else {
                    linkId = -1;
                }
            } else if (linkType == LinkType.Comment.getType()) {
                GroupComment comment = groupCommentDao.findOne(linkId);
                if (comment != null) {
                    this.dynamic.put("commentContent", comment.getContent());
                    this.dynamic.put("communityId", comment.getCommunityId());
                    UserInfo commentUserInfo = userInfoDao.findOne(comment.getUserId());
                    if (commentUserInfo != null) {
                        this.dynamic.put("commentUserId", commentUserInfo.getUserId());
                        this.dynamic.put("commentNickname", UserUtils.getSafeNickname(commentUserInfo.getNickname()));
                    }
                    BookGroup community = bookGroupDao.findOne(comment.getCommunityId());
                    if (community != null) {
                        this.dynamic.put("communityTitle", community.getTitle());
                        this.dynamic.put("communitySubTitle", community.getSubTitle());
                        this.dynamic.put("communityAuthor", community.getAuthor());
                    }
                } else {
                    linkId = -1;
                }
            }
            this.dynamic.put("linkType", linkType);
            this.dynamic.put("linkId", linkId);
            //good
            boolean isGood = false;
            if (userToken != null) {
                isGood = UserGoodUtils.isUserGood(dynamic.getDynamicId(), userToken.getUserId(), GoodType.Dynamic.getType());
            }
            this.dynamic.put("good", isGood);
            //reply list
            if (userMessage.getMessageType() == MessageType.BookCircleReply.getType()) {
                HashSet<Long> ids = new HashSet<Long>();
                ids.add(userMessage.getAcceptUserId());
                ids.add(userMessage.getSendUserId());
                ids.add(dynamic.getUserId());
                //add contact replylist
                for (BookCircleReply reply : bookCircleReplyDao.findByDynamicIdAndUserIdInAndCreatedTimeLessThanOrderByCreatedTimeAsc(
                        dynamic.getDynamicId(), ids, newReply.getCreatedTime())) {
                    try {
                        if (reply.getReplyType() == ReplyType.Reply.getType()) {
                            long objUserId = bookCircleReplyDao.findOne(reply.getReplyObjId()).getUserId();
                            if (objUserId == userMessage.getAcceptUserId() || objUserId == userMessage.getSendUserId() || objUserId == dynamic.getUserId()) {
                                loadReply(reply);//AS AA AD SS SA SD
                            }
                        } else {
                            loadReply(reply);//AD SD
                        }
                    } catch (Exception e) {
                    }
                }
                //add new replylist
                List<BookCircleReply> newReplyList = bookCircleReplyDao.findByReplyObjIdAndReplyTypeAndUserIdOrderByCreatedTimeAsc(
                        newReply.getReplyId(), ReplyType.Reply.getType(), userMessage.getAcceptUserId());
                if (newReplyList.size() > 0) {
                    loadReply(newReply);//SA
                    for (BookCircleReply reply : newReplyList) {
                        loadReply(reply);//AS
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isDirData = Boolean.TRUE;
        }
    }

    public void loadReply(BookCircleReply reply) {
        JSONObject jsonObject = new JSONObject();
        //reply
        UserInfo replyUser = userInfoDao.findOne(reply.getUserId());
        jsonObject.put("userId", replyUser.getUserId());
        jsonObject.put("nickname", replyUser.getNickname());
        jsonObject.put("avatar", Utils.getUrl(replyUser.getAvatar()));
        jsonObject.put("replyId", reply.getReplyId());
        jsonObject.put("replyType", reply.getReplyType());
        jsonObject.put("content", reply.getContent());
        jsonObject.put("createdTime", reply.getCreatedTime());
        //reply obj
        if (reply.getReplyType() == ReplyType.Reply.getType()) {
            UserInfo replyObjUser = userInfoDao.findOne(bookCircleReplyDao.findOne(reply.getReplyObjId()).getUserId());
            JSONObject ob = new JSONObject();
            ob.put("replyObjId", reply.getReplyObjId());
            ob.put("userId", replyObjUser.getUserId());
            ob.put("nickname", replyObjUser.getNickname());
            jsonObject.put("replyObj", ob);
        }
        dynamicReplyList.add(jsonObject);
    }

    public JSONObject getNewReply() {
        return newReply;
    }

    public void setNewReply(JSONObject newReply) {
        this.newReply = newReply;
    }

    public JSONObject getDynamic() {
        return dynamic;
    }

    public void setDynamic(JSONObject dynamic) {
        this.dynamic = dynamic;
    }

    public JSONArray getDynamicReplyList() {
        return dynamicReplyList;
    }

    public void setDynamicReplyList(JSONArray dynamicReplyList) {
        this.dynamicReplyList = dynamicReplyList;
    }

    public Boolean getDirData() {
        return isDirData;
    }

    public void setDirData(Boolean dirData) {
        isDirData = dirData;
    }
}
