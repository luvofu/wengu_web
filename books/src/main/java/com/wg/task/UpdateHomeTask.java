package com.wg.task;

import com.wg.common.utils.HomeUtils;
import com.wg.common.utils.TimeUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.wg.common.utils.Utils.logger;
/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-8-28
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */

@Component
public class UpdateHomeTask {

    //每小时中的第三十分钟一次(30)
    @Transactional
    @Scheduled(cron = "0 30/59 0/1 * * *")
    public void updateHomeData() {
        logger.info("[" + TimeUtils.getCurrentDate() + "] update home task start");
        Date startDate = new Date(TimeUtils.getCurrTimestamp() - 10000);
        HomeUtils.updateHomeData();
        HomeUtils.deleteHomeData(startDate);
//        HomeUtils.updateHotBookByHome();
//        HomeUtils.deleteOldHotBook(SourceType.Home.getType(),startDate);
        HomeUtils.updateHotBookGet();
    }
}
