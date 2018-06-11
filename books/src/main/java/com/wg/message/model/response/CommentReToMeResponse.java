package com.wg.message.model.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.common.Constant;
import com.wg.common.Enum.bookgroup.ReplyType;
import com.wg.common.Enum.common.GoodType;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.bookgroup.domain.GroupReply;
import com.wg.message.domain.UserMessage;
import com.wg.message.utils.UserMsgUtils;
import com.wg.user.domain.UserGood;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserGoodUtils;
import com.wg.user.utils.UserUtils;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/9/11.
 */
public class CommentReToMeResponse {
    //newreply
    private JSONObject newReply = new JSONObject();

    //origincomment
    private JSONObject comment = new JSONObject();

    //originreply
    private JSONArray commentReplyList = new JSONArray();

    private Boolean isDirData;

    public CommentReToMeResponse(UserMessage userMessage, UserToken userToken) {
        try {
            //msg sender
            UserInfo newReplyer = DaoUtils.userInfoDao.findOne(userMessage.getSendUserId());
            if (newReplyer != null) {
                this.newReply.put("userId", newReplyer.getUserId());
                this.newReply.put("nickname", newReplyer.getNickname());
                this.newReply.put("avatar", Utils.getUrl(newReplyer.getAvatar()));
            }
            //msg content
            GroupReply newReply = null;
            UserGood newUserGood = null;
            long commentId = -1;
            if (userMessage.getMessageType() == MessageType.CommunityReply.getType()) {
                newReply = DaoUtils.groupReplyDao.findOne(userMessage.getMessageObjId());
                commentId = newReply.getCommentId();
                this.newReply.put("replyId", newReply.getReplyId());
                this.newReply.put("content", newReply.getContent());
                this.newReply.put("createdTime", newReply.getCreatedTime());
            } else if (userMessage.getMessageType() == MessageType.Good_Comment.getType()) {
                newUserGood = DaoUtils.userGoodDao.findOne(userMessage.getMessageObjId());
                commentId = newUserGood.getGoodObjId();
                this.newReply.put("replyId", Constant.ID_NOT_EXIST);
                this.newReply.put("content", UserMsgUtils.loadMsgText(userMessage));
                this.newReply.put("createdTime", newUserGood.getCreatedTime());
            }
            //comment
            GroupComment comment = DaoUtils.groupCommentDao.findOne(commentId);
            if (comment != null) {
                this.comment.put("commentId", comment.getCommentId());
                this.comment.put("content", comment.getContent());
                this.comment.put("goodNum", comment.getGoodNum());
                this.comment.put("replyNum", comment.getReplyNum());
                this.comment.put("createdTime", comment.getCreatedTime());
                //comment user
                UserInfo commentUserInfo = DaoUtils.userInfoDao.findOne(comment.getUserId());
                this.comment.put("userId", commentUserInfo.getUserId());
                this.comment.put("avatar", Utils.getUrl(commentUserInfo.getAvatar()));
                this.comment.put("nickname", UserUtils.getSafeNickname(commentUserInfo.getNickname()));
                //comment bookgroup
                BookGroup bookGroup = DaoUtils.bookGroupDao.findOne(comment.getCommunityId());
                this.comment.put("communityId", bookGroup.getCommunityId());
                this.comment.put("author", bookGroup.getAuthor());
                this.comment.put("title", bookGroup.getTitle());
                this.comment.put("subTitle", bookGroup.getSubTitle());
                boolean isGood = false;
                if (userToken != null) {
                    isGood = UserGoodUtils.isUserGood(comment.getCommentId(), userToken.getUserId(), GoodType.Comment.getType());
                }
                this.comment.put("good", isGood);
            }
            //replylist
            if (userMessage.getMessageType() == MessageType.CommunityReply.getType()) {
                HashSet<Long> ids = new HashSet<Long>();
                ids.add(userMessage.getAcceptUserId());
                ids.add(userMessage.getSendUserId());
                ids.add(comment.getUserId());
                //add contact replylist
                for (GroupReply reply : DaoUtils.groupReplyDao.findByCommentIdAndUserIdInAndCreatedTimeLessThanOrderByCreatedTimeAsc(comment.getCommentId(), ids, newReply.getCreatedTime())) {
                    try {
                        if (reply.getReplyType() == ReplyType.Reply.getType()) {
                            long objUserId = DaoUtils.groupReplyDao.findOne(reply.getReplyObjId()).getUserId();
                            if (objUserId == userMessage.getAcceptUserId() || objUserId == userMessage.getSendUserId() || objUserId == comment.getUserId()) {
                                loadReply(reply);//AS AA AC SS SA SC
                            }
                        } else {
                            loadReply(reply);//AC SC
                        }
                    } catch (Exception e) {
                    }
                }
                //add new replylist
                List<GroupReply> newReplyList = DaoUtils.groupReplyDao.findByReplyObjIdAndReplyTypeAndUserIdOrderByCreatedTimeAsc(newReply.getReplyId(), ReplyType.Reply.getType(), userMessage.getAcceptUserId());
                if (newReplyList.size() > 0) {
                    loadReply(newReply);//SA
                    for (GroupReply reply : newReplyList) {//AS
                        loadReply(reply);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isDirData = Boolean.TRUE;
        }
    }

    public void loadReply(GroupReply reply) {
        JSONObject jsonObject = new JSONObject();
        UserInfo replyUser = DaoUtils.userInfoDao.findOne(reply.getUserId());
        //user
        jsonObject.put("userId", replyUser.getUserId());
        jsonObject.put("nickname", replyUser.getNickname());
        //reply
        jsonObject.put("replyId", reply.getReplyId());
        jsonObject.put("replyType", reply.getReplyType());
        jsonObject.put("content", reply.getContent());
        jsonObject.put("createdTime", reply.getCreatedTime());
        //reply obj
        if (reply.getReplyType() == ReplyType.Reply.getType()) {
            UserInfo replyObjUser = DaoUtils.userInfoDao.findOne(DaoUtils.groupReplyDao.findOne(reply.getReplyObjId()).getUserId());
            jsonObject.put("replyObjUserId", replyObjUser.getUserId());
            jsonObject.put("replyObjNickname", replyObjUser.getNickname());
        }
        commentReplyList.add(jsonObject);
    }

    public JSONObject getNewReply() {
        return newReply;
    }

    public void setNewReply(JSONObject newReply) {
        this.newReply = newReply;
    }

    public JSONObject getComment() {
        return comment;
    }

    public void setComment(JSONObject comment) {
        this.comment = comment;
    }

    public JSONArray getCommentReplyList() {
        return commentReplyList;
    }

    public void setCommentReplyList(JSONArray commentReplyList) {
        this.commentReplyList = commentReplyList;
    }

    public Boolean getDirData() {
        return isDirData;
    }

    public void setDirData(Boolean dirData) {
        isDirData = dirData;
    }
}
