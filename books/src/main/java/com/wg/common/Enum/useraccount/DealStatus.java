package com.wg.common.Enum.useraccount;

/**
 * Created by Administrator on 2017/1/10 0010.
 */
//账单充值提现状态
public enum DealStatus {
    InDeal(0,"处理中"), Success(1,"成功"), Failed(2,"失败");

    int status;
    String info;

    DealStatus(int status, String info) {
        this.status = status;
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }
}
