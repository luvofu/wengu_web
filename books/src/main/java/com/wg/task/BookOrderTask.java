package com.wg.task;

import com.wg.common.utils.TimeUtils;
import com.wg.bookorder.utils.BookOrderUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/14.
 */
@Component
public class BookOrderTask {
    //每分钟一次检测
    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void checkOrder() {
        Date currDate = TimeUtils.getCurrentDate();

        //overtime deal
        BookOrderUtils.testDealOrder(currDate);

        //overtime handbook
        BookOrderUtils.testHandBook(currDate);

        //nearby shortest lease day& overtime longest lease day
        BookOrderUtils.testUsingBook(currDate);

        //nearby return book limit & overtime return book
        BookOrderUtils.testReturnBook(currDate);
    }
}
