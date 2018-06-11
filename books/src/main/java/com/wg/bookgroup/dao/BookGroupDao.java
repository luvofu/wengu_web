package com.wg.bookgroup.dao;

import com.wg.bookgroup.domain.BookGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/15/2016.
 */
@Repository
public interface BookGroupDao extends JpaRepository<BookGroup, Long> {
    List<BookGroup> findByTitle(String title);
}
