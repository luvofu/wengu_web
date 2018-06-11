package com.wg.useraccount.dao;

import com.wg.useraccount.domain.UserBill;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/14 0014.
 */
@Repository
public interface UserBillDao extends JpaRepository<UserBill, Long> {

    Slice<UserBill> findByUserIdAndIsShowOrderByCreatedTimeDesc(long userId, boolean isShow, Pageable pageable);

    UserBill findByTradeNumber(String out_trade_no);

    List<UserBill> findByUserIdAndBillIdLessThanOrderByBillIdDesc(long userId, long billId, Pageable pageable);

    List<UserBill> findByBillTypeAndDealStatusAndTTimeLessThan(int billType, int dealStatus, Date tTime);
}
