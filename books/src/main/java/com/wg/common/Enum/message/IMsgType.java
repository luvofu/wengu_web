package com.wg.common.Enum.message;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum IMsgType {
    Text(0, "文本"), Img(1, "图片"), Audio(2, "音频"),LngLat(8, "位置"),;
    private long type;
    private String info;

    IMsgType(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public long getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }
}
