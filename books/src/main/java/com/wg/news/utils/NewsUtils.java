package com.wg.news.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.common.Constant;
import com.wg.common.Enum.news.GetType;
import com.wg.common.utils.HtmlUtils;
import com.wg.common.utils.HttpUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.news.domain.News;
import com.wg.news.domain.TempNews;
import org.apache.http.client.methods.HttpGet;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.newsDao;
import static com.wg.common.utils.dbutils.DaoUtils.tempNewsDao;

/**
 * Created by Administrator on 2016/12/13 0013.
 */
public class NewsUtils {
    public static final String SHOWAPI_HOST = "http://route.showapi.com/109-35?";
    public static final String SHOWAPI_SIGN = "077b8f2a2020450aaaa7c19a7ee66585";
    public static final String SHOWAPI_APPID = "28851";
    public static final String COPYRIGHT_KEYWORD = "不得转载";
    public static final int LEAST_WORDS = 20;//新闻正文最少20个文字
    public static List<String> newskeyword = new ArrayList<String>();//新闻搜索关键字
    public static List<String> titleIncludeWords = new ArrayList<String>();//标题需要包涵的关键字
    public static List<String> storedTitles = new ArrayList<String>();//已经存储的新闻标题,用于防重复
    public static List<String> srcExcludeKeyword = new ArrayList<String>();//来源屏蔽关键字

    public static void getNewsFromThird() {
        for (String keyword : newskeyword) {
            getNewsFromShowApi(keyword);
            Utils.sleep(1000);
        }
    }

    public static void getNewsFromShowApi(String keyword) {
        String url = SHOWAPI_HOST
                + "&showapi_sign=" + SHOWAPI_SIGN
                + "&showapi_appid=" + SHOWAPI_APPID
                + "&title=" + Utils.encode(keyword)
                + "&needAllList=1";
        String content = HttpUtils.requestString(new HttpGet(url));
        if (content != null) {
            JSONObject result = JSON.parseObject(content);
            if (result.getInteger("showapi_res_code") == 0) {//query success
                try {
                    JSONArray newsArr = result.getJSONObject("showapi_res_body").getJSONObject("pagebean").getJSONArray("contentlist");
                    for (int i = 0; i < newsArr.size(); i++) {
                        getNewsFromJson(newsArr.getJSONObject(i), GetType.ShowApi.getType());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //获取新闻,并删除临时记录
    public static void getNewsFromWenya() {
        List<TempNews> tempNewsList = tempNewsDao.findAll();
        for (TempNews tempNews : tempNewsList) {
            getNewsFromJson(JSON.parseObject(tempNews.getNewsJson()), GetType.WenyaBack.getType());
        }
        tempNewsDao.delete(tempNewsList);
    }

    //get news from jsonobject
    public static void getNewsFromJson(JSONObject newsObject, int getSource) {
        String title = newsObject.getString("title").trim();
        String ukey = Utils.MD5(title);//md5 title for ukey
        if (newsDao.findByUniqueKey(ukey) == null) { // db not have
            if (relativeNews(title) && uniqueNews(title)) {// words relative and title unique news
                String link = newsObject.getString("link");
                Date pubDate = getPubDate(newsObject.getString("pubDate"));
                String source = newsObject.getString("source");
                if (source != null && applicableSource(source)) {//applicable source
                    String newsBody = newsObject.getString("allList");
                    if (newsBody != null && allrightFree(newsBody)) {//right free
                        String htmlcode = HtmlUtils.loadHtml(title, source, pubDate, newsBody);
                        if (htmlcode != null) {//not empty news
                            String path = HtmlUtils.createHtmlFile(Constant.NEWS_HTML_FOLDER, htmlcode, ukey);
                            if (path != null) {//create html file
                                String images = getImagesDes(newsObject.getJSONArray("imageurls"));
                                News news = AddUtils.addNews(ukey, title, images, link, source, pubDate, path, getSource);
                                storedTitles.add(news.getTitle());//record new stored news
                            }
                        }
                    }
                }
            }
        }
    }

    //图片简介
    public static String getImagesDes(JSONArray imageurls) {
        List<String> images = new ArrayList<String>();
        for (Object object : imageurls) {
            images.add(((JSONObject) object).getString("url"));
            if (images.size() >= 3) break;
        }
        return JSON.toJSONString(images);
    }

    //发布时间
    public static Date getPubDate(String date) {
        Date pubDate = TimeUtils.parseDate(date, TimeUtils.YYYY_MM_DD_HH_MM_SS);
        pubDate = pubDate != null ? pubDate : TimeUtils.getCurrentDate();
        return pubDate.before(TimeUtils.getCurrentDate()) ? pubDate : TimeUtils.getCurrentDate();
    }

    //标题相关
    public static boolean relativeNews(String title) {
        for (String string : titleIncludeWords) {
            if (title.contains(string)) {
                return true;
            }
        }
        return false;
    }

    //新闻不重复
    private static boolean uniqueNews(String title) {
        for (String string : storedTitles) {
            if (Utils.getSimilarDegreeBySet(title, string) >= 0.6) {
                return false;
            }
        }
        return true;
    }

    //新闻版权所有
    public static boolean allrightFree(String newsBody) {
        return !newsBody.contains(COPYRIGHT_KEYWORD);
    }

    //来源可靠
    public static boolean applicableSource(String source) {
        for (String excKeyword : srcExcludeKeyword) {
            if (source.contains(excKeyword)) {
                return false;
            }
        }
        return true;
    }

    public static void removeAllStoredTitles() {
        storedTitles = new ArrayList<String>();
    }

    public static void loadOnePageStoredTitles() {
        Slice<News> newsSlice = newsDao.findAllByOrderByPubDateDesc(new PageRequest(0, Constant.PAGE_NUM_LARGE));
        for (News news : newsSlice.getContent()) {
            storedTitles.add(news.getTitle());
        }
    }

    //update news
    public static void updateNews() {
        loadOnePageStoredTitles();
        getNewsFromWenya();
        getNewsFromThird();
        removeAllStoredTitles();
    }

    //delete news
    public static void deleteNews() {
        Date deleteDate = TimeUtils.getModifyDate(TimeUtils.getCurrentDate(), -30, null, null, null);
        List<News> newsList = newsDao.findByPubDateLessThan(deleteDate);
        //delete html file
        for (News news : newsList) {
            HtmlUtils.deleteHtmlFile(news.getHtml());
        }
        //delete db data
        newsDao.delete(newsList);
    }
}
