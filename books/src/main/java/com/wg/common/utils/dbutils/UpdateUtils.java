package com.wg.common.utils.dbutils;

import com.wg.book.domain.Book;
import com.wg.booksheet.domain.BookSheet;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.common.Enum.solr.CallType;
import com.wg.common.Enum.solr.SolrType;
import com.wg.solr.utils.SolrUtils;
import com.wg.user.domain.UserInfo;
import com.wg.userbook.domain.UserBook;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/11/13 0013.
 */
public class UpdateUtils {
    //book
    public static Book updateBook(Book book) {
        book = bookDao.save(book);
        SolrUtils.tryUpdate(SolrType.Book.getType(), CallType.Api.getType());
        return book;
    }

    //book bookgroup
    public static BookGroup updateBookCommunity(BookGroup bookGroup) {
        bookGroup = bookGroupDao.save(bookGroup);
        SolrUtils.tryUpdate(SolrType.BookGroup.getType(), CallType.Api.getType());
        return bookGroup;
    }

    //booksheet
    public static BookSheet updateBookSheet(BookSheet bookSheet) {
        bookSheet = bookSheetDao.save(bookSheet);
        SolrUtils.tryUpdate(SolrType.BookSheet.getType(), CallType.Api.getType());
        return bookSheet;
    }

    //user
    public static UserInfo updateUserInfo(UserInfo userInfo) {
        userInfo = userInfoDao.save(userInfo);
        SolrUtils.tryUpdate(SolrType.User.getType(), CallType.Api.getType());
        return userInfo;
    }

    //userbook
    public static UserBook updateUserBook(UserBook userBook) {
        userBook = userBookDao.save(userBook);
        SolrUtils.tryUpdate(SolrType.UserBook.getType(), CallType.Api.getType());
        return userBook;
    }
}
