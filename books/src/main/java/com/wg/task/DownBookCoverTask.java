package com.wg.task;

import com.wg.book.domain.Book;
import com.wg.book.domain.TempBookCover;
import com.wg.book.utils.BookUtils;
import com.wg.common.Constant;
import com.wg.common.Enum.book.DownStatus;
import com.wg.common.utils.ImageUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.bookDao;
import static com.wg.common.utils.dbutils.DaoUtils.tempBookCoverDao;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-8-28
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */

@Component
public class DownBookCoverTask {

    @Transactional
    @Scheduled(fixedDelay = 180000)//三分钟一次
    public void downloadBookImageEx() {
        List<TempBookCover> tempBookCoverList = tempBookCoverDao.findByDownStatusOrderByCreatedTimeAsc(DownStatus.NotDown.getStatus(), new PageRequest(0, 100)).getContent();
        if (tempBookCoverList.size() > 0) {
            logger.info("[" + TimeUtils.getCurrentDate() + "] down book cover task start");
            for (TempBookCover tempBookCover : tempBookCoverList) {
                Book book = bookDao.findOne(tempBookCover.getBookId());
                String url = book.getCover();
                String path = url.contains("lpic") ? ImageUtils.saveImage(Constant.BOOK_COVER_FOLDER, book.getBookId(), url) : null;
                url = path == null ? url.replace("lpic", "mpic") : url;
                path = path == null && url.contains("mpic") ? ImageUtils.saveImage(Constant.BOOK_COVER_FOLDER, book.getBookId(), url) : path;
                url = path == null ? url.replace("mpic", "spic") : url;
                path = path == null && url.contains("spic") ? ImageUtils.saveImage(Constant.BOOK_COVER_FOLDER, book.getBookId(), url) : path;
                book.setLocalCover(path != null);
                book.setCover(path == null ? url : path);
                book = UpdateUtils.updateBook(book);
                tempBookCover.setDownStatus(path == null ? DownStatus.DownFailed.getStatus() : DownStatus.DownSuccess.getStatus());
                tempBookCover = tempBookCoverDao.save(tempBookCover);
            }
            BookUtils.deleteTempBookCover(tempBookCoverList);
        }
    }
}
