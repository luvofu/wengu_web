package com.wg.common.Enum.common;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public enum ResponseCode {
    //common code 10000-10099
    SUCCESS(200, "成功！"),
    ERROR_PARAM(10000, "参数错误！"),
    NULL_KEYWORD(10001, "关键字空！"),
    ILLEGAL_USER(10002, "非法用户！"),
    ERROR_TYPE(10003, "类型错误！"),
    DELETE_IMAGE_FAILD(10004, "删除图片失败！"),
    EDIT_IMAGE_FAILD(10005, "编辑图片失败！"),
    CREATE_FAILD(10006, "新建失败！"),
    CANT_NOT_REPLY_GOOD(10007, "暂时不能回复点赞哦！"),
    UPDATE_NEWEST_VERSION(10008, "为了您的使用体验，请升级到最新版本！"),
    INVALID_EMAIL(10009, "请在用户资料中设置您的有效邮箱！"),
    EXPORT_NUM_NOT_ENOUGH(10010, "您的导出次数已用完，次月将会获得新的导出次数！"),

    //user code 10100-10199
    NOT_LOGIN(10100, "用户未登录！"),
    ILLEGAL_MOBILE(10101, "不合法手机号！"),
    REGED_MOBILE(10102, "手机号已被注册！"),
    NOT_REGED_MOBILE(10103, "手机号未注册！"),
    BINDED_MOBILE(10104, "手机号已被绑定！"),
    NOT_BIND_MOBILE(10105, "手机号未绑定！"),
    ERROR_LOGIN(10106, "用户名密码错误！"),
    EXPIRETIME_TOKEN(10107, "令牌过期！"),
    BINDED_THIRD_ACC(10108, "第三方账号已被绑定！"),
    ALREADY_BIND_THIRD(10109, "账号已绑定第三方！"),
    NOT_SET_ACC(10110, "未设置平台账号！"),
    EXIST_ACC(10111, "账号已存在！"),
    ERROR_ORIGIN_PSW(10112, "原密码有误！"),
    EQUAL_ORIGIN_PSW(10113, "新密码与原密码相同,请修改新密码！"),
    ACC_NOT_EXIST(10114, "账号不存在！"),
    ALREADY_SET_PSW(10115, "已设置密码！"),
    USER_NOT_EXIST(10116, "用户不存在！"),
    EXIST_NICKNAME(10117, "昵称已被使用！"),
    EXCEPT_ACC(10118, "账号异常,请重新登陆！"),
    ERROR_VALIDCODE(10119, "验证码错误！"),
    SEND_VALIDCODE_FAIL(10120, "发送验证码失败,请重新请求！"),
    UNREG_THIRD_ACC(10121, "未注册的第三方！"),//前端第三方登录未注册判定注册
    LOGIN_OUT_FAILED(10122, "登出失败！"),
    EMPTY_SEND_VALIDCODE_NUM(10123, "今日验证次数已用完哦！"),
    MOBILE_BIND_THIRD(10124, "手机号已绑定该类型的第三方！"),
    IM_SIGN_FAIL(10125, "IM 注册失败！"),
    IM_UPDATE_FAIL(10126, "IM 更新失败！"),
    IM_SENDMSG_FAIL(10127, "IM 发送信息失败！"),
    CONCERN_SELF(10128, "不能关注自己！"),
    NK_CONTAIN_WENYA(10129, "昵称中不能包含[文芽]或[wenya]，请修改再试哦！"),

    //book code 10200-10299
    BOOK_NOT_EXIST(10200, "书籍不存在！"),
    BOOKCHECK_NOT_EXIST(10201, "审核的录入书籍不存在！"),
    BOOK_SCAN_NOT_EXIST(10203, "未查询到书籍！"),
    BOOK_ENTRY_FAILED(10204, "书籍录入失败！"),

    //bookgroup code 10300-10399
    BOOKCOMMUNITY_NOT_EXIST(10300, "书籍社区不存在！"),
    COMMENT_NOT_EXIST(10301, "评论不存在！"),

    //bookdesk code 10400-10499

    //booksheet code 10500-10599
    BOOKSHEET_NOT_EXIST(10500, "书单不存在！"),
    NULL_BOOKSHEET_NAME(10501, "书单名不能为空！"),
    BOOK_ALREADY_IN_SHEET(10502, "书单已存在该书籍！"),
    BOOK_NOT_IN_SHEET(10503, "书单不存在该书籍！"),
    SHEET_BOOK_NOT_EXIST(10504, "书单书籍不存在！"),

    //usermessage code 10600-10699
    WAS_FRIEND(10600, "已是好友关系！"),
    INVITE_NOT_FRIEND(10601, "邀请非好友！"),
    REINVITE_DESKUSER(10602, "重复邀请书桌用户！"),
    NOT_FRIEND(10603, "非好友关系！"),

    //bookcircle code 10700-10799
    DYNAMIC_NOT_EXIST(10700, "动态不存在！"),

    //user collection code 10800-10899
    DUPLICATE_COLLECT(10800, "重复收藏！"),
    UNKNOWN_COLLECTION_TYPE(10801, "未知收藏类型！"),
    COLLECTION_NOT_EXIST(10802, "收藏不存在！"),
    COLLECTION_OBJ_NOT_EXIST(10803, "收藏对象不存在！"),

    //userbook bookmark code 10900-10999
    USERBOOK_ALREADY_EXIST(10900, "藏书已存在！"),
    ISBN_ALREADY_EXIST(10901, "ISBN已存在！"),
    ERROR_ISBN(10902, "ISBN错误！"),
    NOT_ENOUGH_BOOK_INFO(10903, "书名、作者、isbn、封面不能为空,请完善您的书籍信息！"),
    USERBOOK_NOT_EXIST(10904, "藏书不存在！"),
    BOOKMARK_ALREADY_EXIST(10905, "书签已存在！"),
    BOOKMARK_NOT_EXIST(10906, "书签不存在！"),
    MINUS_PAGE(10907, "页码不能为负数！"),
    EXIST_CATEGORY(10908, "分类已存在,重复添加！"),
    NOT_EXIST_CATEGORY(10909, "分类不存在！"),
    SH_OR_ISBN_EXIST(10910, "统一书号或ISBN10已存在！"),

    //notebook code 11000-11099
    NOTEBOOK_NOT_EXIST(11000, "笔记本不存在！"),
    NOTE_NOT_EXIST(11001, "笔记不存在！"),
    STORYLINE_NOTEXIST(11002, "情节不存在！"),
    NOTEBOOK_EXIST(11003, "已创建该藏书笔记！"),
    //picword code 11100-11199
    PICWORD_NOT_EXIST(11100, "图文不存在！"),
    //book order
    USERBOOK_NOT_LEASEABLE(11200, "该图书不可租借！"),
    USERBOOK_NOT_SALEABLE(11201, "该图书不可出售！"),
    EXCEE_MAX_LEASE_ORDER(11202, "您同时只能借阅两本书籍,请完成订单后再借阅下一本！"),
    BOOK_ORDER_NOTE_EXIST(11203, "订单不存在哦！"),
    BOOK_ORDER_ERROR_DEAL(11204, "订单处理错误！"),
    UNREASONABLE_BROKENGOLD(11205, "折损金应不大于押金！"),
    INVALID_HAND_CODE(11206, "取书码有误！"),
    INVALID_RETURN_CODE(11207, "收书码有误！"),
    PLACE_ORDER_FAILED(11208, "微信下单失败！"),
    BOOK_ORDER_IS_DELETE(11209, "订单已被删除！"),
    UNFINISH_PAYACCOUNT_INFO(11210, "请完善支付账号信息！"),
    IN_MIN_USING_TIME(11211, "临近最短借阅期限（12小时）或在最长借阅期可申请还书！"),

    //user acc bill
    CHARGE_PLACE_BILL_FAILED(11301, "充值创建账单失败！"),
    WITHDRAW_PLACE_BILL_FAILED(11302, "提现创建账单失败！"),
    REMAINGOLD_NOT_ENOUGH(11303, "可用余额不足！"),

    //community
    NO_PPERMISSION(11401, "权限不足！"),
    JOIN_REFUSE(11402, "当前社区拒绝加入！"),
    OWNER_CANT_QUITE(11403, "社主不能退出！"),
    NEARBY_SAME_COMMUNITY(11404, "附近已存在同名社区！"),
    EXCEE_MAX_OWN_NUM(11405, "您最多能拥有两个社长身份，无法创建更多社区！"),
    EXCEE_OBJ_MAX_OWN_NUM(11406, "对方已经拥有两个社长身份，不能接受您的转让！");
    private final int code;

    private final String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
