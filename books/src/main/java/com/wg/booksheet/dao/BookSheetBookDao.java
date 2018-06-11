package com.wg.booksheet.dao;

import com.wg.booksheet.domain.BookSheetBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 9/2/2016.
 */
@Repository
public interface BookSheetBookDao extends JpaRepository<BookSheetBook, Long> {
    List<BookSheetBook> findBySheetIdOrderByCreatedTimeDesc(long sheetId);

    List<BookSheetBook> findBySheetId(long sheetId);

    BookSheetBook findBySheetIdAndBookId(long sheetId, long bookId);

    long countBySheetId(long sheetId);
}
