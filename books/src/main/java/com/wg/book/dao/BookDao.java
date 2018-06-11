package com.wg.book.dao;

import com.wg.book.domain.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wzhonggo on 8/15/2016.
 */
@Repository
public interface BookDao extends JpaRepository<Book, Long> {
    Book findByisbn10(String isbn10);

    Book findByIsbn13(String isbn13);

    long countByCommunityId(long communityId);

    List<Book> findByCommunityId(long communityId);

    Slice<Book> findByCommunityId(long communityId, Pageable pageable);

    Slice<Book> findByCommunityIdOrderByEntryNum(long communityId, Pageable pageable);

    Slice<Book> findAllByUpdatedTimeGreaterThanOrderByEntryNumDesc(Date startDate, Pageable pageable);

    Slice<Book> findAllByUpdatedTimeLessThanOrderByEntryNumDesc(Date startDate, Pageable pageable);
}
