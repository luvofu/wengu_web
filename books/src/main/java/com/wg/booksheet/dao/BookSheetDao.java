package com.wg.booksheet.dao;

import com.wg.booksheet.domain.BookSheet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wzhonggo on 8/31/2016.
 */
@Repository
public interface BookSheetDao extends JpaRepository<BookSheet, Long> {
    List<BookSheet> findByUserIdOrderByCreatedTimeDesc(long userId);

    Slice<BookSheet> findAllByUpdatedTimeGreaterThanAndDescriptionIsNotNullOrderByCollectionNumDesc(Date startDate, Pageable pageable);

    Slice<BookSheet> findAllByUpdatedTimeLessThanAndDescriptionIsNotNullOrderByCollectionNumDesc(Date startDate, Pageable pageable);

    List<BookSheet> findByUserId(Long userId);

    long countByUserId(long userId);
}

