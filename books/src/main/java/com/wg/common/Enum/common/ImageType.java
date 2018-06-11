package com.wg.common.Enum.common;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum ImageType {
    UserAvatar(0), UserBackground(1), BookSheetCover(2), NotebookContent(3), CommunityTheme(4);
    // 定义私有变量
    private int type;

    // 构造函数,枚举类型只能为私有
    private ImageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
