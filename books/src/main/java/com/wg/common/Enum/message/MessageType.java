package com.wg.common.Enum.message;

import com.wg.message.utils.UserMsgUtils;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum MessageType {
    //好友消息
    FriendInvite(0, "邀请你为好友", false),
    AgreeFriendInvite(11, "同意了你的好友邀请", false),

    FriendConcern(15, "关注了你", false),

    //回复消息
    CommunityReply(2, "回复了你", true),
    BookCircleReply(3, "回复了你", true),

    //点赞消息
    Good_Picword(4, "赞了你的图文", false),
    Good_Comment(5, "赞了你的评论", false),
    Good_Dynamic(6, "赞了你的动态", false),
    Good_BookSheet(7, "赞了你的书单", false),

    //收藏消息
    Collect_BookSheet(8, "收藏了你的书单", false),
    Collect_Picword(13, "收藏了你的图文", false),

    //书籍审核
    BookCheck(9, "你有新书审核消息", true),

    //订单消息 1000-1999
    //leasein 1100-1199
    Li_Ar_TakeBook(1101, "藏书人已同意,请及时取书！", true),
    Li_Ar_refuseApply(1102, "因藏书人拒绝申请，订单已关闭！", true),
    Li_Ar_OvertimeDeal_Close(1103, "因超时未处理,订单已关闭！", true),
    Li_Ar_ConfirmHandBook(1104, "藏书人确认交书，请妥善保管书籍！", true),
    Li_Ar_OvertimeTakeBook_Close(1105, "因超时未取书,订单已关闭！", true),
    Li_Ar_ApplyReturn(1106, "最短借阅期限快到了,请及时申请还书！", true),
    Li_Ar_ReturnBook(1107, "您已申请还书，请及时还书！", true),
    Li_Ar_ReturnBookSB(1108, "最大借阅期限已到，请及时还书！", true),
    Li_Ar_ReturnBookNB(1109, "还书截至日期快到了,请及时还书！", true),
    Li_Ar_Complaint(1110, "因藏书人申诉,订单已处于申诉状态！", true),
    Li_Ar_OvertimeReturnBook_Close(1111, "因超时未还书,订单已违约！", true),

    //leaseout 1200-1299
    Lo_Or_NewOrder(1201, "您有新订单,请及时处理！", true),
    Lo_Or_HandBook(1202, "您已同意申请,请及时交书！", true),
    Lo_Or_OvertimeDeal_Close(1203, "因超时未处理,订单已关闭！", true),
    Lo_Or_OvertimeHandBook_Close(1204, "因超时未交书,订单已关闭！", true),
    Lo_Or_TakeBook(1205, "借阅人申请还书,请及时取书！", true),
    Lo_Or_TakeBookNB(1206, "还书截至日期快到了,请及时取书！", true),
    Lo_Or_ConfirmReturnBook(1207, "借阅人确认还书，订单顺利完成！", true),
    Lo_Or_Complaint(1208, "因借阅人申诉,订单已处于申诉状态！", true),
    Lo_Or_OvertimeTakeBook_Close(1209, "因借阅人未还书,订单已违约！", true),

    //buyin 1300-1399
    Bi_Ar_TakeBook(1301, "藏书人已同意,请及时取书！", true),
    Bi_Ar_refuseApply(1302, "因藏书人拒绝申请，订单已关闭！", true),
    Bi_Ar_OvertimeDeal_Close(1303, "因超时未处理,订单已关闭！", true),
    Bi_Ar_ConfirmHandBook(1304, "藏书人确认交书，订单顺利完成！", true),
    Bi_Ar_OvertimeTakeBook_Close(1305, "因超时未取书,订单已关闭！", true),

    //saleout 1400-1499
    So_Or_NewOrder(1401, "您有新订单,请及时处理！", true),
    So_Or_HandBook(1402, "您已同意申请,请及时交书！", true),
    So_Or_OvertimeDeal_Close(1403, "因超时未处理,订单已关闭！", true),
    So_Or_OvertimeHandBook_Close(1404, "因超时未交书,订单已关闭！", true),

    //账单消息 2000-2999
    ACC_CHARGE_SUCCESS(2101, "充值成功！", true),
    ACC_WITHDRAW_SUCCESS(2102, "提现成功！", true),
    ACC_WITHDRAW_ROLLBAK(2103, "提现失败！", true),
    ACC_ORDER_APPLER_BILL(2104, "您的type订单:《title》已结算，请查看账单！", true),
    ACC_ORDER_OWNER_BILL(2105, "您的type订单:《title》已结算，请查看账单！", true),

    //社区消息 3000-3999
    COMM_JOIN_VERTIFY(3101, "申请加入社区", true),
    COMM_ACCEPTE_JOIN(3102, "同意您加入社区", true),
    COMM_INVITE_JOIN(3103, "邀请您加入社区", true),
    COMM_NOTE(3104, "发布了社区公告！", false),
    COMM_TRANS_OWNER(3105, "设置您为社区社长！", true),
    COMM_SET_MANAGER(3106, "设置您为社区管理员！", true),
    COMM_QUITE(3107, "退出了社区", true),
    COMM_EXPLE_OUT(3108, "您被请出了社区", false);

    // 定义私有变量
    private int type;
    private String text;
    private boolean isNotify;

    // 构造函数,枚举类型只能为私有
    MessageType(int type, String text, boolean isNotify) {
        this.type = type;
        this.text = text;
        this.isNotify = isNotify;
    }

    public static String getText(int msgType) {
        return UserMsgUtils.mtMap.get(msgType).getText();
    }

    public static Boolean isNotify(int msgType) {
        return UserMsgUtils.mtMap.get(msgType).isNotify();
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public boolean isNotify() {
        return isNotify;
    }
}
