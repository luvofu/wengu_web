package com.wg.solr.utils;

import com.wg.bookgroup.domain.BookGroup;
import com.wg.common.Constant;
import com.wg.common.Enum.solr.SolrType;
import com.wg.common.utils.Utils;
import com.wg.solr.modle.QueryRes;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/9/18.
 */
public class BookGroupSolr {
    public static QueryRes<BookGroup> findByTitleContains(String keyword, int page) {
        keyword = SolrUtils.escapeQueryChars(keyword);
        String queryString = "&start=" + page * Constant.PAGE_NUM_LARGE + "&rows=" + Constant.PAGE_NUM_LARGE;
        if (StringUtils.isNotBlank(keyword)) {
            queryString += "&q=title:" + Utils.encode(keyword);
        } else {
            queryString += "&q=*:*";
        }
        return SolrUtils.get(SolrType.BookGroup.getType(), queryString, BookGroup.class);
    }
}
