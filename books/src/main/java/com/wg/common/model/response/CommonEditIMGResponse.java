package com.wg.common.model.response;

import com.wg.common.utils.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午4:24
 * To change this template use File | Settings | File Templates.
 */
public class CommonEditIMGResponse {
    private String imageUrl;

    public CommonEditIMGResponse(String imageUrl) {
        this.imageUrl = Utils.getUrl(imageUrl);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
