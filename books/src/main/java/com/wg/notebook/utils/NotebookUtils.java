package com.wg.notebook.utils;

import com.wg.notebook.domain.Note;
import com.wg.notebook.domain.Notebook;
import com.wg.notebook.domain.Storyline;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import static com.wg.common.utils.dbutils.DaoUtils.noteDao;
import static com.wg.common.utils.dbutils.DaoUtils.storylineDao;

/**
 * Created by Administrator on 2016/12/8 0008.
 */
public class NotebookUtils {
    //情节排序步长
    public static final double SORT_STEP = 10;

    public static double getStorylineSort(Notebook notebook, long id) {
        Storyline pre = storylineDao.findOne(id);
        Slice<Storyline> storylineSlice = pre != null ? storylineDao.findByNotebookIdAndSortLessThanOrderBySortDesc(
                notebook.getNotebookId(), pre.getSort(), new PageRequest(0, 1)) :
                storylineDao.findByNotebookIdOrderBySortDesc(notebook.getNotebookId(), new PageRequest(0, 1));
        Storyline next = storylineSlice.getContent().size() == 1 ? storylineSlice.getContent().get(0) : null;
        double nextSort = next != null ? next.getSort() : 0;
        double preSort = pre != null ? pre.getSort() : nextSort + 2 * SORT_STEP;
        return (preSort + nextSort) / 2.0;
    }

    public static double getNoteSort(Notebook notebook, long id) {
        Note pre = noteDao.findOne(id);
        Slice<Note> noteSlice = pre != null ? noteDao.findByNotebookIdAndSortLessThanOrderBySortDesc(
                notebook.getNotebookId(), pre.getSort(), new PageRequest(0, 1)) :
                noteDao.findByNotebookIdOrderBySortDesc(notebook.getNotebookId(), new PageRequest(0, 1));
        Note next = noteSlice.getContent().size() == 1 ? noteSlice.getContent().get(0) : null;
        double nextSort = next != null ? next.getSort() : 0;
        double preSort = pre != null ? pre.getSort() : nextSort + 2 * SORT_STEP;
        return (preSort + nextSort) / 2.0;
    }
}
