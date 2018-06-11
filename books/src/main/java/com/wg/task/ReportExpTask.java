package com.wg.task;

import com.wg.message.model.ReportInfo;
import com.wg.message.utils.EmailUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

/**
 * Created by Administrator on 2017/4/21 0021.
 */
@Component
public class ReportExpTask {
    //待通知信息
    public static HashSet<ReportInfo> reportSet = new HashSet<ReportInfo>();

    @Transactional
    @Scheduled(fixedDelay = 300000)//五分钟一次
    public void searchThird() {
        while (reportSet.size() > 0) {
            ReportInfo reportInfo = reportSet.iterator().next();
            EmailUtils.report(reportInfo);//report again
            reportSet.remove(reportInfo);//remove it
        }
    }
}
