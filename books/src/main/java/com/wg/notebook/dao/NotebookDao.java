package com.wg.notebook.dao;

import com.wg.notebook.domain.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-3
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface NotebookDao extends JpaRepository<Notebook, Long> {
    List<Notebook> findByUserIdAndPermissionLessThanOrderByCreatedTimeDesc(long userId, int permission);

    Notebook findByUserIdAndBookId(long userId, long bookId);

    List<Notebook> findByUserId(Long userId);

    long countByUserId(long userId);
}
