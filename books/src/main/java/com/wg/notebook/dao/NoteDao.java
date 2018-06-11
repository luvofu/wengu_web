package com.wg.notebook.dao;

import com.wg.notebook.domain.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-3
 * Time: 下午2:41
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface NoteDao extends JpaRepository<Note, Long> {
    Slice<Note> findByNotebookIdAndSortLessThanOrderBySortDesc(long notebookId, double sort, Pageable pageable);

    Slice<Note> findByNotebookIdOrderBySortDesc(long notebookId, Pageable pageable);

    Slice<Note> findByNotebookIdOrderByCreatedTimeDesc(long notebookId, Pageable pageable);

    List<Note> findByNotebookId(long notebookId);

    long countByNotebookId(long notebookId);

    List<Note> findByNotebookIdOrderByCreatedTimeAsc(long notebookId);
}
