package com.wg.common.model.response;

import com.alibaba.fastjson.JSONArray;
import com.wg.common.FileConfig;
import com.wg.common.PropConfig;

/**
 * Created by Administrator on 2016/11/6 0006.
 */
public class CommonConfigResponse {
    private long updatedTime;
    private JSONArray address;
    //客服id
    private long officialUserId;

    public CommonConfigResponse(long updatedTime) {
        this.updatedTime = FileConfig.updatedTime;
        if (updatedTime != FileConfig.updatedTime) {
//            this.address = FileConfig.address;
            this.officialUserId = PropConfig.OFFICER_USERID;
        }
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public JSONArray getAddress() {
        return address;
    }

    public void setAddress(JSONArray address) {
        this.address = address;
    }

    public long getOfficialUserId() {
        return officialUserId;
    }

    public void setOfficialUserId(long officialUserId) {
        this.officialUserId = officialUserId;
    }
}
