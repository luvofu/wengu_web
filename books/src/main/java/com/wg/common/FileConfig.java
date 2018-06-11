package com.wg.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wg.common.utils.TimeUtils;
import com.wg.news.utils.NewsUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wg.common.utils.Utils.logger;
/**
 * Created by Administrator on 2016/9/29.
 */

/**
 * 配置文件配置
 */
@Component
public class FileConfig {

    //更新标志时间
    public static long updatedTime;

    //书籍分类、书籍标签、书单标签
    public static HashMap<String, String> map = new HashMap<String, String>();//映射map
    public static List<String> topClassList = new ArrayList<String>();//中图法分类
    public static List<String> secClassList = new ArrayList<String>();//映射分类
    public static List<String> bookTagList = new ArrayList<String>();//书库筛选用
    public static List<String> bookSheetTagList = new ArrayList<String>();//书单筛选用

    //中国地址
    public static JSONArray address;

    @PostConstruct
    void init() {
        logger.info("FileConfig construct");
        updateConfigFile();
    }

    public static void updateConfigFile() {
        loadAddress("/address.txt");
        loadBookClass("/bookClass.txt");
        bookTagList = getStringLines("/bookTag.txt");
        bookSheetTagList = getStringLines("/bookSheetTag.txt");
        NewsUtils.newskeyword = getStringLines("/newsKeyword.txt");
        NewsUtils.titleIncludeWords = getStringLines("/newsTitleIncWords.txt");
        NewsUtils.srcExcludeKeyword = getStringLines("/newsSrcExckeyword.txt");
        updatedTime = TimeUtils.getCurrentDate().getTime();
    }

    //中图法分类映射
    public static void loadBookClass(String filePath) {
        //init topclass、secclass、map
        map = new HashMap<String, String>();
        topClassList = new ArrayList<String>();
        secClassList = new ArrayList<String>();
        List<String> stringList = getStringLines(filePath);
        for (String string : stringList) {
            String[] couple = string.split("[\t]");
            if (StringUtils.isNotBlank(couple[0]) && StringUtils.isNotBlank(couple[1])) {
                boolean newClass = true;
                for (String topClass : topClassList) {
                    if (topClass.equals(couple[1])) {
                        newClass = false;
                        break;
                    }
                }
                if (newClass) {
                    topClassList.add(couple[1]);
                }
                secClassList.add(couple[0]);
                map.put(couple[0], couple[1]);
            }
        }
    }

    //中国区域地址
    public static void loadAddress(String filePath) {
        try {
            List<String> stringList = getStringLines(filePath);
            if (stringList.size() == 1) {
                address = JSON.parseObject(stringList.get(0)).getJSONArray("address");
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    //读取每行字符串
    public static List<String> getStringLines(String filePath) {
        List<String> stringList = new ArrayList<String>();
        InputStream ips = null;
        try {
            ips = FileConfig.class.getResourceAsStream(filePath);
            List<String> strLineList = IOUtils.readLines(ips, Constant.CHAR_SET_UTF8);
            for (String string : strLineList) {
                if (StringUtils.isNotBlank(string)) {
                    stringList.add(string);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if (ips != null) {
                IOUtils.closeQuietly(ips);
            }
        }
        return stringList;
    }
}
