package com.wg.common.Enum.userbook;

/**
 * Created by Administrator on 2016/11/11 0011.
 */
public enum CategoryGroupType {
    All(0, "全部"), Normal(1, "中图法"), Custom(2, "自定义"), Other(3, "其它");
    // 定义私有变量
    private int type;
    private String name;

    CategoryGroupType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
