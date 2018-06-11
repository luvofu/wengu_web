package com.wg.solr.utils;

import com.wg.common.Constant;
import com.wg.common.Enum.solr.SolrType;
import com.wg.common.utils.Utils;
import com.wg.solr.modle.QueryRes;
import com.wg.userbook.domain.UserBook;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/9/18.
 */
public class UserBookSolr {
    public static QueryRes<UserBook> findByKeywordAndUserIdAndPermission(String keyword, long userId, int permission, int page) {
        keyword = SolrUtils.escapeQueryChars(keyword);
        String queryString = "&start=" + page * Constant.PAGE_NUM_LARGE + "&rows=" + Constant.PAGE_NUM_LARGE;
        if (StringUtils.isNotBlank(keyword)) {
            queryString += "&q=(title:*" + Utils.encode(keyword) + "*+OR+author:*" + Utils.encode(keyword) + "*)";
        } else {
            queryString += "&q=*:*";
        }
        queryString += "+AND+userId:" + userId + "+AND+permission:[0+TO+" + permission + "]";
        queryString += "&sort=" + Utils.encode("updatedTime desc");
        return SolrUtils.get(SolrType.UserBook.getType(), queryString, UserBook.class);
    }
}
