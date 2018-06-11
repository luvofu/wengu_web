package com.wg.solr.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wg.common.Constant;
import com.wg.common.Enum.solr.CallType;
import com.wg.common.Enum.solr.SolrType;
import com.wg.common.PropConfig;
import com.wg.common.utils.HttpUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.solr.domain.TempSolrIndex;
import com.wg.solr.modle.QueryRes;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.tempSolrIndexDao;

/**
 * Created by Administrator on 2016/9/26.
 */
@Component
public class SolrUtils {
    //solr核
    public static final HashMap<Integer, String> SOLR_CORE = new HashMap<Integer, String>();
    //更新标志
    public static final HashMap<Integer, Boolean> UPDATE_SIGN = new HashMap<Integer, Boolean>();
    //删除标志
    public static final HashMap<Integer, Boolean> DELETE_SIGN = new HashMap<Integer, Boolean>();
    //请求时间
    public static final HashMap<Integer, Long> REQUEST_TIME = new HashMap<Integer, Long>();
    //更新线程
    public static final HashMap<Integer, Thread> UPDATE_THREAD = new HashMap<Integer, Thread>();
    //请求最小间隔 ms
    public static final long DELTA_TIME = 3000;

    @PostConstruct
    void init() {
        logger.info("SolrUtils construct");

        for (SolrType solrType : SolrType.values()) {
            SOLR_CORE.put(solrType.getType(), solrType.getCore());
            REQUEST_TIME.put(solrType.getType(), TimeUtils.getCurrTimestamp());
            UPDATE_SIGN.put(solrType.getType(), true);
            DELETE_SIGN.put(solrType.getType(), true);
        }
    }

    //获取最后请求时间
    public static Long getRequestTime(int solrType) {
        return REQUEST_TIME.get(solrType);
    }

    //设置最后请求时间
    public static void setRequestTime(int solrType) {
        REQUEST_TIME.put(solrType, TimeUtils.getCurrTimestamp());
    }

    //设置更新标志
    public static void setUpdateSign(int SolrType, boolean request) {
        UPDATE_SIGN.put(SolrType, request);
    }

    //获得更新标志
    public static boolean getUpdateSign(int SolrType) {
        return UPDATE_SIGN.get(SolrType);
    }

    //设置删除标志
    public static void setDeleteSign(int SolrType, boolean request) {
        DELETE_SIGN.put(SolrType, request);
    }

    //获得删除标志
    public static boolean getDeleteSign(int SolrType) {
        return DELETE_SIGN.get(SolrType);
    }

    //空转
    public static boolean inIdel(int solrType) {
        return TimeUtils.getCurrTimestamp() - getRequestTime(solrType) >= DELTA_TIME;
    }

    //获取solr core path
    public static String getSolrCore(int solrType) {
        return SOLR_CORE.get(solrType);
    }

    //批量获取某一类型删除记录
    public static List<Long> getIdsByType(List<TempSolrIndex> tempSolrIndexList) {
        List<Long> objIds = new ArrayList<Long>();
        for (TempSolrIndex tempSolrIndex : tempSolrIndexList) {
            objIds.add(tempSolrIndex.getObjId());
        }
        return objIds;
    }

    //空转 且 有更新
    public static boolean requestUpdate(int solrType) {
        if (inIdel(solrType)) {
            if (getUpdateSign(solrType)) {
                setRequestTime(solrType);
                setUpdateSign(solrType, false);
                return true;
            }
        }
        return false;
    }

    //空转 且 有删除
    public static boolean requestDelete(int solrType) {
        if (inIdel(solrType)) {
            if (getDeleteSign(solrType)) {
                setRequestTime(solrType);
                setDeleteSign(solrType, false);
                return true;
            }
        }
        return false;
    }

    //try update
    public static void tryUpdate(int solrType, int callType) {
        if (callType == CallType.Api.getType()) {//api
            setUpdateSign(solrType, true);
            tryStartUpdate(solrType);
        } else if (callType == CallType.Task.getType()) {//task
            if (requestUpdate(solrType)) {
                updateSolrIndex(solrType);
            }
        }
    }

    //start thread to update solr
    public static void tryStartUpdate(final int solrType) {
        if (!UPDATE_THREAD.containsKey(solrType) || !UPDATE_THREAD.get(solrType).isAlive()) {
            UPDATE_THREAD.put(solrType, new Thread() {
                @Override
                public void run() {
                    while (SolrUtils.requestUpdate(solrType)) {
                        Utils.sleep(DELTA_TIME);
                        SolrUtils.updateSolrIndex(solrType);
                    }
                }
            });
            UPDATE_THREAD.get(solrType).start();
        }
    }

    //update solr index
    public static void updateSolrIndex(int solrType) {
        if (!update(solrType)) {
            logger.error("update solr " + getSolrCore(solrType) + " failed!");
        }
    }

    //try delete
    public static void tryDelete(int solrType, int callType) {
        if (callType == CallType.Api.getType()) {//api
            setDeleteSign(solrType, true);//set map true
        }
        if (requestDelete(solrType)) {//time in idel and map true
            deleteSolrIndex(solrType);
        }
    }

    //delete solr index
    public static void deleteSolrIndex(int solrType) {
        List<TempSolrIndex> tempSolrIndexList = tempSolrIndexDao.findByType(solrType);
        if (tempSolrIndexList.size() > 0) {
            if (delete(SolrUtils.getIdsByType(tempSolrIndexList), solrType)) {
                tempSolrIndexDao.delete(tempSolrIndexList);
            } else {
                logger.error("delete solr " + getSolrCore(solrType) + " failed!");
            }
        }
    }


    //get
    public static <T> QueryRes<T> get(int solrType, String queryString, Class<T> clazz) {
        String core = SolrUtils.getSolrCore(solrType);
        String url = PropConfig.SOLR_SERVER_URL + core + "/select?wt=json" + queryString;
        logger.info("solr search,core:" + core);
        HttpGet httpGet = new HttpGet(url);
        String result = HttpUtils.requestString(httpGet);
        if (result != null) {
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson.containsKey("responseHeader") && resultJson.getJSONObject("responseHeader").getInteger("status") == 0) {
                JSONObject jsonResponse = resultJson.getJSONObject("response");
                return new QueryRes<T>(
                        JSON.parseArray(jsonResponse.getString("docs"), clazz),
                        jsonResponse.getLongValue("numFound"));
            }
        }
        return new QueryRes<T>();
    }

    //update
    public static boolean update(int solrType) {
        String core = SolrUtils.getSolrCore(solrType);
        String url = PropConfig.SOLR_SERVER_URL + core + "/dataimport?_=" + TimeUtils.getCurrentDate().getTime() + "&indent=on&wt=json";
        logger.info("solr update,core:" + core);
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        String content = "command=delta-import" + "&verbose=false" + "&clean=false" +
                "&commit=true" + "&core=" + core + "&optimize=false" + "&name=dataimport";
        StringEntity entity = new StringEntity(content, Constant.CHAR_SET_UTF8);
        httppost.setEntity(entity);
        String result = HttpUtils.requestString(httppost);
        if (result != null) {
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson.containsKey("responseHeader") && resultJson.getJSONObject("responseHeader").getInteger("status") == 0) {
                return true;
            }
        }
        return false;
    }

    //delete
    public static boolean delete(List<Long> ids, int solrType) {
        String core = SolrUtils.getSolrCore(solrType);
        String url = PropConfig.SOLR_SERVER_URL + core + "/update?_" + TimeUtils.getCurrentDate().getTime() + "&boost=1.0&commitWithin=1000&overwrite=true&wt=json";
        logger.info("solr delete,core:" + core);
        HttpPost httppost = new HttpPost(url);
        StringEntity entity = new StringEntity(getDelIndexXml(ids), ContentType.TEXT_XML);
        httppost.setEntity(entity);
        String result = HttpUtils.requestString(httppost);
        if (result != null) {
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson.containsKey("responseHeader") && resultJson.getJSONObject("responseHeader").getInteger("status") == 0) {
                return true;
            } else {
                logger.error("ids=" + JSON.toJSONString(ids) + " ,error core " + core);
            }
        }
        return false;
    }

    public static String getDelIndexXml(List<Long> ids) {
        String content = "<add><delete>";
        for (long id : ids) {
            content += "<id>" + id + "</id>";
        }
        content += "</delete><commit/></add>";
        return content;
    }

    //solr escape syntax chars
    public static String escapeQueryChars(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
                    || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
                    /*|| c == '*'*/ || c == '?' || c == '|' || c == '&' || c == ';' || c == '/'
                    || Character.isWhitespace(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

}
