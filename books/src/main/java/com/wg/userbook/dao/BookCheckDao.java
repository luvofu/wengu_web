package com.wg.userbook.dao;

import com.wg.userbook.domain.BookCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by wzhonggo on 8/16/2016.
 */
@Repository
public interface BookCheckDao extends JpaRepository<BookCheck, Long> {
    Slice<BookCheck> findByUserIdOrderByCreatedTimeDesc(long userId, Pageable pageable);

    Page<BookCheck> findByCheckStatusAndCreatedTimeBetweenOrderByCreatedTimeDesc(int checkStatus, Date startDate, Date endDate, Pageable pageable);

    @Modifying
    @Query(value = "delete from BookCheck bookCheck where bookCheck.userId=?1")
    void deleteByUserId(long userId);
}
