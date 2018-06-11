package com.wg.task;

import com.wg.book.utils.BookUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
@Component
public class SearchThirdTask {
    //待搜索第三方关键字
    public static HashSet<String> keywordSet = new HashSet<String>();
    @Transactional
    @Scheduled(fixedDelay = 30000)
    public void searchThird() {
        if (keywordSet.size() > 0) {
            String keyword = keywordSet.iterator().next();
            BookUtils.searchThirdBook(keyword);
            keywordSet.remove(keyword);
        }
    }
}
