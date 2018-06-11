package com.wg.common.Enum.common;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public enum ExportType {
    Notebook(1, "导出笔记"), Userbook(2, "导出藏书");

    private int type;
    private String info;

    ExportType(int type, String info) {
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
