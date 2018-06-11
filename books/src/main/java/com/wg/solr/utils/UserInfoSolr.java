package com.wg.solr.utils;

import com.wg.common.Constant;
import com.wg.common.Enum.solr.SolrType;
import com.wg.common.utils.Utils;
import com.wg.solr.modle.QueryRes;
import com.wg.user.domain.UserInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/9/18.
 */
public class UserInfoSolr {
    public static QueryRes<UserInfo> findByNicknameContains(String keyword, int page) {
        keyword = SolrUtils.escapeQueryChars(keyword);
        String queryString = "&start=" + page * Constant.PAGE_NUM_LARGE + "&rows=" + Constant.PAGE_NUM_LARGE;
        if (StringUtils.isNotBlank(keyword)) {
            queryString += "&q=nickname:*" + Utils.encode(keyword) + "*";
        } else {
            queryString += "&q=*:*";
        }
        return SolrUtils.get(SolrType.User.getType(), queryString, UserInfo.class);
    }
}
