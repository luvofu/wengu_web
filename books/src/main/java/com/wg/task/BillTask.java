package com.wg.task;

import com.wg.common.Enum.useraccount.BillType;
import com.wg.common.Enum.useraccount.DealStatus;
import com.wg.common.utils.TimeUtils;
import com.wg.useraccount.domain.UserBill;
import com.wg.useraccount.utils.UserAccUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.userBillDao;
import static com.wg.common.utils.Utils.logger;

/**
 * Created by Administrator on 2017/3/17 0017.
 */
@Component
public class BillTask {
    //每小时开始一次（0）
    @Transactional
    @Scheduled(cron = "0 0 */1 * * *")
    public void dealWithdrawBill() {
        logger.info("[" + TimeUtils.getCurrentDate() + "] deal withdraw bill task start");
        List<UserBill> userBillList = userBillDao.findByBillTypeAndDealStatusAndTTimeLessThan(
                BillType.Withdraw.getType(), DealStatus.InDeal.getStatus(), TimeUtils.getCurrentDate());
        for (UserBill userBill : userBillList) {
            UserAccUtils.fund(userBill);
        }
    }
}
