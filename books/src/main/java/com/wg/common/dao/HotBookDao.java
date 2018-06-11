package com.wg.common.dao;

import com.wg.common.domain.HotBook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wzhonggo on 8/15/2016.
 */
@Repository
public interface HotBookDao extends JpaRepository<HotBook, Long> {
    HotBook findByBookId(long bookId);

    List<HotBook> findBySourceAndUpdatedTimeLessThan(int type, Date date);

    List<HotBook> findByIsGet(boolean isGet);
}
