package com.wg.common.Enum.bookorder;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum FilterType {
    All(0, "全部"),
    Ongoing(1, "进行中"),
    Finish(2, "完成");
    // 定义私有变量
    private int type;
    private String info;
    // 构造函数,枚举类型只能为私有

    FilterType(int type, String info) {
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