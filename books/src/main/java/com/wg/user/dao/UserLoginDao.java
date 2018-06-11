package com.wg.user.dao;

import com.wg.user.domain.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wzhonggo on 8/2/2016.
 */
@Repository
public interface UserLoginDao extends JpaRepository<UserLogin, Long> {
    UserLogin findByUserId(long userId);
    UserLogin findByUserName(String userName);
    UserLogin findByRegMobile(String regMobile);
    UserLogin findByWeiboId(String weiboId);
    UserLogin findByWeixinId(String weixinId);
}
