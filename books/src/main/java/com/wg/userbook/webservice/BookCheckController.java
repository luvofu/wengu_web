package com.wg.userbook.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.book.utils.BookUtils;
import com.wg.common.Constant;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.userbook.CheckStatus;
import com.wg.common.ResponseContent;
import com.wg.common.utils.ImageUtils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.user.domain.UserToken;
import com.wg.userbook.domain.BookCheck;
import com.wg.userbook.model.request.BookCheckRequest;
import com.wg.userbook.model.response.BookCheckPersonalResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
 * Created by wzhonggo on 8/30/2016.
 */
@Controller
public class BookCheckController {

    @Transactional
    @RequestMapping(value = "/api/bookCheck/entry", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String entry(HttpServletRequest request,
                        HttpServletResponse response, ModelMap modelMap, BookCheckRequest bookCheckRequest, @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookCheckRequest.getToken());
        String title = bookCheckRequest.getTitle();
        String author = bookCheckRequest.getAuthor();
        String isbn10 = bookCheckRequest.getIsbn10();
        String isbn13 = bookCheckRequest.getIsbn();
        boolean isbn10NotBlank = StringUtils.isNotBlank(isbn10);
        boolean isbn13NotBlack = StringUtils.isNotBlank(isbn13);
        if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(author) && file != null && (isbn10NotBlank || isbn13NotBlack)) {
            if (!isbn13NotBlack || BookUtils.isIsbn(isbn13)) {
                if (!isbn13NotBlack || BookUtils.uniqueIsbn(isbn13)) {
                    AddUtils.addBookCheck(userToken.getUserId(),
                            title.trim(), bookCheckRequest.getSubTitle(), bookCheckRequest.getOriginTitle(),
                            author.trim(), bookCheckRequest.getTranslator(), bookCheckRequest.getPrice(),
                            bookCheckRequest.getPublisher(), bookCheckRequest.getPubDate(), bookCheckRequest.getBinding(),
                            bookCheckRequest.getPages(), bookCheckRequest.getSummary(), bookCheckRequest.getAuthorInfo(),
                            isbn10, isbn13, file);
                } else {
                    responseContent.update(ResponseCode.ISBN_ALREADY_EXIST);
                }
            } else {
                responseContent.update(ResponseCode.ERROR_ISBN);
            }
        } else {
            responseContent.update(ResponseCode.NOT_ENOUGH_BOOK_INFO);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookCheck/personal", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String personal(HttpServletRequest request,
                        HttpServletResponse response, ModelMap modelMap, BookCheckRequest bookCheckRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookCheckRequest.getToken());
        long userId = userToken.getUserId();
        if (userInfoDao.findOne(userId) != null) {
            Pageable pageable = new PageRequest(bookCheckRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<BookCheck> bookCheckSlice = bookCheckDao.findByUserIdOrderByCreatedTimeDesc(userId, pageable);
            List<BookCheckPersonalResponse> bookCheckPersonalResponseList = new ArrayList<BookCheckPersonalResponse>();
            for (BookCheck bookCheck : bookCheckSlice.getContent()) {
                bookCheckPersonalResponseList.add(new BookCheckPersonalResponse(bookCheck));
            }
            responseContent.putData("bookCheckList", bookCheckPersonalResponseList);
        } else {
            responseContent.update(ResponseCode.USER_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookCheck/edit", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String edit(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, BookCheckRequest bookCheckRequest, @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookCheckRequest.getToken());
        BookCheck bookCheck = bookCheckDao.findOne(bookCheckRequest.getBookCheckId());
        String isbn13 = bookCheckRequest.getIsbn();
        String isbn10 = bookCheckRequest.getIsbn10();
        if (bookCheck != null) {
            if (bookCheck.getCheckStatus() != CheckStatus.Pass.getStatus()) {
                if (userToken.getUserId() == bookCheck.getUserId()) {
                    boolean isbnOk = true;
                    if (isbn13 != null) {
                        if (BookUtils.isIsbn(isbn13)) {
                            if (BookUtils.uniqueIsbn(isbn13)) {
                                bookCheck.setIsbn13(isbn13);
                            } else {
                                responseContent.update(ResponseCode.ISBN_ALREADY_EXIST);
                                isbnOk = false;
                            }
                        } else {
                            responseContent.update(ResponseCode.ERROR_ISBN);
                            isbnOk = false;
                        }
                    }
                    if (isbn10 != null) {
                        if (BookUtils.uniqueIsbn(isbn10)) {
                            bookCheck.setIsbn10(isbn10);
                        } else {
                            responseContent.update(ResponseCode.SH_OR_ISBN_EXIST);
                            isbnOk = false;
                        }
                    }
                    if (isbnOk) {
                        if (bookCheckRequest.getTitle() != null)
                            bookCheck.setTitle(bookCheckRequest.getTitle());
                        if (bookCheckRequest.getSubTitle() != null)
                            bookCheck.setSubTitle(bookCheckRequest.getSubTitle());
                        if (bookCheckRequest.getOriginTitle() != null)
                            bookCheck.setOriginTitle(bookCheckRequest.getOriginTitle());
                        if (bookCheckRequest.getAuthor() != null)
                            bookCheck.setAuthor(bookCheckRequest.getAuthor());
                        if (bookCheckRequest.getTranslator() != null)
                            bookCheck.setTranslator(bookCheckRequest.getTranslator());
                        if (bookCheckRequest.getPrice() != null)
                            bookCheck.setPrice(bookCheckRequest.getPrice());
                        if (bookCheckRequest.getPublisher() != null)
                            bookCheck.setPublisher(bookCheckRequest.getPublisher());
                        if (bookCheckRequest.getPubDate() != null)
                            bookCheck.setPubDate(bookCheckRequest.getPubDate());
                        if (bookCheckRequest.getBinding() != null)
                            bookCheck.setBinding(bookCheckRequest.getBinding());
                        if (bookCheckRequest.getPages() != null)
                            bookCheck.setPages(bookCheckRequest.getPages());
                        if (bookCheckRequest.getSummary() != null)
                            bookCheck.setSummary(bookCheckRequest.getSummary());
                        if (bookCheckRequest.getAuthorInfo() != null)
                            bookCheck.setAuthorInfo(bookCheckRequest.getAuthorInfo());
                        if (file != null) {
                            if (ImageUtils.deleteImage(bookCheck.getCover())) {
                                bookCheck.setCover(ImageUtils.saveImage(Constant.BOOKCHECK_COVER_FOLDER, bookCheck.getBookCheckId(), file));
                            }
                        }
                        bookCheck.setCheckStatus(CheckStatus.InCheck.getStatus());
                        bookCheck = bookCheckDao.save(bookCheck);
                    }
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.ERROR_PARAM);
            }
        } else {
            responseContent.update(ResponseCode.BOOKCHECK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookCheck/delete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, BookCheckRequest bookCheckRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookCheckRequest.getToken());
        BookCheck bookCheck = bookCheckDao.findOne(bookCheckRequest.getBookCheckId());
        if (bookCheck != null) {
            if (userToken.getUserId() == bookCheck.getUserId()) {
                if (ImageUtils.deleteImage(bookCheck.getCover())) {
                    bookCheckDao.delete(bookCheck);
                } else {
                    responseContent.update(ResponseCode.DELETE_IMAGE_FAILD);
                }
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.BOOKCHECK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }
}
