package com.wg.common.Enum.message;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum MsgOptType {
    ByIds(0, "消息ID"),
    ByTypes(1, "消息类型"),
    ByTypeRange(2, "消息类型范围"),
    DynamicMsgs(3, "书圈消息（回复、点赞）"),
    SysMsgs(4, "系统通知消息（除订单）"),
    NotRead(5, "未读消息");
    private int type;
    private String info;

    MsgOptType(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }
}
