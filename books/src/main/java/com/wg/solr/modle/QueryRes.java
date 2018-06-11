package com.wg.solr.modle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/31 0031.
 */
public class QueryRes<T> {
    private List<T> docList;
    private long totalRes;

    public QueryRes(List<T> docList, long totalRes) {
        this.docList = docList;
        this.totalRes = totalRes;
    }

    public QueryRes() {
        this.docList = new ArrayList<T>();
        this.totalRes = 0;
    }

    public List<T> getDocList() {
        return docList;
    }

    public void setDocList(List<T> docList) {
        this.docList = docList;
    }

    public long getTotalRes() {
        return totalRes;
    }

    public void setTotalRes(long totalRes) {
        this.totalRes = totalRes;
    }
}
