package com.wg.book.dao;

import com.wg.book.domain.BookRemark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午1:58
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface BookRemarkDao extends JpaRepository<BookRemark, Long> {
    Slice<BookRemark> findByBookIdOrderByCreatedTimeDesc(long bookId, Pageable pageable);

    long countByBookId(long bookId);

    @Modifying
    @Query(value = "delete from BookRemark bookRemark where bookRemark.userId=?1")
    void deleteBookRemarkByUserId(Long userId);
}
