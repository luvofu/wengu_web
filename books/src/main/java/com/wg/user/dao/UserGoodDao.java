package com.wg.user.dao;

import com.wg.user.domain.UserGood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
@Repository
public interface UserGoodDao extends JpaRepository<UserGood, Long> {
    UserGood findByUserIdAndGoodObjIdAndGoodType(long userId, long goodObjId, int goodType);

    List<UserGood> findByGoodObjIdAndGoodType(long goodObjId, int goodType);

    long countByGoodObjIdAndGoodType(long commentId, int goodType);

    List<UserGood> findByUserId(Long userId);
}
