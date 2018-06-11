package com.wg.solr.utils;

import com.wg.book.domain.Book;
import com.wg.common.Constant;
import com.wg.common.Enum.solr.SolrType;
import com.wg.common.utils.Utils;
import com.wg.solr.modle.QueryRes;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/9/18.
 */
public class BookSolr {
    public static QueryRes<Book> findByKeywordContains(String keyword, int page) {
        keyword = SolrUtils.escapeQueryChars(keyword);
        String queryString = "&start=" + page * Constant.PAGE_NUM_LARGE + "&rows=" + Constant.PAGE_NUM_LARGE;
        if (StringUtils.isNotBlank(keyword)) {
            queryString += "&q=title:" + Utils.encode(keyword) + "+OR+author:*" + Utils.encode(keyword) + "*";
        } else {
            queryString += "&q=*:*";
        }
        return SolrUtils.get(SolrType.Book.getType(), queryString, Book.class);
    }

    public static QueryRes<Book> findByCategoryOrderBySortTypeDesc(String category, int page, int sortType) {
        String queryString = "&start=" + page * Constant.PAGE_NUM_LARGE + "&rows=" + Constant.PAGE_NUM_LARGE;
        if (StringUtils.isNotBlank(category)) {
            queryString += "&q=category:" + Utils.encode(category);
        } else {
            queryString += "&q=*:*";
        }
        if (sortType == com.wg.common.Enum.book.SortType.entryNum.getType()) {
            queryString += "&sort=" + Utils.encode("entryNum desc");
        } else {
            queryString += "&sort=" + Utils.encode("rating desc");
        }
        return SolrUtils.get(SolrType.Book.getType(), queryString, Book.class);
    }

//    public List<Book> findByTagContainsOrderBySortTypeDesc(String tag, int page, int sortType) {
//        String queryString = "&start=" + page * Constant.PAGE_NUM_LARGE
//                + "&rows=" + Constant.PAGE_NUM_LARGE
//                + "&q=" + SolrUtils.getQueryStringByTags(tag);
//        if (sortType == com.wg.common.Enum.book.SortType.entryNum.getType()) {
//            queryString += "&sort=" + Utils.encode("entryNum desc");
//        } else {
//            queryString += "&sort=" + Utils.encode("rating desc");
//        }
//        return get(SolrType.Book.getType(), queryString, Book.class);
//    }
}
