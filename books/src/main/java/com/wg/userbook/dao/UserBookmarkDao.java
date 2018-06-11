package com.wg.userbook.dao;

import com.wg.userbook.domain.UserBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/15/2016.
 */
@Repository
public interface UserBookmarkDao extends JpaRepository<UserBookmark, Long> {
    UserBookmark findByUserBookId(long userBookId);

    List<UserBookmark> findByUserIdOrderByUpdatedTimeDesc(long userId);
}
