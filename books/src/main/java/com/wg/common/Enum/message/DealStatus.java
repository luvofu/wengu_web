package com.wg.common.Enum.message;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum DealStatus {
    NotDeal(0), Accept(1), Refuse(2);
    private int status;

    private DealStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
