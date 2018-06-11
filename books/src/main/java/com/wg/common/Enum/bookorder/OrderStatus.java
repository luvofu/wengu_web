package com.wg.common.Enum.bookorder;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum OrderStatus {
    All(0,"全部","全部"),
    Deal(1, "申请中", "新订单"),
    Hand(2, "待取书", "待交书"),
    Using(3, "借阅中", "借阅中"),
    Return(4, "待还书", "待取书"),
    Finish(5,"完成","完成");
    // 定义私有变量
    private int status;
    private String applyerInfo;
    private String ownerInfo;
    // 构造函数,枚举类型只能为私有

    OrderStatus(int status, String applyerInfo, String ownerInfo) {
        this.status = status;
        this.applyerInfo = applyerInfo;
        this.ownerInfo = ownerInfo;
    }

    public int getStatus() {
        return status;
    }

    public String getApplyerInfo() {
        return applyerInfo;
    }

    public String getOwnerInfo() {
        return ownerInfo;
    }
}
