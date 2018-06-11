package com.wg.user.dao;

import com.wg.user.domain.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/15/2016.
 */
@Repository
public interface UserFriendDao extends JpaRepository<UserFriend, Long> {
    List<UserFriend> findByUserIdOrderByCreatedTimeDesc(long userId);

    List<UserFriend> findByFriendIdOrderByCreatedTimeDesc(long userId);

    UserFriend findByUserIdAndFriendId(long userId, long friendId);

    long countByUserId(long userId);

    long countByFriendId(long userId);

    @Query(value = "select friendId from UserFriend userFriend where userFriend.userId=?1")
    List<Long> getFriendUserIds(long userId);

    List<UserFriend> findByUserId(Long userId);

    List<UserFriend> findByFriendId(Long userId);
}
