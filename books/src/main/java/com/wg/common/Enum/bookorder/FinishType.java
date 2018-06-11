package com.wg.common.Enum.bookorder;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum FinishType {
    Ongoing(0, "进行中"),
    Normal(1, "成功"),
    BreakContract(2, "违约"),
    Cancle(3, "取消"),
    Refuse(4, "拒绝");
    // 定义私有变量
    private int type;
    private String info;
    // 构造函数,枚举类型只能为私有

    FinishType(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public static String getFinishInfo(int finishType) {
        if (finishType == Ongoing.getType()) {
            return Ongoing.getInfo();
        } else if (finishType == Normal.getType()) {
            return Normal.getInfo();
        } else if (finishType == BreakContract.getType()) {
            return BreakContract.getInfo();
        } else if (finishType == Cancle.getType()) {
            return Cancle.getInfo();
        }
        return null;
    }
}
