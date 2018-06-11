package com.wg.common.Enum.user;

/**
 * Created by Administrator on 2016/9/28.
 */
public enum PlatformType {
    Unknow(0, "Unknow"), iOS(1, "iOS"), Android(2, "Android");
    // 定义私有变量
    private int type;
    private String info;
    // 构造函数,枚举类型只能为私有


    PlatformType(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public static int getInfo(String platform) {
        if (platform.equals(iOS.getInfo())) {
            return iOS.getType();
        } else if (platform.equals(Android.getInfo())) {
            return Android.getType();
        } else {
            return Unknow.getType();
        }
    }
}
