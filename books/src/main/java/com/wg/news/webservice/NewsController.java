package com.wg.news.webservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.common.Constant;
import com.wg.common.ResponseContent;
import com.wg.common.utils.Utils;
import com.wg.news.domain.News;
import com.wg.news.model.request.NewsRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.wg.common.utils.dbutils.DaoUtils.newsDao;

/**
 * Created by Administrator on 2016/12/14.
 */
@Controller
public class NewsController {

    @Transactional
    @RequestMapping(value = "/api/news/top", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String top(HttpServletRequest request,
                      HttpServletResponse response, NewsRequest newsRequest) {
        ResponseContent responseContent = new ResponseContent();
        Slice<News> newsSlice = newsDao.findAllByOrderByPubDateDesc(new PageRequest(newsRequest.getPage(), Constant.PAGE_NUM_MEDIUM));
        JSONArray jsonArray = new JSONArray();
        for (News news : newsSlice.getContent()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("newsId", news.getNewsId());
            jsonObject.put("title", news.getTitle());
            jsonObject.put("images", JSON.parseArray(news.getImages()));
            jsonObject.put("source", news.getSource());
            jsonObject.put("pubDate", news.getPubDate());
            jsonObject.put("link", Utils.getUrl(news.getHtml()));
            jsonArray.add(jsonObject);
        }
        responseContent.putData("newsList", jsonArray);
        return JSON.toJSONString(responseContent);
    }
}
