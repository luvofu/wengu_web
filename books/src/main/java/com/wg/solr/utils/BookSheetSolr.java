package com.wg.solr.utils;

import com.wg.booksheet.domain.BookSheet;
import com.wg.common.Constant;
import com.wg.common.Enum.booksheet.SortType;
import com.wg.common.Enum.solr.SolrType;
import com.wg.common.utils.Utils;
import com.wg.solr.modle.QueryRes;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/9/18.
 */
public class BookSheetSolr {
    public static QueryRes<BookSheet> findByTagContainsOrderBySortTypeDesc(String tag, int page, int sortType) {
        String queryString = "&start=" + page * Constant.PAGE_NUM_LARGE + "&rows=" + Constant.PAGE_NUM_LARGE;
        if (StringUtils.isNotBlank(tag)) {
            queryString += "&q=" + getQueryStringByTags(tag);
        } else {
            queryString += "&q=*:*";
        }
        if (sortType == SortType.createdTime.getType()) {
            queryString += "&sort=" + Utils.encode("createdTime desc");
        } else {
            queryString += "&sort=" + Utils.encode("collectionNum desc");
        }
        return SolrUtils.get(SolrType.BookSheet.getType(), queryString, BookSheet.class);
    }

    //获取多个tag模糊匹配逻辑AND
    public static String getQueryStringByTags(String tagsStr) {
        String[] tags = tagsStr.split(Constant.SPLIT_CHAR);
        String queryString = "";
        for (int index = 0; index < tags.length; index++) {
            String tag = tags[index];
            if (StringUtils.isNotBlank(tag)) {
                tag = "tag:*" + Utils.encode(tag) + "*";
                if (index != 0) {
                    queryString += "+AND+";
                }
                queryString += tag;
            }
        }
        return queryString;
    }
}
