package com.wg.bookorder.dao;

import com.wg.bookorder.domain.OrderEvaluate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/2/14 0014.
 */
@Repository
public interface OrderEvaluateDao extends JpaRepository<OrderEvaluate, Long> {
    Slice<OrderEvaluate> findByOwnerIdAndBookIdOrderByCreatedTimeDesc(long userId, long bookId, Pageable pageable);
}
