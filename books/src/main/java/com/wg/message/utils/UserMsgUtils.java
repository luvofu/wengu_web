package com.wg.message.utils;

import com.wg.book.domain.Book;
import com.wg.bookcircle.domain.BookCircleDynamic;
import com.wg.bookcircle.domain.BookCircleReply;
import com.wg.bookorder.domain.BookOrder;
import com.wg.booksheet.domain.BookSheet;
import com.wg.common.Constant;
import com.wg.common.Enum.bookorder.OrderType;
import com.wg.common.Enum.bookorder.UserOrderType;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.MsgLinkType;
import com.wg.common.Enum.message.MsgOptType;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.Enum.userbook.CheckStatus;
import com.wg.common.utils.Utils;
import com.wg.community.domain.Community;
import com.wg.message.domain.UserMessage;
import com.wg.message.model.response.MessageResponse;
import com.wg.message.model.response.MessageResponseEx;
import com.wg.user.domain.UserCollection;
import com.wg.user.domain.UserGood;
import com.wg.user.domain.UserToken;
import com.wg.useraccount.domain.UserBill;
import com.wg.userbook.domain.BookCheck;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.*;

@Component
public class UserMsgUtils {
    public static HashMap<Integer, MessageType> mtMap = new HashMap<Integer, MessageType>();
    public static final int[] orderMsgRange = {1000, 2000};
    public static final int[] billMsgRange = {2000, 3000};
    public static final int[] commMsgRange = {3000, 4000};

    @PostConstruct
    void init() {
        logger.info("UserMessageUtils construct");
        for (MessageType messageType : MessageType.values()) {
            mtMap.put(messageType.getType(), messageType);
        }
    }

    //消息文本装载
    public static String loadMsgText(UserMessage userMessage) {
        long objId = userMessage.getMessageObjId();
        int msgType = userMessage.getMessageType();
        String text = MessageType.getText(msgType);
        String content = userMessage.getContent();
        if (msgType > orderMsgRange[0] && msgType < orderMsgRange[1]) {
            BookOrder bookOrder = bookOrderDao.findOne(objId);
            Book book = bookDao.findOne(bookOrder.getBookId());
            String type = UserOrderType.getTypeInfo(OrderType.getUserOrderType(
                    bookOrder.getOrderType(), userMessage.getAcceptUserId() == bookOrder.getUserId()));
            text = type + "订单:《" + book.getTitle() + "》," + text;
        }
        if (msgType > billMsgRange[0] && msgType < billMsgRange[1]) {
            if (msgType == MessageType.ACC_ORDER_APPLER_BILL.getType() || msgType == MessageType.ACC_ORDER_OWNER_BILL.getType()) {
                UserBill userBill = userBillDao.findOne(userMessage.getMessageObjId());
                BookOrder bookOrder = bookOrderDao.findByOrderNumber(userBill.getOrderNumber());
                Book book = bookDao.findOne(bookOrder.getBookId());
                String type = UserOrderType.getTypeInfo(OrderType.getUserOrderType(
                        bookOrder.getOrderType(), userMessage.getAcceptUserId() == bookOrder.getUserId()));
                text = text.replace("type", type).replace("title", book.getTitle());
            }
        } else if (msgType > commMsgRange[0] && msgType < commMsgRange[1]) {
            Community community = communityDao.findOne(userMessage.getMessageObjId());
            if (msgType == MessageType.COMM_JOIN_VERTIFY.getType()) {
                text = text + "：[" + community.getName() + "]" + "\n" + (StringUtils.isNotBlank(content) ? content : "无");
            } else if (msgType == MessageType.COMM_ACCEPTE_JOIN.getType() || msgType == MessageType.COMM_INVITE_JOIN.getType() ||
                    msgType == MessageType.COMM_QUITE.getType() || msgType == MessageType.COMM_EXPLE_OUT.getType()) {
                text = text + "：[" + community.getName() + "]";
            } else if (msgType == MessageType.COMM_TRANS_OWNER.getType() || msgType == MessageType.COMM_SET_MANAGER.getType()) {
                text = text + "\n来自社区：[" + community.getName() + "]";
            }
        } else if (msgType == MessageType.FriendInvite.getType()) {
            text = text + "\n附加消息：" + (StringUtils.isNotBlank(content) ? content : "无");
        } else if (msgType == MessageType.Collect_BookSheet.getType()) {
            UserCollection userCollection = userCollectionDao.findOne(objId);
            BookSheet booksheet = bookSheetDao.findOne(userCollection.getCollectObjId());
            text = text + ":【" + booksheet.getName() + "】";
        }
        return text;
    }

    //设置消息link&content
    public static void loadMsgLink(UserMessage userMessage, MessageResponseEx msgResEx) {
        if (msgResEx.getDir() == null) {
            try {
                int type = userMessage.getMessageType();
                if (type == MessageType.Good_Dynamic.getType()) {
                    UserGood userGood = userGoodDao.findOne(userMessage.getMessageObjId());
                    BookCircleDynamic dynamic = bookCircleDynamicDao.findOne(userGood.getGoodObjId());
                    msgResEx.setMsgLinkType(MsgLinkType.Dynamic.getType());
                    msgResEx.setMsgLinkId(dynamic.getDynamicId());
                    msgResEx.setMsgLinkContent(dynamic.getContent());
                    msgResEx.setMsgLinkImage(Utils.getUrl(dynamic.getImage()));
                } else if (type == MessageType.BookCircleReply.getType()) {
                    BookCircleReply reply = bookCircleReplyDao.findOne(userMessage.getMessageObjId());
                    BookCircleDynamic dynamic = bookCircleDynamicDao.findOne(reply.getDynamicId());
                    msgResEx.setMsgLinkType(MsgLinkType.Dynamic.getType());
                    msgResEx.setMsgLinkId(dynamic.getDynamicId());
                    msgResEx.setMsgLinkContent(dynamic.getContent());
                    msgResEx.setMsgLinkImage(Utils.getUrl(dynamic.getImage()));
                    msgResEx.setContent(reply.getContent());
                } else if (type == MessageType.Collect_BookSheet.getType()) {
                    UserCollection userCollection = userCollectionDao.findOne(userMessage.getMessageObjId());
                    BookSheet bookSheet = bookSheetDao.findOne(userCollection.getCollectObjId());
                    msgResEx.setMsgLinkType(MsgLinkType.BookSheet.getType());
                    msgResEx.setMsgLinkId(bookSheet.getSheetId());
                    msgResEx.setMsgLinkImage(Utils.getUrl(bookSheet.getCover()));
                } else if (type == MessageType.BookCheck.getType()) {
                    BookCheck bookCheck = bookCheckDao.findOne(userMessage.getMessageObjId());
                    msgResEx.setMsgLinkType(MsgLinkType.BookCheck.getType());
                    msgResEx.setMsgLinkId(bookCheck.getBookCheckId());
                    msgResEx.setMsgLinkImage(Utils.getUrl(bookCheck.getCover()));
                    if (bookCheck.getCheckStatus() == CheckStatus.Pass.getStatus()) {
                        msgResEx.setContent("你创建的《" + bookCheck.getTitle() + "》通过了审核。");
                    } else if (bookCheck.getCheckStatus() == CheckStatus.NotPass.getStatus()) {
                        msgResEx.setContent("你创建的《" + bookCheck.getTitle() + "》未通过审核。\n原因：" + bookCheck.getCheckInfo());
                    }
                }
            } catch (Exception e) {
                msgResEx.setDir(true);
            }
        }
    }

    //消息装载，脏数据返回null，并删除消息
    public static MessageResponse loadMessage(UserMessage userMessage, boolean containLink) {
        MessageResponse messageResponse;
        if (!containLink) {//short msg
            messageResponse = new MessageResponse(userMessage);
        } else {//detail msg
            messageResponse = new MessageResponseEx(userMessage);
        }
        if (messageResponse.getDir() == null) {
            return messageResponse;
        } else {
            logger.info("dir msg,type:" + userMessage.getMessageType() + ",id:" + userMessage.getMessageObjId());
            userMessageDao.delete(userMessage);
            return null;
        }
    }

    //多种方式拉取消息
    public static List getMessages(UserToken userToken, int msgOptType, String params, int page) {
        List<UserMessage> userMessageList = new ArrayList<UserMessage>();
        if (msgOptType == MsgOptType.ByIds.getType()) {
            List<Long> messageIdList = Utils.getLongListByString(params);
            userMessageList = userMessageDao.findAll(messageIdList);
        } else if (msgOptType == MsgOptType.ByTypes.getType()) {
            List<Integer> getMessageTypeList = Utils.getIntListByString(params);
            userMessageList = userMessageDao.findByAcceptUserIdAndMessageTypeInOrderByCreatedTimeDesc(
                    userToken.getUserId(), getMessageTypeList, new PageRequest(page, Constant.PAGE_NUM_LARGE)).getContent();
        } else if (msgOptType == MsgOptType.ByTypeRange.getType()) {
            List<Integer> msgTypeList = Utils.getIntListByString(params);
            if (msgTypeList.size() == 2) {
                userMessageList = userMessageDao.findByAcceptUserIdAndMessageTypeBetweenOrderByCreatedTimeDesc(
                        userToken.getUserId(), msgTypeList.get(0), msgTypeList.get(1), new PageRequest(page, Constant.PAGE_NUM_LARGE)).getContent();
            }
        } else if (msgOptType == MsgOptType.DynamicMsgs.getType()) {
            List<Integer> msgTypes = new ArrayList<Integer>();
            msgTypes.add(MessageType.BookCircleReply.getType());
            msgTypes.add(MessageType.Good_Dynamic.getType());
            userMessageList = userMessageDao.findByAcceptUserIdAndMessageTypeInOrderByCreatedTimeDesc(
                    userToken.getUserId(), msgTypes, new PageRequest(page, Constant.PAGE_NUM_LARGE)).getContent();
        } else if (msgOptType == MsgOptType.SysMsgs.getType()) {
            userMessageList = userMessageDao.getSysMsgs(
                    userToken.getUserId(), MessageType.BookCheck.getType(),
                    billMsgRange[0], billMsgRange[1], new PageRequest(page, Constant.PAGE_NUM_LARGE));

        } else if (msgOptType == MsgOptType.NotRead.getType()) {
            userMessageList = userMessageDao.findByAcceptUserIdAndReadStatus(userToken.getUserId(), ReadStatus.NotRead.getStatus());
        }
        List<MessageResponse> messageResponseList = new ArrayList<MessageResponse>();
        for (UserMessage userMessage : userMessageList) {
            MessageResponse messageResponse = UserMsgUtils.loadMessage(userMessage, true);
            if (messageResponse != null) {
                messageResponseList.add(messageResponse);
            }
        }
        return messageResponseList;
    }

    //三种方式读消息
    public static void readMessages(UserToken userToken, int msgOptType, String params) {
        List<UserMessage> userMessageList = new ArrayList<UserMessage>();
        if (msgOptType == MsgOptType.ByIds.getType()) {
            List<Long> readMessageTypeList = Utils.getLongListByString(params);
            userMessageList = userMessageDao.findAll(readMessageTypeList);
        } else if (msgOptType == MsgOptType.ByTypes.getType()) {
            List<Integer> readMessageTypeList = Utils.getIntListByString(params);
            userMessageList = userMessageDao.findByAcceptUserIdAndMessageTypeInAndReadStatus(
                    userToken.getUserId(), readMessageTypeList, ReadStatus.NotRead.getStatus());
        } else if (msgOptType == MsgOptType.ByTypeRange.getType()) {
            List<Integer> msgTypeList = Utils.getIntListByString(params);
            if (msgTypeList.size() == 2) {
                userMessageList = userMessageDao.findByAcceptUserIdAndMessageTypeBetweenAndReadStatus(
                        userToken.getUserId(), msgTypeList.get(0), msgTypeList.get(1), ReadStatus.NotRead.getStatus());
            }
        }
        for (UserMessage userMessage : userMessageList) {
            userMessage.setReadStatus(ReadStatus.Read.getStatus());
        }
        userMessageDao.save(userMessageList);
    }

    //删除消息:根据 接受用户&&发送用户&&消息类型&&消息对象
    public static void deleteUserMessage(long accepterId, long senderId, int messageType, long messageObjId) {
        List<UserMessage> userMessageList = userMessageDao.findByAcceptUserIdAndSendUserIdAndMessageObjIdAndMessageType(
                accepterId, senderId, messageObjId, messageType);
        if (userMessageList != null && userMessageList.size() > 0) {
            userMessageDao.delete(userMessageList);
        }
    }

    //删除消息:根据 接受用户&&发送用户&&消息类型集合
    public static void deleteUserMessage(long accpterId, long senderId, List<Integer> msgTypes) {
        List<UserMessage> userMessageList = userMessageDao.findByAcceptUserIdAndSendUserIdAndMessageTypeIn(accpterId, senderId, msgTypes);
        if (userMessageList != null && userMessageList.size() > 0) {
            userMessageDao.delete(userMessageList);
        }
    }

    //删除消息:根据 发送用户&&消息类型&&消息对象
    public static void deleteUserMessage(long senderId, int messageType, long messageObjId) {
        List<UserMessage> userMessageList = userMessageDao.findBySendUserIdAndMessageObjIdAndMessageType(
                senderId, messageObjId, messageType);
        if (userMessageList != null && userMessageList.size() > 0) {
            userMessageDao.delete(userMessageList);
        }
    }

    //删除消息:根据 消息类型&&消息对象
    public static void deleteUserMessage(int messageType, long messageObjId) {
        for (UserMessage userMessage : userMessageDao.findByMessageObjIdAndMessageType(messageObjId, messageType)) {
            userMessageDao.delete(userMessage);
        }
    }

    //推送消息
    public static void pushMessage(UserMessage userMessage) {
        if (userMessage != null) {
            MessageResponse messageResponse = UserMsgUtils.loadMessage(userMessage, false);
            if (messageResponse != null) {
                if (MsgPushUtils.pushToSingle(MsgPushUtils.getTransmissionTemplate(messageResponse), userMessage.getAcceptUserId())) {

                }
            }
        }
    }
}
