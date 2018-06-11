package com.wg.common.model.response;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午4:23
 * To change this template use File | Settings | File Templates.
 */
public class CommonGoodResponse {
    private boolean isGood;

    public CommonGoodResponse(boolean isGood) {
        this.isGood = isGood;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }
}
