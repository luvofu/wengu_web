package com.wg.booksheet.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.book.domain.Book;
import com.wg.book.model.response.BookEntityResponse;
import com.wg.book.utils.BookUtils;
import com.wg.booksheet.domain.BookSheet;
import com.wg.booksheet.domain.BookSheetBook;
import com.wg.booksheet.model.request.BookSheetEditRecommendRequest;
import com.wg.booksheet.model.request.BookSheetRequest;
import com.wg.booksheet.model.response.BookSheetAddResponse;
import com.wg.booksheet.model.response.BookSheetDetailResponse;
import com.wg.booksheet.model.response.BookSheetEntityResponse;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.ResponseContent;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.solr.modle.QueryRes;
import com.wg.solr.utils.BookSheetSolr;
import com.wg.user.domain.UserToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by wzhonggo on 8/31/2016.
 */
@Controller
public class BookSheetContoller {

    @Transactional
    @RequestMapping(value = "/api/bookSheet/lib", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String lib(HttpServletRequest request,
                      HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest) {
        ResponseContent responseContent = new ResponseContent();
        List<BookSheetEntityResponse> bookSheetEntityResponseList = new ArrayList<BookSheetEntityResponse>();
        String tags = bookSheetRequest.getFilterType();
        List<BookSheet> bookSheetList = BookSheetSolr.findByTagContainsOrderBySortTypeDesc(
                tags, bookSheetRequest.getPage(), bookSheetRequest.getSortType()).getDocList();
        for (BookSheet bookSheet : bookSheetList) {
            bookSheetEntityResponseList.add(new BookSheetEntityResponse(bookSheet));
        }
        responseContent.putData("bookSheetList", bookSheetEntityResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookSheet/add", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String add(HttpServletRequest request,
                      HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookSheetRequest.getToken());
        String name = bookSheetRequest.getName();
        if (name != null) {
            BookSheet bookSheet = AddUtils.addBookSheet(userToken.getUserId(), name, null, null);
            if (bookSheet != null) {
                BookSheetAddResponse bookSheetAddResponse = new BookSheetAddResponse(bookSheet);
                responseContent.setData(bookSheetAddResponse);
            } else {
                responseContent.update(ResponseCode.CREATE_FAILD);
            }
        } else {
            responseContent.update(ResponseCode.NULL_BOOKSHEET_NAME);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookSheet/delete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookSheetRequest.getToken());
        BookSheet bookSheet = bookSheetDao.findOne(bookSheetRequest.getSheetId());
        if (bookSheet != null) {
            if (userToken.getUserId() == bookSheet.getUserId()) {
                DeleteUtils.deleteBookSheet(bookSheet);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.BOOKSHEET_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookSheet/personal", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String personal(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookSheetRequest.getToken());
        if (userInfoDao.findOne(bookSheetRequest.getUserId()) != null) {
            int permission = Utils.getPermission(userToken, bookSheetRequest.getUserId());
            List<BookSheet> bookSheetList = bookSheetDao.findByUserIdOrderByCreatedTimeDesc(bookSheetRequest.getUserId());
            List<BookSheetEntityResponse> bookSheetEntityResponseList = new ArrayList<BookSheetEntityResponse>();
            for (BookSheet bookSheet : bookSheetList) {
                bookSheetEntityResponseList.add(new BookSheetEntityResponse(bookSheet));
            }
            responseContent.putData("relationType", permission);
            responseContent.putData("bookSheetList", bookSheetEntityResponseList);
        } else {
            responseContent.update(ResponseCode.USER_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookSheet/detail", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String detail(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookSheetRequest.getToken());
        BookSheet bookSheet = bookSheetDao.findOne(bookSheetRequest.getSheetId());
        if (bookSheet != null) {
            int permission = Utils.getPermission(userToken, bookSheet.getUserId());
            BookSheetDetailResponse bookSheetDetailResponse = new BookSheetDetailResponse(bookSheet, userToken);
            responseContent.putData("relationType", permission);
            responseContent.putData("bookSheetDetail", bookSheetDetailResponse);
        } else {
            responseContent.update(ResponseCode.BOOKSHEET_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookSheet/addBook", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String addBook(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookSheetRequest.getToken());
        BookSheet bookSheet = bookSheetDao.findOne(bookSheetRequest.getSheetId());
        Book book = bookDao.findOne(bookSheetRequest.getBookId());
        if (bookSheet != null) {
            if (book != null) {
                if (bookSheet.getUserId() == userToken.getUserId()) {
                    AddUtils.addBookSheetBook(bookSheet, book);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.BOOK_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.BOOKSHEET_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookSheet/deleteBook", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String deleteBook(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookSheetRequest.getToken());

        BookSheetBook bookSheetBook = bookSheetBookDao.findOne(bookSheetRequest.getSheetBookId());
        if (bookSheetBook != null) {
            BookSheet bookSheet = bookSheetDao.findOne(bookSheetBook.getSheetId());
            if (bookSheet.getUserId() == userToken.getUserId()) {
                DeleteUtils.deleteBookSheetBook(bookSheetBook);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.BOOK_NOT_IN_SHEET);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookSheet/edit", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String edit(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookSheetRequest.getToken());
        BookSheet bookSheet = bookSheetDao.findOne(bookSheetRequest.getSheetId());
        if (bookSheet != null) {
            String name = bookSheetRequest.getName();
            String description = bookSheetRequest.getDescription();
            String tag = bookSheetRequest.getTag();
            if (bookSheet.getUserId() == userToken.getUserId()) {
                if (name != null) {
                    bookSheet.setName(bookSheetRequest.getName().trim());
                }
                if (description != null) {
                    bookSheet.setDescription(!description.equals("") ? description : null);
                }
                if (tag != null) {
                    bookSheet.setTag(tag);
                }
                bookSheet = UpdateUtils.updateBookSheet(bookSheet);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.BOOKSHEET_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookSheet/bookSearch", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookSearch(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest) {
        ResponseContent responseContent = new ResponseContent();
        String keyword = bookSheetRequest.getKeyword();
        long sheetId = bookSheetRequest.getSheetId();
        int page = bookSheetRequest.getPage();
        if (StringUtils.isNotBlank(keyword) && sheetId != -1) {
            List<BookEntityResponse> bookSheetBookEntityResponses = new ArrayList<BookEntityResponse>();
            QueryRes<Book> queryRes = BookUtils.bookSearch(keyword, page);
            for (Book book : queryRes.getDocList()) {
                bookSheetBookEntityResponses.add(
                        new BookEntityResponse(book, bookSheetBookDao.findBySheetIdAndBookId(sheetId, book.getBookId()) != null));
            }
            responseContent.putData("bookList", bookSheetBookEntityResponses);
            responseContent.putData("total", queryRes.getTotalRes());
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * version 101 ** version 101 ** version 101 ** version 101 ** version 101 ** version 101 ** version 101 ** version 101 ** version 101
     **/

    @Transactional
    @RequestMapping(value = "/api/bookSheet/add_v101", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String add_v101(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, BookSheetRequest bookSheetRequest, @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookSheetRequest.getToken());
        String name = bookSheetRequest.getName();
        String description = bookSheetRequest.getDescription();
        if (StringUtils.isNotBlank(name) && file != null) {
            BookSheet bookSheet = AddUtils.addBookSheet(userToken.getUserId(), name, StringUtils.isBlank(description) ? null : description, file);
            if (bookSheet != null) {
                responseContent.setData(new BookSheetAddResponse(bookSheet));
            } else {
                responseContent.update(ResponseCode.CREATE_FAILD);
            }
        } else {
            responseContent.update(ResponseCode.NULL_BOOKSHEET_NAME);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103
     **/
    @Transactional
    @RequestMapping(value = "/api/bookSheet/editRecommend", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String edit(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, BookSheetEditRecommendRequest bookSheetEditRecommendRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookSheetEditRecommendRequest.getToken());
        long sheetbookId = bookSheetEditRecommendRequest.getSheetBookId();
        String recommend = bookSheetEditRecommendRequest.getRecommend();
        BookSheetBook bookSheetBook = bookSheetBookDao.findOne(sheetbookId);
        if (bookSheetBook != null) {
            BookSheet bookSheet = bookSheetDao.findOne(bookSheetBook.getSheetId());
            if (bookSheet.getUserId() == userToken.getUserId()) {
                bookSheetBook.setRecommend(recommend);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.SHEET_BOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }
}
