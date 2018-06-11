package com.wg.common.Enum.solr;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum SolrType {
    Book(1, "book_core"),
    User(2, "user_core"),
    UserBook(3, "userBook_core"),
    BookGroup(4, "bookGroup_core"),
    BookSheet(5, "bookSheet_core");
    // 定义私有变量
    private int type;
    private String core;

    // 构造函数,枚举类型只能为私有


    SolrType(int type, String core) {
        this.type = type;
        this.core = core;
    }

    public int getType() {
        return type;
    }

    public String getCore() {
        return core;
    }
}
