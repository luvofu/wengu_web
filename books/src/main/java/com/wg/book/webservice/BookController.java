package com.wg.book.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.book.domain.Book;
import com.wg.book.domain.BookRemark;
import com.wg.book.model.request.BookRequest;
import com.wg.book.model.response.BookDetailResponse;
import com.wg.book.model.response.BookEntityResponse;
import com.wg.book.model.response.BookRemarkResponse;
import com.wg.book.utils.BookUtils;
import com.wg.common.Constant;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.ResponseContent;
import com.wg.solr.utils.BookSolr;
import com.wg.solr.modle.QueryRes;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by wzhonggo on 8/24/2016.
 */
@Controller
public class BookController {

    /* *
    *@see BookEntityResponse
     * @param request
     * @param response
     * @param modelMap
     * @param bookRequest String keywrod; int page;
     * @return
     */
    @Transactional
    @RequestMapping(value = "/api/book/search", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String search(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, BookRequest bookRequest) {
        ResponseContent responseContent = new ResponseContent();
        String keyword = bookRequest.getKeyword();
        int page = bookRequest.getPage();
        if (StringUtils.isNotBlank(keyword)) {
            List<BookEntityResponse> bookEntityResponseList = new ArrayList<BookEntityResponse>();
            QueryRes<Book> queryRes = BookUtils.bookSearch(keyword, page);
            for (Book book : queryRes.getDocList()) {
                bookEntityResponseList.add(new BookEntityResponse(book));
            }
            responseContent.putData("bookList", bookEntityResponseList);
            responseContent.putData("total", queryRes.getTotalRes());
        } else {
            responseContent.update(ResponseCode.NULL_KEYWORD);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/book/lib", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String lib(HttpServletRequest request,
                      HttpServletResponse response, ModelMap modelMap, BookRequest bookRequest) {
        ResponseContent responseContent = new ResponseContent();
        String category = bookRequest.getFilterType();
        int page = bookRequest.getPage();
        int sortType = bookRequest.getSortType();
        List<BookEntityResponse> bookEntityResponseList = new ArrayList<BookEntityResponse>();
        QueryRes<Book> queryRes = BookSolr.findByCategoryOrderBySortTypeDesc(category, page, sortType);
        for (Book book : queryRes.getDocList()) {
            bookEntityResponseList.add(new BookEntityResponse(book));
        }
        responseContent.putData("bookList", bookEntityResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/book/detail", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookDetail(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap, BookRequest bookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = null;
        if (bookRequest.getToken() != null) {
            userToken = userTokenDao.findByToken(bookRequest.getToken());
        }
        Book book = bookDao.findOne(bookRequest.getBookId());
        if (book != null) {
            BookDetailResponse bookDetailResponse = new BookDetailResponse(book, userToken);
            responseContent.setData(bookDetailResponse);
        } else {
            responseContent.update(ResponseCode.BOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/book/remark", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String remark(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, BookRequest bookRequest) {
        ResponseContent responseContent = new ResponseContent();
        Pageable pageable = new PageRequest(bookRequest.getPage(), Constant.PAGE_NUM_LARGE);
        Slice<BookRemark> bookRemarkSlice = bookRemarkDao.findByBookIdOrderByCreatedTimeDesc(bookRequest.getBookId(), pageable);
        List<BookRemarkResponse> bookRemarkResponses = new ArrayList<BookRemarkResponse>();
        for (BookRemark bookRemark : bookRemarkSlice.getContent()) {
            if (StringUtils.isNotBlank(bookRemark.getRemark())) {
                UserInfo userInfo = userInfoDao.findOne(bookRemark.getUserId());
                bookRemarkResponses.add(new BookRemarkResponse(bookRemark, userInfo));
            }
        }
        responseContent.putData("bookRemarkList", bookRemarkResponses);
        return JSON.toJSONString(responseContent);
    }
}
