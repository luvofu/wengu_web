package com.wg.common.model.response;

import com.wg.book.domain.Book;
import com.wg.book.model.response.BookEntityResponse;
import com.wg.booksheet.domain.BookSheet;
import com.wg.booksheet.model.response.BookSheetEntityResponse;
import com.wg.common.Constant;
import com.wg.common.Enum.common.HomeType;
import com.wg.common.domain.Home;
import com.wg.common.domain.HotBook;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.bookgroup.model.response.BookCommunityCommentResponse;
import com.wg.user.domain.UserToken;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午4:23
 * To change this template use File | Settings | File Templates.
 */
public class CommonHomeResponse {
    private List<BookEntityResponse> bookList;
    private List<BookSheetEntityResponse> bookSheetList;
    private List<BookCommunityCommentResponse> commentList;

    public CommonHomeResponse(UserToken userToken, boolean showComment) {
        bookList = new ArrayList<BookEntityResponse>();
        for (HotBook hotBook : DaoUtils.hotBookDao.findByIsGet(true)) {
            Book book = DaoUtils.bookDao.findOne(hotBook.getBookId());
            if (book != null) {
                bookList.add(new BookEntityResponse(book));
            }
            if (bookList.size() >= Constant.PAGE_NUM_MEDIUM) {
                break;
            }
        }

        bookSheetList = new ArrayList<BookSheetEntityResponse>();
        for (Home home : DaoUtils.homeDao.findByObjTypeOrderByHeatDesc(HomeType.BookSheet.getType(), new PageRequest(0, Constant.HOME_GET_NUM)).getContent()) {
            BookSheet bookSheet = DaoUtils.bookSheetDao.findOne(home.getObjId());
            if (bookSheet != null) {
                bookSheetList.add(new BookSheetEntityResponse(bookSheet));
            }
            if (bookSheetList.size() >= Constant.PAGE_NUM_MEDIUM) {
                break;
            }
        }

        if (showComment) {
            commentList = new ArrayList<BookCommunityCommentResponse>();
            for (Home home : DaoUtils.homeDao.findByObjTypeOrderByHeatDesc(HomeType.CommunityComment.getType(), new PageRequest(0, Constant.HOME_GET_NUM)).getContent()) {
                GroupComment groupComment = DaoUtils.groupCommentDao.findOne(home.getObjId());
                if (groupComment != null) {
                    commentList.add(new BookCommunityCommentResponse(groupComment, userToken));
                }
                if (commentList.size() >= Constant.PAGE_NUM_MEDIUM) {
                    break;
                }
            }
        }
    }

    public List<BookEntityResponse> getBookList() {
        return bookList;
    }

    public void setBookList(List<BookEntityResponse> bookList) {
        this.bookList = bookList;
    }

    public List<BookSheetEntityResponse> getBookSheetList() {
        return bookSheetList;
    }

    public void setBookSheetList(List<BookSheetEntityResponse> bookSheetList) {
        this.bookSheetList = bookSheetList;
    }

    public List<BookCommunityCommentResponse> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<BookCommunityCommentResponse> commentList) {
        this.commentList = commentList;
    }
}
