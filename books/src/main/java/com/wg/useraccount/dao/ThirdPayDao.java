package com.wg.useraccount.dao;

import com.wg.useraccount.domain.ThirdPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
@Repository
public interface ThirdPayDao extends JpaRepository<ThirdPay, Long> {
    List<ThirdPay> findByUserId(long userId);

    ThirdPay findByUserIdAndPayType(long userId, int payType);
}
