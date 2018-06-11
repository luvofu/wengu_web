package com.wg.user.dao;

import com.wg.user.domain.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wzhonggo on 8/4/2016.
 */
@Repository
public interface ValidDao extends JpaRepository<Valid, Long> {
    Valid findByDeviceTokenAndRegMobileAndUseCondition(String deviceToken, String regMobile, int useCondition);
}
