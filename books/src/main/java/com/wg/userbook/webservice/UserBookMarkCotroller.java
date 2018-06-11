package com.wg.userbook.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.common.Constant;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.userbook.ReadStatus;
import com.wg.common.ResponseContent;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.user.domain.UserToken;
import com.wg.userbook.domain.UserBook;
import com.wg.userbook.domain.UserBookmark;
import com.wg.userbook.model.request.UserBookMarkRequest;
import com.wg.userbook.model.response.UserBookInfoResponse;
import com.wg.userbook.model.response.UserBookMarkPersonalResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
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
 * Created by Administrator on 2016/9/7.
 */
@Controller
public class UserBookMarkCotroller {

    @Transactional
    @RequestMapping(value = "/api/bookmark/personal", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String personal(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, UserBookMarkRequest userBookMarkRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookMarkRequest.getToken());
        List<UserBook> userBookList = userBookDao.findByUserIdOrderByUpdatedTimeDesc(userToken.getUserId());
        List<UserBookMarkPersonalResponse> userBookMarkPersonalResponseList = new ArrayList<UserBookMarkPersonalResponse>();
        for (UserBook userBook : userBookList) {
            UserBookmark userBookmark = userBookmarkDao.findByUserBookId(userBook.getUserBookId());
            if (userBookmark != null) {
                userBookMarkPersonalResponseList.add(new UserBookMarkPersonalResponse(userBookmark));
            }
        }
        responseContent.putData("bookmarkList", userBookMarkPersonalResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookmark/add", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String add(HttpServletRequest request,
                      HttpServletResponse response, ModelMap modelMap, UserBookMarkRequest userBookMarkRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookMarkRequest.getToken());
        UserBook userBook = userBookDao.findOne(userBookMarkRequest.getUserBookId());
        int pages = userBookMarkRequest.getPages();
        if (0 < pages) {
            if (userBook != null) {
                if (userBook.getUserId() == userToken.getUserId()) {
                    UserBookmark userBookmark = userBookmarkDao.findByUserBookId(userBook.getUserBookId());
                    if (userBookmark == null) {
                        int totalPage = Utils.getNumber(userBook.getBook().getPages());
                        AddUtils.addUserBookmark(userBook, pages, totalPage > pages ? totalPage : pages);
                    } else {
                        responseContent.update(ResponseCode.BOOKMARK_ALREADY_EXIST);
                    }
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.USERBOOK_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookmark/edit", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String edit(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, UserBookMarkRequest userBookMarkRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserBookmark userBookmark = userBookmarkDao.findOne(userBookMarkRequest.getBookmarkId());
        if (userBookMarkRequest.getPages() > 0) {
            if (userBookmark != null) {
                userBookmark.setPages(userBookMarkRequest.getPages());
                if (userBookmark.getTotalPage() < userBookmark.getPages()) {
                    userBookmark.setTotalPage(userBookmark.getPages());
                }
                userBookmark = userBookmarkDao.save(userBookmark);
            } else {
                responseContent.update(ResponseCode.BOOKMARK_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookmark/delete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, UserBookMarkRequest userBookMarkRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookMarkRequest.getToken());
        UserBookmark userBookmark = userBookmarkDao.findOne(userBookMarkRequest.getBookmarkId());
        if (userBookmark != null) {
            UserBook userBook = userBookDao.findOne(userBookmark.getUserBookId());
            if (userBook.getUserId() == userToken.getUserId()) {
                //set userbook read status
                if (userBook != null) {
                    userBook.setReadStatus(ReadStatus.Finish.getType());
                    userBook = UpdateUtils.updateUserBook(userBook);
                    userBookmarkDao.delete(userBookmark);
                } else {
                    responseContent.update(ResponseCode.USERBOOK_NOT_EXIST);
                }
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.BOOKMARK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    /**
     * version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103 ** version 103
     **/

    @Transactional
    @RequestMapping(value = "/api/bookmark/add_v103", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String add_v103(HttpServletRequest request,
                           HttpServletResponse response, ModelMap modelMap, UserBookMarkRequest userBookMarkRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookMarkRequest.getToken());
        UserBook userBook = userBookDao.findOne(userBookMarkRequest.getUserBookId());
        int pages = userBookMarkRequest.getPages();
        int totalPage = userBookMarkRequest.getTotalPage();
        if (pages <= totalPage && pages > 0) {
            if (userBook != null) {
                if (userBook.getUserId() == userToken.getUserId()) {
                    if (userBookmarkDao.findByUserBookId(userBook.getUserBookId()) == null) {
                        AddUtils.addUserBookmark(userBook, pages, totalPage);
                    } else {
                        responseContent.update(ResponseCode.BOOKMARK_ALREADY_EXIST);
                    }
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.USERBOOK_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookmark/edit_v103", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String edit_v103(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, UserBookMarkRequest userBookMarkRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserBookmark userBookmark = userBookmarkDao.findOne(userBookMarkRequest.getBookmarkId());
        int pages = userBookMarkRequest.getPages();
        int totalPage = userBookMarkRequest.getTotalPage();
        if (0 < pages && pages <= totalPage) {
            if (userBookmark != null) {
                userBookmark.setPages(pages);
                userBookmark.setTotalPage(totalPage);
                userBookmark = userBookmarkDao.save(userBookmark);
            } else {
                responseContent.update(ResponseCode.BOOKMARK_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookmark/personal_v103", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String personal_v103(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap, UserBookMarkRequest userBookMarkRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookMarkRequest.getToken());
        List<UserBookmark> userBookmarkList = userBookmarkDao.findByUserIdOrderByUpdatedTimeDesc(userToken.getUserId());
        List<UserBookMarkPersonalResponse> userBookMarkPersonalResponseList = new ArrayList<UserBookMarkPersonalResponse>();
        for (UserBookmark userBookmark : userBookmarkList) {
            userBookMarkPersonalResponseList.add(new UserBookMarkPersonalResponse(userBookmark));
        }
        responseContent.putData("bookmarkList", userBookMarkPersonalResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookmark/markAbleBooks", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String markAbleBooks(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, UserBookMarkRequest userBookMarkRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(userBookMarkRequest.getToken());
        String keyword = StringUtils.isNotBlank(userBookMarkRequest.getKeyword()) ? userBookMarkRequest.getKeyword() : "";
        List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
        List<UserBook> userBookList = userBookDao.findAddmarkUserBooks(
                userToken.getUserId(), keyword, new PageRequest(userBookMarkRequest.getPage(), Constant.PAGE_NUM_LARGE));
        for (UserBook userBook : userBookList) {
            userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
        }
        responseContent.putData("userBookList", userBookInfoResponseList);
        return JSON.toJSONString(responseContent);
    }

}
