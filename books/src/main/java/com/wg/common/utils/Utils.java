package com.wg.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wg.common.Constant;
import com.wg.common.Enum.common.Permission;
import com.wg.common.PropConfig;
import com.wg.user.domain.UserToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static com.wg.common.utils.dbutils.DaoUtils.userFriendDao;

/**
 * Created by Administrator on 2016/12/26 0026.
 */
public class Utils {
    public static Logger logger = LoggerFactory.getLogger(Utils.class);

    //获取uuid
    public static String getUuidString() {
        return UUID.randomUUID().toString();
    }

    //获取关系权限
    public static int getPermission(UserToken userToken, long userId) {
        if (userToken != null) {
            if (userToken.getUserId() == userId) {
                return Permission.Personal.getType();
            } else if (userFriendDao.findByUserIdAndFriendId(userToken.getUserId(), userId) != null) {
                return Permission.Friend.getType();
            }
        }
        return Permission.Open.getType();
    }

    //encode
    public static String encode(String param) {
        if (StringUtils.isNotBlank(param)) {
            try {
                return URLEncoder.encode(param, Constant.CHAR_SET_UTF8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return param;
    }

    //线程睡眠
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //获得"|"分割的Stringlist
    public static List<String> getStringListByString(String string) {
        List<String> stringList = new ArrayList<String>();
        if (string != null) {
            String[] strings = string.split(Constant.SPLIT_CHAR);
            HashMap<String, Long> map = new HashMap<String, Long>();
            for (String s : strings) {
                if (map.get(s) == null) {
                    stringList.add(s);
                    map.put(s, 0L);
                }
            }
        }
        return stringList;
    }

    //获得"|"分割的Longlist
    public static List<Long> getLongListByString(String string) {
        List<Long> longs = new ArrayList<Long>();
        if (string != null) {
            try {
                String[] longStrs = string.split(Constant.SPLIT_CHAR);
                HashMap<Long, Long> map = new HashMap<Long, Long>();
                for (String longStr : longStrs) {
                    Long l = Long.valueOf(longStr);
                    if (map.get(l) == null) {
                        longs.add(l);
                        map.put(l, 0L);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return longs;
    }

    //获得"|"分割的Intlist
    public static List<Integer> getIntListByString(String string) {
        List<Integer> Ints = new ArrayList<Integer>();
        if (string != null) {
            try {
                String[] intStrs = string.split(Constant.SPLIT_CHAR);
                HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
                for (String intStr : intStrs) {
                    Integer i = Integer.valueOf(intStr);
                    if (map.get(i) == null) {
                        Ints.add(i);
                        map.put(i, 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Ints;
    }

    //从string获取整数数值
    public static int getNumber(String string) {
        int number = 0;
        if (StringUtils.isNotBlank(string)) {
            StringBuilder sb = new StringBuilder();
            for (char charValue : string.toCharArray()) {
                if (charValue >= '0' && charValue <= '9') {
                    sb.append(charValue);
                }
            }
            if (sb.length() > 0) {
                number = Integer.valueOf(sb.toString());
            }
        }
        return number;
    }

    //从string获取小数数值
    public static double getDoubleNumber(String string) {
        double doubleNumber = 0;
        if (StringUtils.isNotBlank(string)) {
            StringBuilder sb = new StringBuilder();
            boolean getDot = false;
            for (char charValue : string.toCharArray()) {
                if (charValue >= '0' && charValue <= '9') {
                    sb.append(charValue);
                } else if (charValue == '.' && !getDot) {
                    getDot = true;
                    sb.append(charValue);
                }
            }
            if (sb.length() > 0) {
                try {
                    doubleNumber = Double.valueOf(sb.toString());
                } catch (Exception e) {
                    doubleNumber = 0;
                }
            }
        }
        return doubleNumber;
    }

    //获取url
    public static String getUrl(String path) {
        if (path == null) return null;
        if (path.contains("http")) {
            return path;
        } else {
            return PropConfig.SERVER_URL + path;
        }
    }

    //字符串保留中文、英文
    public static String moveSign(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char charValue : str.toCharArray())
            if ((charValue >= 0x4E00 && charValue <= 0X9FA5)
                    || (charValue >= 'a' && charValue <= 'z')
                    || (charValue >= 'A' && charValue <= 'Z')
                    || (charValue >= '0' && charValue <= '9')) {
                sb.append(charValue);
            }
        return sb.toString();
    }

    //get similar degree by max common string
    public static double getSimilarDegree(String strX, String strY) {
        char[] charArrayX = moveSign(strX).toCharArray();
        char[] charArrayY = moveSign(strY).toCharArray();
        int xLen = charArrayX.length;
        int ylen = charArrayY.length;
        int[][] length = new int[xLen + 1][ylen + 1];
        for (int i = 0; i < xLen; i++) {
            for (int j = 0; j < ylen; j++) {
                if (charArrayX[i] == charArrayY[j]) {
                    length[i + 1][j + 1] = length[i][j] + 1;
                } else {
                    length[i + 1][j + 1] = Math.max(length[i][j + 1], length[i + 1][j]);
                }
            }
        }
        int max = Math.max(xLen, ylen);
        if (max > 0) {
            return length[xLen][ylen] * 1.0 / Math.max(xLen, ylen);
        } else {
            return 1;
        }
    }

    //get similar degree by set
    public static double getSimilarDegreeBySet(String strX, String strY) {
        char[] charArrayX = moveSign(strX).toCharArray();
        char[] charArrayY = moveSign(strY).toCharArray();
        HashSet<Character> setX = new HashSet<Character>();
        HashSet<Character> setY = new HashSet<Character>();
        for (char c : charArrayX) {
            setX.add(c);
        }
        for (char c : charArrayY) {
            setY.add(c);
        }
        double maxLen = setX.size() > setY.size() ? setX.size() : setY.size();
        setY.retainAll(setX);
        return setY.size() * 1.0 / maxLen;
    }

    //get similar degree  by count
    public static double getSimilarDegreeEx(String strX, String strY) {
        char[] charArrayShort = moveSign(strX).toCharArray();
        char[] charArrayLong = moveSign(strY).toCharArray();
        if (charArrayLong.length < charArrayShort.length) {
            char[] temp = charArrayShort;
            charArrayShort = charArrayLong;
            charArrayLong = temp;
        }
        double count = 0;
        for (char chS : charArrayShort) {
            for (char chL : charArrayLong) {
                if (chL == chS) {
                    count++;
                    break;
                }
            }
        }
        return count / (charArrayLong.length);
    }

    //gener md5 code
    public static String MD5(String str) {
        if (str != null) {
            return DigestUtils.md5Hex(str);
        }
        return null;
    }

    //gener rand code
    public static String generRandCode(int num) {
        String randcode = "";
        for (int index = 0; index < num; index++) {
            randcode += (int) (Math.random() * 10);
        }
        return randcode;
    }

    /**
     * 获取一定长度的随机字符串
     *
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    //参数XML化
    public static String parseParamToXml(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        Set es = map.entrySet();
        for (Object e1 : es) {
            Map.Entry entry = (Map.Entry) e1;
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append("<").append(k).append(">").append(v).append("</").append(k).append(">");
        }
        sb.append("</xml>");
        try {
            return new String(sb.toString().getBytes(), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    //XML参数化
    public static Map<String, String> getParamFromXML(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        //这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(xmlString.getBytes());
        Document document = builder.parse(is);
        //获取到document里面的全部结点
        NodeList allNodes = document.getFirstChild().getChildNodes();
        Node node;
        Map<String, String> map = new HashMap<String, String>();
        int i = 0;
        while (i < allNodes.getLength()) {
            node = allNodes.item(i);
            if (node instanceof Element) {
                map.put(node.getNodeName(), node.getTextContent());
            }
            i++;
        }
        return map;
    }

    //JSON参数化
    public static Map<String, String> getParamFromJson(String jsonString) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject jsonObject = JSON.parseObject(jsonString);
            for (Object o : jsonObject.keySet()) {
                String key = String.valueOf(o);
                String value = jsonObject.getString(key);
                map.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> copyMap(Map<String, String> fromMap) {
        Map<String, String> toMap = new HashMap<String, String>();
        for (String key : fromMap.keySet()) {
            toMap.put(key, fromMap.get(key));
        }
        return toMap;
    }

    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {
        if (string == null) return null;
        StringBuilder unicode = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            String hex = Integer.toHexString(string.charAt(i));
            if (hex.length() <= 2) {
                hex = "00" + hex;
            }
            unicode.append("\\u").append(hex);
        }
        return unicode.toString();
    }


}
