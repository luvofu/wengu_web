package com.wg.user.dao;

import com.wg.user.domain.UserCollection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/18/2016.
 */
@Repository
public interface UserCollectionDao extends JpaRepository<UserCollection, Long> {
    UserCollection findByUserIdAndCollectObjIdAndCollectType(long userId, long collectObjId, int collectType);

    Slice<UserCollection> findByUserIdAndCollectTypeOrderByCreatedTimeDesc(long userId, int collectType, Pageable pageable);

    long countByUserIdAndCollectType(long userId, int collectType);

    List<UserCollection> findByCollectObjIdAndCollectType(long collectObjId, int collectType);

    long countByCollectObjIdAndCollectType(long collectObjId, int collectType);

    List<UserCollection> findByUserId(Long userId);
}
