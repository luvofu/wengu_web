package com.wg.common.Enum.user;

/**
 * Created by Administrator on 2016/9/28.
 */
public enum UseCondition {
    Signup(0), ForgetPsw(1),CheckMobile(2),BindMobile(3), ThirdBindLogin(4);
    // 定义私有变量
    private int condition;
    // 构造函数,枚举类型只能为私有
    UseCondition(int condition) {
        this.condition = condition;
    }

    public int getCondition() {
        return condition;
    }
}
