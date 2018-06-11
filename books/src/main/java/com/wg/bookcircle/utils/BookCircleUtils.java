package com.wg.bookcircle.utils;

import java.util.List;

/**
 * Created by Administrator on 2017/1/4 0004.
 */
public class BookCircleUtils {
    public static String getBookEntryDynamicContent(List<String> titleList) {
        int entryCount = titleList.size();
        String content = "添加了" + entryCount + "本书籍:";
        for (int index = 0; index < entryCount; index++) {
            if (index < 3) {
                content += "《" + titleList.get(index) + "》";
            } else {
                content += "等。";
                break;
            }
        }
        return content;
    }

    public static String getBookEntryDynamicContent(String title) {
        return "添加了1本书籍:" + "《" + title + "》";
    }
}
