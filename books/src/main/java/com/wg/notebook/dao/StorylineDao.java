package com.wg.notebook.dao;

import com.wg.notebook.domain.Storyline;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/12/8 0008.
 */
@Repository
public interface StorylineDao extends JpaRepository<Storyline, Long> {
    Slice<Storyline> findByNotebookIdAndSortLessThanOrderBySortDesc(long notebookId, double sort, Pageable pageable);

    Slice<Storyline> findByNotebookIdOrderBySortDesc(long notebookId, Pageable pageable);

    long countByNotebookId(long notebookId);

    List<Storyline> findByNotebookId(long notebookId);
}