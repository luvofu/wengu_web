package com.wg.common.Enum.userbook;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public enum OtherCategory {
    //filter other type
    Personal("私密"),
    Reading("在读"),
    Not_read("未读"),
    Finish_read("已读"),
    Leaseable("可借阅"),
    Saleable("可出售"),
    My_share("我的分享");

    private final String filter;

    OtherCategory(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }
}
