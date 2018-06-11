package com.wg.task;

import com.wg.common.utils.TimeUtils;
import com.wg.news.utils.NewsUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.wg.common.utils.Utils.logger;

/**
 * Created by Administrator on 2016/12/14.
 */
@Component
public class UpdateNewsTask {

    //每小时的第五分钟开始，每十分钟更新一次新闻,遍历所有的关键字(5\15\25\35\45\55)
    @Transactional
    @Scheduled(cron = "0 5/10 0/1 * * *")
    public void updateNews() {
        logger.info("[" + TimeUtils.getCurrentDate() + "] update news task start");
        NewsUtils.updateNews();
    }

    //每天凌晨四点,删除30天以前的新闻
    @Transactional
    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteNews() {
        NewsUtils.deleteNews();
    }
}
