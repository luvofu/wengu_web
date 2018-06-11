package com.wg.common.Enum.common;

/**
 * Created by Administrator on 2016/9/26.
 */
public enum DeleteType {
    Comment(0),CommentReply(1),Dynamic(2),DynamicReply(3);

    int type;

    DeleteType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
