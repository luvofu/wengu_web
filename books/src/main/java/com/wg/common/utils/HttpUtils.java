package com.wg.common.utils;

import com.wg.common.Constant;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wg.common.utils.Utils.logger;

/**
 * Created by wzhonggo on 8/24/2016.
 */

/**
 * http工具
 */
public class HttpUtils {
    public static CloseableHttpClient httpclient = HttpClients.createDefault();

    // HTTP  请求 get post
    public static HttpEntity request(HttpRequestBase httpRequestBase) {
        try {
            CloseableHttpResponse response = httpclient.execute(httpRequestBase);
            int status_code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (status_code == HttpStatus.OK.value()) {
                return entity;
            }
            EntityUtils.consume(entity);
            response.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    // get、post接受服务器返回内容为string
    public static String requestString(HttpRequestBase httpRequestBase) {
        String respStr = null;
        try {
            HttpEntity httpEntity = request(httpRequestBase);
            if (httpEntity != null) {
                respStr = EntityUtils.toString(httpEntity, Constant.CHAR_SET_UTF8);
            }
            EntityUtils.consume(httpEntity);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return respStr;
    }

    // get、post接受服务器返回内容保存为file
    public static void requestFile(HttpRequestBase httpRequestBase, File file) {
        InputStream ips = null;
        try {
            HttpEntity httpEntity = request(httpRequestBase);
            if (httpEntity != null) {
                ips = httpEntity.getContent();
                FileUtils.copyInputStreamToFile(ips, file);
            }
            EntityUtils.consume(httpEntity);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (ips != null) {
                IOUtils.closeQuietly(ips);
            }
        }
    }

    public static String getRequestUrl(String url, Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {
                prestr = prestr + key + "=" + Utils.encode(value);
            } else {
                prestr = prestr + key + "=" + Utils.encode(value) + "&";
            }
        }
        return url + prestr;
    }

    public static String getHostAddrByName(String domain) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(domain);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
        return addr.getHostAddress();//获得IP地址
    }

    //通过域名获取ip
    public static String getIpByDomain(String domain) {
        String ip = null;
        try {
            InetAddress addr = InetAddress.getByName(domain);
            ip = addr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }
}
