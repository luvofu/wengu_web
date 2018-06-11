package com.wg.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.common.Constant;
import com.wg.common.PropConfig;
import com.wg.news.utils.NewsUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import static com.wg.common.utils.Utils.logger;

/**
 * Created by Administrator on 2016/12/15 0015.
 */
public class HtmlUtils {

    public static final String NEWS_COPYRIGHT_NOTICE = "内容来源于网络,如果涉及版权问题,请及时联系我们,作删除处理！邮箱:uvnya@foxmail.com";

    public static final String HTML_BEGAIN = "<html>" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width\">" +
            "<script type=\"text/javascript\">" +
            "    var viewport = document.querySelector(\"meta[name=viewport]\");" +
            "    var scale = screen.width / 640;" +
            "    var isIPhone = window.navigator.userAgent.match(/iPhone/gi);" +
            "    var isAndroid = window.navigator.userAgent.match(/Android/gi);" +
            "    if (isIPhone) {" +
            "        viewport.setAttribute('content', 'width=device-width, initial-scale=' + scale + ', maximum-scale=' + scale + ', minimum-scale=' + scale + ', user-scalable=no');" +
            "    } else if (isAndroid) {" +
            "        viewport.setAttribute('content', 'width=device-width, target-densitydpi=device-dpi,initial-scale=' + scale + '');" +
            "    }" +
            "    document.documentElement.firstElementChild.appendChild(viewport);" +
            "</script>" +
            "<style>" +
            "    body {" +
            "        width: 640px;" +
            "        margin: 0;" +
            "        padding: 0;" +
            "        line-height: 300%;" +
            "    }" +
            "" +
            "    .src {" +
            "        margin-top: 20px;" +
            "        margin-bottom: 30px;" +
            "        font-size: 20px;" +
            "        color: #888;" +
            "    }" +
            "" +
            "    .srcData{" +
            "        margin-left: 30px;" +
            "    }" +
            "" +
            "    .versrc {" +
            "        margin-top: 20px;" +
            "        margin-bottom: 30px;" +
            "        font-size: 13px;" +
            "        color: #888;" +
            "    }" +
            "" +
            "    .head {" +
            "        margin-top: 50px;" +
            "        margin-left: 20px;" +
            "        margin-right: 20px;" +
            "        font-size: 24px;" +
            "    }" +
            "" +
            "    .image {" +
            "        width: 640px;" +
            "        text-align: center;" +
            "    }" +
            "" +
            "    .image img {" +
            "        width: 600px;" +
            "        margin-top: 2px;" +
            "    }" +
            "" +
            "    .content {" +
            "        font-size: 30px;" +
            "        margin-left: 20px;" +
            "        margin-right: 20px;" +
            "    }" +
            "" +
            "    .version {" +
            "        margin-top: 50px;" +
            "        margin-left: 20px;" +
            "        margin-right: 20px;" +
            "    }" +
            "</style>" +
            "<body>";
    public static final String HTML_END = "<div class=\"version\">" +
            "    <hr style=\"border:1px solid #e1e1e1;margin-bottom: 10px; \"/>" +
            "    <span class=\"versrc\">" + NEWS_COPYRIGHT_NOTICE + "</span>" +
            "</div>" +
            "</body>" +
            "</html>";
    public static final String HEAD_BEGIN = "<div class=\"head\"><h2>";
    public static final String HEAD_SRC = "</h2><span class=\"src\">";
    public static final String HEAD_DATE = "</span> <span class=\"src srcData\">";
    public static final String HEAD_END = "</span><hr style=\"border:1px solid #e1e1e1;margin-bottom: 50px; \"/></div>";
    public static final String IMAGE_BEGIN = "<div class=\"image\"><img src='";
    public static final String IMAGE_END = "'/></div>";
    public static final String CONTENT_BEGIN = "<div class=\"content\"><p>";
    public static final String CONTENT_END = "</p></div>";


    public static String loadHtml(String title, String source, Date pubDate, String newsBody) {
        StringBuilder htmlcode = new StringBuilder();
        htmlcode.append(HTML_BEGAIN);
        htmlcode.append(loadHead(title, source, pubDate));
        JSONArray AllList = JSON.parseArray(newsBody);
        int wordsSize = 0;
        for (Object object : AllList) {
//            if (object.getClass().equals(String.class)) {
            if (object instanceof String) {
                String content = (String) object;
                if (content.length() > 0) {
                    htmlcode.append(loadContent(content));
                    wordsSize += content.length();
                }
            } else {
                htmlcode.append(loadImage(((JSONObject) object).getString("url")));
            }
        }
        htmlcode.append(HTML_END);
        return wordsSize > NewsUtils.LEAST_WORDS ? htmlcode.toString() : null;
    }

    public static String loadHead(String title, String source, Date pubDate) {
        return HEAD_BEGIN + title + HEAD_SRC + source + HEAD_DATE + TimeUtils.describDate(pubDate) + HEAD_END;
    }

    public static String loadImage(String imageUrl) {
        return IMAGE_BEGIN + imageUrl + IMAGE_END;
    }

    public static String loadContent(String content) {
        return CONTENT_BEGIN + content + CONTENT_END;
    }

    public static String createHtmlFile(String folder, String htmlCode, String name) {
        if (StringUtils.isNotBlank(htmlCode) && StringUtils.isNotBlank(name)) {
            try {
                String path = folder + name + Constant.HTML_SUFFIX;
                File html = new File(PropConfig.UPLOAD_PATH + path);
                FileWriter fileWriter = new FileWriter(html);
                fileWriter.write(htmlCode);
                fileWriter.close();
                return path;
            } catch (Exception e) {
                logger.error(e.getMessage(), e.getCause());
            }
        }
        return null;
    }

    public static void deleteHtmlFile(String html) {
        try {
            if (html != null) {
                FileUtils.deleteQuietly(new File(PropConfig.UPLOAD_PATH + html));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
