package com.wg.book.dao;

import com.wg.book.domain.BookKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wzhonggo on 8/15/2016.
 */
@Repository
public interface BookKeywordDao extends JpaRepository<BookKeyword, Long> {
    BookKeyword findByKeyword(String keyword);
}
