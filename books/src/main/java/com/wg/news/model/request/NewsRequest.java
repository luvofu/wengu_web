package com.wg.news.model.request;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public class NewsRequest {
    int page = 0;
    long newsId;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getNewsId() {
        return newsId;
    }

    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }
}
