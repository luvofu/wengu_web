package com.wg.bookgroup.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.book.domain.Book;
import com.wg.book.model.response.BookEntityResponse;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.bookgroup.model.request.BookCommunityRequest;
import com.wg.bookgroup.model.response.BookCommunityCommentResponse;
import com.wg.bookgroup.model.response.BookCommunityDetailResponse;
import com.wg.bookgroup.model.response.BookCommunitySearchResponse;
import com.wg.common.Constant;
import com.wg.common.Enum.bookgroup.SortType;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.ResponseContent;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.solr.utils.BookGroupSolr;
import com.wg.solr.modle.QueryRes;
import com.wg.user.domain.UserToken;
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
 * Created by wzhonggo on 8/31/2016.
 */
@Controller
public class BookGroupController {

    @Transactional
    @RequestMapping(value = "/api/bookCommunity/search", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String search(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, BookCommunityRequest bookCommunityRequest) {
        ResponseContent responseContent = new ResponseContent();
        String keyword = bookCommunityRequest.getKeyword();
        if (keyword != null) {
            List<BookCommunitySearchResponse> bookCommunitySearchResponseList = new ArrayList<BookCommunitySearchResponse>();
            QueryRes<BookGroup> queryRes = BookGroupSolr.findByTitleContains(keyword, bookCommunityRequest.getPage());
            for (BookGroup bookGroup : queryRes.getDocList()) {
                bookCommunitySearchResponseList.add(new BookCommunitySearchResponse(bookGroup));
            }
            responseContent.putData("bookCommunityList", bookCommunitySearchResponseList);
            responseContent.putData("total", queryRes.getTotalRes());
        } else {
            responseContent.update(ResponseCode.NULL_KEYWORD);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookCommunity/book", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String book(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, BookCommunityRequest bookCommunityRequest) {
        ResponseContent responseContent = new ResponseContent();
        long communityId = bookCommunityRequest.getCommunityId();
        int page = bookCommunityRequest.getPage();
        if (communityId != -1 && page != -1) {
            Pageable pageable = new PageRequest(bookCommunityRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<Book> bookSlice = bookDao.findByCommunityIdOrderByEntryNum(communityId, pageable);
            List<BookEntityResponse> bookEntityResponseList = new ArrayList<BookEntityResponse>();
            for (Book book : bookSlice.getContent()) {
                bookEntityResponseList.add(new BookEntityResponse(book));
            }
            responseContent.putData("bookList", bookEntityResponseList);
        } else {
            responseContent.update(ResponseCode.BOOKCOMMUNITY_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookCommunity/detail", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String detail(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, BookCommunityRequest bookCommunityRequest) {
        ResponseContent responseContent = new ResponseContent();
        long communityId = bookCommunityRequest.getCommunityId();
        BookGroup bookGroup = bookGroupDao.findOne(communityId);
        if (bookGroup != null) {
            BookCommunityDetailResponse bookCommunityDetailResponse = new BookCommunityDetailResponse(bookGroup);
            responseContent.setData(bookCommunityDetailResponse);
        } else {
            responseContent.update(ResponseCode.BOOKCOMMUNITY_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/bookCommunity/comment", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookCommunityComment(HttpServletRequest request,
                                       HttpServletResponse response, ModelMap modelMap, BookCommunityRequest bookCommunityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = null;
        if (bookCommunityRequest.getToken() != null) {
            userToken = userTokenDao.findByToken(bookCommunityRequest.getToken());
        }
        if (bookCommunityRequest.getCommunityId() != -1) {
            Pageable pageable = new PageRequest(bookCommunityRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<GroupComment> communityCommentSlice;
            if (bookCommunityRequest.getSortType() == SortType.ReplyNum.getType()) {
                communityCommentSlice = groupCommentDao.findByCommunityIdOrderByReplyNumDesc(bookCommunityRequest.getCommunityId(), pageable);
            } else {
                communityCommentSlice = groupCommentDao.findByCommunityIdOrderByCreatedTimeDesc(bookCommunityRequest.getCommunityId(), pageable);
            }
            List<BookCommunityCommentResponse> bookCommunityCommentResponseList = new ArrayList<BookCommunityCommentResponse>();
            for (GroupComment groupComment : communityCommentSlice.getContent()) {
                bookCommunityCommentResponseList.add(new BookCommunityCommentResponse(groupComment, userToken));
            }
            responseContent.putData("commentList", bookCommunityCommentResponseList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/bookCommunity/addComment", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookCommunityAdd(HttpServletRequest request,
                                   HttpServletResponse response, ModelMap modelMap, BookCommunityRequest bookCommunityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookCommunityRequest.getToken());
        BookGroup bookGroup = bookGroupDao.findOne(bookCommunityRequest.getCommunityId());
        if (bookGroup != null) {
            AddUtils.addCommunityComment(bookGroup, userToken.getUserId(), bookCommunityRequest.getContent());
        } else {
            responseContent.update(ResponseCode.BOOKCOMMUNITY_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }
}

