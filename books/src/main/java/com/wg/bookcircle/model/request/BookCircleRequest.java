package com.wg.bookcircle.model.request;

import com.wg.common.Enum.bookcircle.DynamicType;
import com.wg.common.Enum.common.Permission;

/**
 * Created by Administrator on 2016/9/10.
 */
public class BookCircleRequest {
    private String token;
    private int page=0;
    private String content;
    private String location;
    //    链接id（普通图文id为空,其他均为实际的id,用于点击跳转到指定的内容模块）
    private long linkId;
    //    类型（0:普通图文/1:书籍/2:书单/3:评论）
    private int linkType;
    //    0:(默认)用户自定义动态；1:系统推送动态
    private int dynamicType = DynamicType.Personal.getType();
    //0公开,1好友、2私密
    private int permission = Permission.Open.getType();
    private long userId = -1;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getDynamicType() {
        return dynamicType;
    }

    public void setDynamicType(int dynamicType) {
        this.dynamicType = dynamicType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
