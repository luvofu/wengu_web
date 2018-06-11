package com.wg.common.Enum.user;

/**
 * Created by Administrator on 2017/1/10 0010.
 */
public enum ConcernStatus {
    NOEachConcern(0), SingleConcern(1), SingleBeConcerned(2), MutualConcern(3);

    int status;

    ConcernStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
