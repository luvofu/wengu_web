package com.wg.useraccount.dao;

import com.wg.useraccount.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/3/12 0012.
 */
@Repository
public interface UserAccountDao extends JpaRepository<UserAccount,Long> {
    UserAccount findByUserId(long userId);
}
