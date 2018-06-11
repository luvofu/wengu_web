package com.wg.bookcircle.dao;

import com.wg.bookcircle.domain.BookCircleDynamic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/9/1.
 */
@Repository
public interface BookCircleDynamicDao extends JpaRepository<BookCircleDynamic, Long> {
    Slice<BookCircleDynamic> findByUserIdOrderByCreatedTimeDesc(long userId, Pageable pageable);

    Slice<BookCircleDynamic> findByUserIdAndPermissionLessThanOrderByCreatedTimeDesc(long userId, int permission, Pageable pageable);

    Slice<BookCircleDynamic> findByUserIdInAndPermissionLessThanOrderByCreatedTimeDesc(HashSet<Long> ids, int permission, Pageable pageable);

    List<BookCircleDynamic> findByUserId(Long userId);
}
