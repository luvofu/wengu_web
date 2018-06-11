package com.wg.book.dao;

import com.wg.book.domain.TempBookCover;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/15/2016.
 */
@Repository
public interface TempBookCoverDao extends JpaRepository<TempBookCover, Long> {
    Slice<TempBookCover> findByDownStatusOrderByCreatedTimeAsc(int downStatus, Pageable pageable);
}
