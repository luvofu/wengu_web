package com.wg.user.dao;

import com.wg.user.domain.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/4/2016.
 */
@Repository
public interface UserTokenDao extends JpaRepository<UserToken, Long> {
    UserToken findByUserIdAndPlatform(long userId, String platform);

    UserToken findByToken(String token);

    List<UserToken> findByUserId(long userId);
}
