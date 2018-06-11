package com.wg.userbook.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.book.domain.Book;
import com.wg.book.domain.BookRemark;
import com.wg.book.model.response.BookEntityResponse;
import com.wg.book.utils.BookUtils;
import com.wg.bookcircle.utils.BookCircleUtils;
import com.wg.bookgroup.utils.BookGroupUtils;
import com.wg.common.Constant;
import com.wg.common.Enum.bookcircle.DynamicType;
import com.wg.common.Enum.bookcircle.LinkType;
import com.wg.common.Enum.common.Permission;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.userbook.*;
import com.wg.common.ResponseContent;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.solr.modle.QueryRes;
import com.wg.solr.utils.UserBookSolr;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.userbook.domain.UserBook;
import com.wg.userbook.domain.UserCategory;
import com.wg.userbook.model.CategoryStatis;
import com.wg.userbook.model.CategoryStatisEx;
import com.wg.userbook.model.request.CategoryBooks;
import com.wg.userbook.model.request.UserBookAddScanBooksRequest;
import com.wg.userbook.model.request.UserBookPersonalRequest;
import com.wg.userbook.model.request.UserBookRequest;
import com.wg.userbook.model.response.UserBookCategoryStatisExResponse;
import com.wg.userbook.model.response.UserBookCategoryStatisResponse;
import com.wg.userbook.model.response.UserBookDetailResponse;
import com.wg.userbook.model.response.UserBookInfoResponse;
import com.wg.userbook.utils.UserBookUtils;
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
 * Created by wzhonggo on 8/30/2016.
 */
@Controller
public class UserBookController {

    @Transactional
    @RequestMapping(value = "/api/userBook/entry", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String userBookEntry(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        Book book = bookDao.findOne(userBookRequest.getBookId());
        UserBook userBook = userBookDao.findByBookIdAndUserId(book.getBookId(), userToken.getUserId());
        if (book != null) {
            if (userBook == null) {
                userBook = AddUtils.addUserBook(userToken.getUserId(), book, null, EntryType.SearchInput.getType());
                if (userBook != null) {
                    AddUtils.addBookCircleDynamic(
                            userToken.getUserId(), BookCircleUtils.getBookEntryDynamicContent(book.getTitle()),
                            0, LinkType.Common.getType(), null, null, Permission.Open.getType(), DynamicType.System.getType());
                } else {
                    responseContent.update(ResponseCode.CREATE_FAILD);
                }
            } else {
                responseContent.update(ResponseCode.USERBOOK_ALREADY_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.BOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/delete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        if (StringUtils.isNotBlank(userBookRequest.getUserBookIdList())) {
            boolean deleteAble = true;
            List<UserBook> userBookList = new ArrayList<UserBook>();
            for (Long id : Utils.getLongListByString(userBookRequest.getUserBookIdList())) {
                UserBook userBook = userBookDao.findOne(id);
                if (userBook != null) {
                    if (userToken.getUserId() == userBook.getUserId()) {
                        userBookList.add(userBook);
                    } else {
                        responseContent.update(ResponseCode.ILLEGAL_USER);
                        deleteAble = false;
                        break;
                    }
                }
            }
            if (deleteAble) {
                DeleteUtils.deleteUserBook(userBookList);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/personal", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String userBookPersonal(HttpServletRequest request,
                                   HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        long userId = userBookRequest.getUserId();
        if (userInfoDao.findOne(userId) != null) {
            int permission = Utils.getPermission(userToken, userId);
            Pageable pageable = new PageRequest(userBookRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<UserBook> userBookSlice = userBookDao.findByUserIdAndPermissionLessThanOrderByCreatedTimeDesc(userId, permission + 1, pageable);
            List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
            for (UserBook userBook : userBookSlice.getContent()) {
                userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
            }
            responseContent.putData("relationType", permission);
            responseContent.putData("userBookList", userBookInfoResponseList);
        } else {
            responseContent.update(ResponseCode.USER_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/detail", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String detail(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        UserBook userBook = userBookDao.findOne(userBookRequest.getUserBookId());
        if (userBook != null) {
            BookRemark bookRemark = bookRemarkDao.findOne(userBook.getRemarkId());
            UserBookDetailResponse userBookDetailResponse = new UserBookDetailResponse(userBook, bookRemark);
            responseContent.setData(userBookDetailResponse);
        } else {
            responseContent.update(ResponseCode.USERBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/edit", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String edit(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        UserBook userBook = userBookDao.findOne(userBookRequest.getUserBookId());
        if (userBook != null) {
            if (userBook.getUserId() == userToken.getUserId()) {
                if (userBookRequest.getGetType() == GetType.Other.getType()
                        || userBookRequest.getGetType() == GetType.Buy.getType()
                        || userBookRequest.getGetType() == GetType.Present.getType()) {
                    userBook.setGetType(userBookRequest.getGetType());
                }
                if (userBookRequest.getBookType() == BookType.Paper.getType()
                        || userBookRequest.getBookType() == BookType.Electronic.getType()
                        || userBookRequest.getBookType() == BookType.Other.getType()) {
                    userBook.setBookType(userBookRequest.getBookType());
                }
                if (userBookRequest.getReadStatus() == ReadStatus.Finish.getType()
                        || userBookRequest.getReadStatus() == ReadStatus.NotRead.getType()
                        || userBookRequest.getReadStatus() == ReadStatus.Reading.getType()) {
                    userBook.setReadStatus(userBookRequest.getReadStatus());
                }
                if (userBookRequest.getGetTime() != null)
                    userBook.setGetTime(TimeUtils.getDate(Long.parseLong(userBookRequest.getGetTime())));
                if (userBookRequest.getGetPlace() != null)
                    userBook.setGetPlace(userBookRequest.getGetPlace().trim());
                if (userBookRequest.getReadPlace() != null)
                    userBook.setReadPlace(userBookRequest.getReadPlace().trim());
                if (userBookRequest.getReadTime() != null)
                    userBook.setReadTime(TimeUtils.getDate(Long.parseLong(userBookRequest.getReadTime())));
                if (userBookRequest.getTag() != null) {
                    userBook.setTag(userBookRequest.getTag().trim());
                    BookGroupUtils.updateCs(userBook);
                }
                if (userBookRequest.getOther() != null)
                    userBook.setOther(userBookRequest.getOther());
                if (userBookRequest.getPermission() == Permission.Open.getType()
                        || userBookRequest.getPermission() == Permission.Friend.getType()
                        || userBookRequest.getPermission() == Permission.Personal.getType()) {
                    userBook.setPermission(userBookRequest.getPermission());
                }
                if (userBookRequest.getRating() > 0f) {
                    BookUtils.updateBookRating(userBook, userBookRequest.getRating());
                    userBook.setRating(userBookRequest.getRating());
                }
                if (userBookRequest.getRemark() != null) {
                    BookRemark bookRemark = bookRemarkDao.findOne(userBook.getRemarkId());
                    if (bookRemark == null) {
                        bookRemark = new BookRemark();
                        bookRemark.setBookId(userBook.getBookId());
                        bookRemark.setUserId(userBook.getUserId());
                        bookRemark.setRemark(userBookRequest.getRemark());
                        bookRemark = bookRemarkDao.save(bookRemark);
                        userBook.setRemarkId(bookRemark.getRemarkId());
                    } else {
                        bookRemark.setRemark(userBookRequest.getRemark().trim());
                        bookRemark = bookRemarkDao.save(bookRemark);
                    }
                }
                if (userBookRequest.getLease() != null || userBookRequest.getSale() != null) {
                    if (userBookRequest.getLease() != null) {
                        userBook.setLease(userBookRequest.getLease());
                    }
                    if (userBookRequest.getSale() != null) {
                        userBook.setSale(userBookRequest.getSale());
                    }
                    UserBookUtils.setLeaseSale(userBook);
                }
                if (userBookRequest.getEvaluation() > 0) {
                    userBook.setEvaluation(userBookRequest.getEvaluation());
                }
                if (userBookRequest.getDayRentGold() >= 0) {
                    userBook.setDayRentGold(userBookRequest.getDayRentGold());
                }
                if (userBookRequest.getBookDescription() != null) {
                    userBook.setDescription(userBookRequest.getBookDescription());
                }
                userBook = UpdateUtils.updateUserBook(userBook);
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.USERBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * version 101 ** version 101 ** version 101 ** version 101 ** version 101 ** version 101 ** version 101 ** version 101 ** version 101
     **/

    @Transactional
    @RequestMapping(value = "/api/userBook/search", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String search(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        String keyword = userBookRequest.getKeyword();
        List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
        QueryRes<UserBook> queryRes = UserBookSolr.findByKeywordAndUserIdAndPermission(
                keyword, userToken.getUserId(), Permission.Personal.getType(), userBookRequest.getPage());
        for (UserBook userBook : queryRes.getDocList()) {
            userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
        }
        responseContent.putData("total", queryRes.getTotalRes());
        responseContent.putData("userBookList", userBookInfoResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/entry_v101", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String entry_v101(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        List<Long> ids = new ArrayList<Long>();
        if (userBookRequest.getBookIds() != null) {
            ids = Utils.getLongListByString(userBookRequest.getBookIds());
        }
        if (ids.size() > 0) {
            List<String> titleList = new ArrayList<String>();
            for (Book book : bookDao.findAll(ids)) {
                UserBook userBook = userBookDao.findByBookIdAndUserId(book.getBookId(), userToken.getUserId());
                if (userBook == null) {
                    AddUtils.addUserBook(userToken.getUserId(), book, null, EntryType.SearchInput.getType());
                    titleList.add(book.getTitle());
                }
            }
            if (titleList.size() > 0) {
                AddUtils.addBookCircleDynamic(
                        userToken.getUserId(), BookCircleUtils.getBookEntryDynamicContent(titleList),
                        0, LinkType.Common.getType(), null, null, Permission.Open.getType(), DynamicType.System.getType());
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/scanSearch", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String scanSearch(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        String isbn = userBookRequest.getIsbn();
        Book book = BookUtils.getBookByIsbn(isbn);
        if (book != null) {
            BookEntityResponse bookEntityResponse =
                    new BookEntityResponse(book, userBookDao.findByBookIdAndUserId(book.getBookId(), userToken.getUserId()) != null);
            responseContent.setData(bookEntityResponse);
        } else {
            responseContent.update(ResponseCode.BOOK_SCAN_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/scanBooks", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String scanBooks(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        List<Long> ids = Utils.getLongListByString(userBookRequest.getBookIds());
        List<BookEntityResponse> bookEntityResponseList = new ArrayList<BookEntityResponse>();
        for (Book book : bookDao.findAll(ids)) {
            bookEntityResponseList.add(
                    new BookEntityResponse(book, userBookDao.findByBookIdAndUserId(book.getBookId(), userToken.getUserId()) != null));
        }
        responseContent.setData(bookEntityResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/bookSearch", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookSearch(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        String keyword = userBookRequest.getKeyword();
        int page = userBookRequest.getPage();
        if (StringUtils.isNotBlank(keyword)) {
            List<BookEntityResponse> bookEntityResponseList = new ArrayList<BookEntityResponse>();
            QueryRes<Book> queryRes = BookUtils.bookSearch(keyword, page);
            for (Book book : queryRes.getDocList()) {
                bookEntityResponseList.add(
                        new BookEntityResponse(book, userBookDao.findByBookIdAndUserId(book.getBookId(), userToken.getUserId()) != null));
            }
            responseContent.putData("bookList", bookEntityResponseList);
            responseContent.putData("total", queryRes.getTotalRes());
        } else {
            responseContent.update(ResponseCode.NULL_KEYWORD);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/privateBooks", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String privateBooks(HttpServletRequest request,
                               HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
        Pageable pageable = new PageRequest(userBookRequest.getPage(), Constant.PAGE_NUM_LARGE);
        Slice<UserBook> userBookSlice = userBookDao.findByUserIdAndPermissionOrderByCreatedTimeDesc(
                userToken.getUserId(), Permission.Personal.getType(), pageable);
        for (UserBook userBook : userBookSlice.getContent()) {
            userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
        }
        responseContent.putData("userBookList", userBookInfoResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/categoryBooks", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String categoryBooks(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        long userId = userBookRequest.getUserId();
        String category = userBookRequest.getCategory();
        int categoryType = userBookRequest.getCategoryType();
        if (StringUtils.isNotBlank(category) && userId > 0
                && (categoryType == CategoryType.Normal.getType() || categoryType == CategoryType.Custom.getType())) {
            int permission = Utils.getPermission(userToken, userBookRequest.getUserId());
            List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
            Pageable pageable = new PageRequest(userBookRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<UserBook> userBookSlice;
            if (categoryType == CategoryType.Normal.getType()) {
                userBookSlice = userBookDao.findByUserIdAndPermissionLessThanAndCategoryOrderByCreatedTimeDesc(userId, permission + 1, category, pageable);
            } else {
                long categoryId = 0;
                List<UserCategory> userCategoryList = userCategoryDao.findByUserId(userToken.getUserId());
                for (UserCategory userCategory : userCategoryList) {
                    if (category.equals(userCategory.getCategory())) {
                        categoryId = userCategory.getCategoryId();
                        break;
                    }
                }
                userBookSlice = userBookDao.findByCategoryIdAndPermissionLessThanOrderByCreatedTimeDesc(categoryId, permission + 1, pageable);
            }
            for (UserBook userBook : userBookSlice.getContent()) {
                userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
            }
            responseContent.putData("relationType", permission);
            responseContent.putData("userBookList", userBookInfoResponseList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/categoryStatis", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String categoryStatis(HttpServletRequest request,
                                 HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        long userId = userBookRequest.getUserId();
        if (userId > 0) {
            int permission = Utils.getPermission(userToken, userId);
            List<CategoryStatis> normalCategoryStatisList = null;
            List<CategoryStatisEx> customCategoryStatisList = null;
            if (permission == Permission.Personal.getType()) {
                customCategoryStatisList = UserBookUtils.getCustomCategoryStatisEx(userId);
            }
            normalCategoryStatisList = UserBookUtils.getNormalCategoryStatis(userId);
            UserBookCategoryStatisResponse userBookCategoryStatisResponse = new UserBookCategoryStatisResponse(
                    normalCategoryStatisList, customCategoryStatisList);
            responseContent.putData("relationType", permission);
            responseContent.putData("categoryStatis", userBookCategoryStatisResponse);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/moveToCategory", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String moveToCategory(HttpServletRequest request,
                                 HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        List<Long> userBookIds = Utils.getLongListByString(userBookRequest.getUserBookIdList());
        String category = userBookRequest.getCategory();
        if (userBookIds.size() > 0 && StringUtils.isNotBlank(category)) {
            List<UserBook> userBookList = userBookDao.findAll(userBookIds);
            for (UserBook userBook : userBookList) {
                UserBookUtils.setUserBookCategory(userBook, category);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * version 102 ** version 102 ** version 102 ** version 102 ** version 102 ** version 102 ** version 102 ** version 102 ** version 102
     **/

    @Transactional
    @RequestMapping(value = "/api/userBook/personal_v102", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String personal_v102(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        int categoryType = userBookRequest.getCategoryType();
        long userId = userBookRequest.getUserId();
        long categoryId = userBookRequest.getCategoryId();
        String category = userBookRequest.getCategory();
        if (categoryType != -1 && userId != -1 && (
                categoryType == CategoryType.Normal.getType() && StringUtils.isNotBlank(category)
                        || categoryType == CategoryType.Custom.getType() && categoryId != -1
                        || categoryType == CategoryType.All.getType())) {
            int permission = Utils.getPermission(userToken, userId);
            Pageable pageable = new PageRequest(userBookRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<UserBook> userBookSlice;
            if (categoryType == CategoryType.Normal.getType() && category != null) {
                userBookSlice = userBookDao.findByUserIdAndPermissionLessThanAndCategoryOrderByCreatedTimeDesc(userId, permission + 1, category, pageable);
            } else if (categoryType == CategoryType.Custom.getType() && categoryId != -1) {
                userBookSlice = userBookDao.findByCategoryIdAndPermissionLessThanOrderByCreatedTimeDesc(categoryId, permission + 1, pageable);
            } else {
                userBookSlice = userBookDao.findByUserIdAndPermissionLessThanOrderByCreatedTimeDesc(userId, permission + 1, pageable);
            }
            List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
            for (UserBook userBook : userBookSlice.getContent()) {
                userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
            }
            responseContent.putData("relationType", permission);
            responseContent.putData("userBookList", userBookInfoResponseList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/addCategory", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String addCategory(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        String category = userBookRequest.getCategory();
        if (StringUtils.isNotBlank(category)) {
            UserCategory userCategory = userCategoryDao.findByUserIdAndCategory(userToken.getUserId(), category);
            if (userCategory == null) {
                userCategory = AddUtils.addUserCategory(userToken.getUserId(), category);
                responseContent.putData("categoryId", userCategory.getCategoryId());
            } else {
                responseContent.update(ResponseCode.EXIST_CATEGORY);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/deleteCategory", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String deleteCategory(HttpServletRequest request,
                                 HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        long categoryId = userBookRequest.getCategoryId();
        if (categoryId > 0) {
            UserCategory userCategory = userCategoryDao.findOne(categoryId);
            if (userCategory != null) {
                if (userToken.getUserId() == userCategory.getUserId()) {
                    DeleteUtils.deleteUserCategory(userCategory);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.NOT_EXIST_CATEGORY);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/editCategory", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String editCategory(HttpServletRequest request,
                               HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        long categoryId = userBookRequest.getCategoryId();
        String category = userBookRequest.getCategory();
        if (categoryId > 0 && StringUtils.isNotBlank(category)) {
            UserCategory userCategory = userCategoryDao.findOne(categoryId);
            if (userCategory != null) {
                if (userToken.getUserId() == userCategory.getUserId()) {
                    if (userCategoryDao.findByUserIdAndCategory(userToken.getUserId(), category) == null) {
                        userCategory.setCategory(category);
                        userCategory = userCategoryDao.save(userCategory);
                    } else {
                        responseContent.update(ResponseCode.EXIST_CATEGORY);
                    }
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.NOT_EXIST_CATEGORY);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/sortCategory", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String sortCategory(HttpServletRequest request,
                               HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        String categoryIds = userBookRequest.getCategoryIds();
        List<Long> categoryIdList = Utils.getLongListByString(categoryIds);
        if (categoryIdList.size() > 0) {
            List<UserCategory> userCategoryList = new ArrayList<UserCategory>();
            boolean categoryOk = true;
            for (Long categoryId : categoryIdList) {
                UserCategory userCategory = userCategoryDao.findOne(categoryId);
                if (userCategory != null) {
                    if (userCategory.getUserId() == userToken.getUserId()) {
                        userCategoryList.add(userCategory);
                    } else {
                        categoryOk = false;
                        responseContent.update(ResponseCode.ILLEGAL_USER);
                        break;
                    }
                } else {
                    categoryOk = false;
                    responseContent.update(ResponseCode.NOT_EXIST_CATEGORY);
                    break;
                }
            }
            if (categoryOk) {
                for (UserCategory userCategory : userCategoryList) {
                    userCategory.setSort(userCategoryList.indexOf(userCategory));
                }
                userCategoryDao.save(userCategoryList);
            }
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103
     **/

    @Transactional
    @RequestMapping(value = "/api/userBook/addScanBooks", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String addScanBooks(HttpServletRequest request,
                               HttpServletResponse response, ModelMap modelMap, UserBookAddScanBooksRequest userBookAddScanBooksRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookAddScanBooksRequest.getToken());
        String scanBooks = userBookAddScanBooksRequest.getScanBooks();
        if (StringUtils.isNotBlank(scanBooks)) {
            List<CategoryBooks> categoryBooksList = JSON.parseArray(scanBooks, CategoryBooks.class);
            List<String> titleList = new ArrayList<String>();
            for (CategoryBooks categoryBooks : categoryBooksList) {
                for (Book book : bookDao.findAll(categoryBooks.getBookIds())) {
                    UserBook userBook = userBookDao.findByBookIdAndUserId(book.getBookId(), userToken.getUserId());
                    if (userBook == null) {
                        AddUtils.addUserBook(userToken.getUserId(), book, categoryBooks.getCategory(), EntryType.SearchInput.getType());
                        titleList.add(book.getTitle());
                    }
                }
            }
            if (titleList.size() > 0) {
                AddUtils.addBookCircleDynamic(
                        userToken.getUserId(), BookCircleUtils.getBookEntryDynamicContent(titleList),
                        0, LinkType.Common.getType(), null, null, Permission.Open.getType(), DynamicType.System.getType());
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * version 104 ** version 104 ** version 104 ** version 104 ** version 104 ** version 104 ** version 104 ** version 104 ** version 104 *
     **/

    @Transactional
    @RequestMapping(value = "/api/userBook/categoryStatis_v104", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String categoryStatis_v104(HttpServletRequest request,
                                      HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        long userId = userBookRequest.getUserId();
        if (userId > 0) {
            int permission = Utils.getPermission(userToken, userId);
            UserBookCategoryStatisExResponse userBookCategoryStatisExResponse = new UserBookCategoryStatisExResponse(userId, permission);
            responseContent.putData("total", userBookDao.countByUserId(userId));
            responseContent.putData("relationType", permission);
            responseContent.putData("categoryGroupList", userBookCategoryStatisExResponse.getCategoryGroupList());
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/personal_v104", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String personal_v104(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap, UserBookPersonalRequest userBookPersonalRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookPersonalRequest.getToken());
        long userId = userBookPersonalRequest.getUserId();
        int categoryType = userBookPersonalRequest.getCategoryType();
        String category = userBookPersonalRequest.getCategory();
        int page = userBookPersonalRequest.getPage();
        if (userBookPersonalRequest.paramOk()) {
            int permission = Utils.getPermission(userToken, userId);
            Slice<UserBook> userBookSlice = UserBookUtils.getUserBooks(userId, categoryType, category, page, permission + 1);
            List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
            if (userBookSlice != null) {
                for (UserBook userBook : userBookSlice.getContent()) {
                    userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
                }
            }
            responseContent.putData("relationType", permission);
            responseContent.putData("userBookList", userBookInfoResponseList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/customCategory", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String customCategory(HttpServletRequest request,
                                 HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        responseContent.putData("categoryType", CategoryGroupType.Custom.getType());
        responseContent.putData("categoryTypeName", CategoryGroupType.Custom.getName());
        responseContent.putData("categoryStatisList", UserBookUtils.getCustomCategoryStatisEx(userToken.getUserId()));
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/editReadStatus", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String editReadStatus(HttpServletRequest request,
                                 HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        int readStatus = userBookRequest.getReadStatus();
        if (userBookRequest.getUserBookIds() != null
                && (readStatus == ReadStatus.Reading.getType()
                || readStatus == ReadStatus.NotRead.getType()
                || readStatus == ReadStatus.Finish.getType())) {
            List<Long> ids = Utils.getLongListByString(userBookRequest.getUserBookIds());
            for (UserBook userBook : userBookDao.findAll(ids)) {
                userBook.setReadStatus(readStatus);
                userBook = UpdateUtils.updateUserBook(userBook);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    /* version 200 * version 200 * version 200 * version 200 * version 200 * version 200 * version 200 * version 200 * version 200 */

    @Transactional
    @RequestMapping(value = "/api/userBook/search_v200", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String search_v200(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        long userId = userBookRequest.getUserId();
        String keyword = userBookRequest.getKeyword();
        int permission = Utils.getPermission(userToken, userId);
        List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
        QueryRes<UserBook> queryRes = UserBookSolr.findByKeywordAndUserIdAndPermission(
                keyword, userId, permission, userBookRequest.getPage());
        for (UserBook userBook : queryRes.getDocList()) {
            userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
        }
        responseContent.putData("total", queryRes.getTotalRes());
        responseContent.putData("relationType", permission);
        responseContent.putData("userBookList", userBookInfoResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/userBook/nearbySearch", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String nearbySearch(HttpServletRequest request,
                               HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        double longitude = userBookRequest.getLongitude();
        double latitude = userBookRequest.getLatitude();
        String keyword = userBookRequest.getKeyword();
        List<UserBookInfoResponse> userBookResponse = new ArrayList<UserBookInfoResponse>();
        List objectArray = UserBookUtils.getNearbyUserBook(longitude, latitude, keyword, userBookRequest.getPage());
        for (int index = 0; index < objectArray.size(); index++) {
            Object[] objects = (Object[]) objectArray.get(index);
            userBookResponse.add(new UserBookInfoResponse((UserBook) objects[0], (UserInfo) objects[1]));
        }
        responseContent.putData("userBookList", userBookResponse);
        return JSON.toJSONString(responseContent);
    }

    //可借售书籍
    @Transactional
    @RequestMapping(value = "/api/userBook/tradeAble", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String tradeAble(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserBookRequest userBookRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookRequest.getToken());
        String keyword = userBookRequest.getKeyword() != null ? userBookRequest.getKeyword() : "";
        List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
        List objectArray = userBookDao.findTradeAbleBooks(userToken.getUserId(), keyword, new PageRequest(userBookRequest.getPage(), Constant.PAGE_NUM_LARGE));
        for (int index = 0; index < objectArray.size(); index++) {
            UserBook userBook = (UserBook) objectArray.get(index);
            userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
        }
        responseContent.putData("userBookList", userBookInfoResponseList);
        return JSON.toJSONString(responseContent);
    }
}