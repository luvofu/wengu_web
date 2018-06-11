package com.wg.admin.webservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.admin.domain.Admin;
import com.wg.admin.model.request.AdminRequest;
import com.wg.admin.model.request.BackendRequest;
import com.wg.admin.model.request.BookCheckQueryRequest;
import com.wg.admin.model.response.BookCheckQueryResponse;
import com.wg.admin.model.response.FeedbackQueryResponse;
import com.wg.admin.utils.VersionUtils;
import com.wg.book.domain.Book;
import com.wg.book.utils.BookUtils;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.bookgroup.utils.BookGroupUtils;
import com.wg.common.Constant;
import com.wg.common.Enum.book.KeywordSearchType;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.common.SourceType;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.Enum.userbook.CheckStatus;
import com.wg.common.FileConfig;
import com.wg.common.PropConfig;
import com.wg.common.ResponseContent;
import com.wg.common.utils.HomeUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.news.domain.TempNews;
import com.wg.news.utils.NewsUtils;
import com.wg.user.domain.Feedback;
import com.wg.user.domain.UserInfo;
import com.wg.userbook.domain.BookCheck;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-11-6
 * Time: 上午10:42
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class AdminController {

    @Transactional
    @RequestMapping(value = "/api/backend/bookEntry", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookEntry(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        String keyword = backendRequest.getKeyword();
        boolean getOk = false;
        if (StringUtils.isNotBlank(keyword)) {
            if (!BookUtils.isIsbn(keyword)) {
                BookUtils.updateBookSearchKeyword(keyword, 0, KeywordSearchType.Backend.getType());
                if (DaoUtils.bookKeywordDao.findByKeyword(keyword) != null) {
                    getOk = true;
                }
            } else {
                if (BookUtils.getBookByIsbn(keyword) != null) {
                    getOk = true;
                }
            }
            if (getOk) {
            } else {
                responseContent.update(ResponseCode.BOOK_ENTRY_FAILED);
            }
        } else {
            responseContent.update(ResponseCode.NULL_KEYWORD);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/backend/bookEntryByBook", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookEntryByBook(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        Book book = new Book();
        book.setTitle(backendRequest.getTitle());
        book.setSubTitle(backendRequest.getSubTitle());
        book.setOriginTitle(backendRequest.getOriginTitle());
        book.setAuthor(backendRequest.getAuthor());
        book.setThirdRating(backendRequest.getThirdRating());
        book.setTag(backendRequest.getTag());
        book.setSummary(backendRequest.getSummary());
        book.setPages(backendRequest.getPrice());
        book.setCover(backendRequest.getCover());
        book.setCatalog(backendRequest.getCatalog());
        book.setPages(backendRequest.getPages());
        book.setAuthorInfo(backendRequest.getAuthorInfo());
        book.setTranslator(backendRequest.getTranslator());
        book.setPublisher(backendRequest.getPublisher());
        book.setPubDate(backendRequest.getPubDate());
        book.setIsbn10(backendRequest.getIsbn10());
        book.setIsbn13(backendRequest.getIsbn13());
        book.setBinding(backendRequest.getBinding());
        book.setSource(com.wg.common.Enum.book.SourceType.WenYa.getType());
        book = AddUtils.addBook(book);
        if (book != null) {
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/admin/redirectBookCommunity", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String redirectBookCommunity(HttpServletRequest request,
                                        HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        // create bookgroup
        Book book = bookDao.findOne(backendRequest.getBookId());
        BookGroup bookGroup = bookGroupDao.findOne(backendRequest.getCommunityId());
        if (book != null) {
            BookGroupUtils.redirectBookCommunity(book, bookGroup);
        } else {
            responseContent.update(ResponseCode.BOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/admin/updateHome", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String updateHome(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        logger.info("[" + TimeUtils.getCurrentDate() + "] update home task start");
        Date startDate = new Date(TimeUtils.getCurrTimestamp() - 10000);
        HomeUtils.updateHomeData();
        HomeUtils.deleteHomeData(startDate);
//        HomeUtils.updateHotBookByHome();
//        HomeUtils.deleteOldHotBook(SourceType.Home.getType(), startDate);
        HomeUtils.updateHotBookGet();
        logger.info("[" + TimeUtils.getCurrentDate() + "] update home task end");
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/backend/updateHotBookFromDouBan", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String updateHotBookByDouBan(HttpServletRequest request,
                                        HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        HomeUtils.updateHotBookByDouBan(backendRequest.getIsbns());
//        HomeUtils.updateHotBookGet();
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/backend/deleteHotBookFromDouBan", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String deleteHotBookFromDouBan(HttpServletRequest request,
                                          HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        HomeUtils.deleteOldHotBook(SourceType.DouBan.getType(), new Date(backendRequest.getDate()));
        HomeUtils.updateHotBookGet();
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/admin/upgrade", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String upgrade(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        //msg push platform
        VersionUtils.version203();
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/admin/add", method = RequestMethod.GET)
    public String addPage(HttpServletRequest request,
                          HttpServletResponse response) {
        return "/admin/add";
    }

    @Transactional
    @RequestMapping(value = "/api/admin/add", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String add(HttpServletRequest request,
                      HttpServletResponse response, AdminRequest adminRequest) {
        ResponseContent responseContent = new ResponseContent();
        Admin admin = adminDao.findByUsernameAndPassword(adminRequest.getUsername(), Utils.MD5(adminRequest.getPassword()));
        if (admin == null) {
            admin = new Admin();
            admin.setUsername(adminRequest.getUsername());
            admin.setPassword(Utils.MD5(adminRequest.getPassword()));
            admin.setRoleType(adminRequest.getRoleType());
            adminDao.save(admin);
        } else {
            responseContent.update(ResponseCode.EXIST_ACC);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/admin/login", method = RequestMethod.GET)
    public String loginPage(HttpServletRequest request,
                            HttpServletResponse response) {
        return "/admin/login";
    }

    @Transactional
    @RequestMapping(value = "/api/admin/home", method = RequestMethod.GET)
    public String home(HttpServletRequest request,
                       HttpServletResponse response) {
        return "/admin/home";
    }

    @Transactional
    @RequestMapping(value = "/api/admin/login", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest request,
                        HttpServletResponse response, AdminRequest adminRequest, ModelMap modelMap) {
        ResponseContent responseContent = new ResponseContent();
        String username = adminRequest.getUsername();
        String password = adminRequest.getPassword();
        if (username != null && password != null) {
            Admin admin = adminDao.findByUsernameAndPassword(username, Utils.MD5(password));
            if (admin == null) {
                responseContent.update(ResponseCode.ERROR_LOGIN);
            } else {
                HttpSession httpSession = request.getSession();
                httpSession.setAttribute("admin", admin);
            }
        } else {
            responseContent.update(ResponseCode.NOT_LOGIN);
        }
        return JSON.toJSONString(responseContent);
    }


    @Transactional
    @RequestMapping(value = "/api/admin/bookCheck", method = RequestMethod.GET)
    public String bookCheckView(HttpServletRequest request,
                                HttpServletResponse response) {
        return "/bookCheck/view";
    }

    @Transactional
    @RequestMapping(value = "/api/admin/bookCheck/query", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String bookCheckQuery(HttpServletRequest request,
                                 HttpServletResponse response, BookCheckQueryRequest bookCheckQueryRequest) {
        ResponseContent responseContent = new ResponseContent();
        Date startDate = TimeUtils.parseDate(bookCheckQueryRequest.getStartTime(), "MM/dd/yyyy");
        Date endDate = TimeUtils.parseDate(bookCheckQueryRequest.getEndTime(), "MM/dd/yyyy");
        if (startDate == null) startDate = TimeUtils.getDate(0L);
        if (endDate == null || endDate.before(startDate)) endDate = TimeUtils.getCurrentDate();
        endDate = TimeUtils.getModifyDate(endDate, 1, null, null, null);
        int checkStatus = bookCheckQueryRequest.getCheckStatus();
        List<BookCheckQueryResponse> bookCheckQueryResponseList = new ArrayList<BookCheckQueryResponse>();
        Page<BookCheck> bookCheckPage = bookCheckDao.findByCheckStatusAndCreatedTimeBetweenOrderByCreatedTimeDesc(
                checkStatus, startDate, endDate, new PageRequest(bookCheckQueryRequest.getPage(), Constant.PAGE_NUM_MEDIUM));
        for (BookCheck bookCheck : bookCheckPage.getContent()) {
            UserInfo userInfo = userInfoDao.findOne(bookCheck.getUserId());
            bookCheckQueryResponseList.add(new BookCheckQueryResponse(bookCheck, userInfo.getNickname()));
        }
        responseContent.putData("bookCheckQueryResponseList", bookCheckQueryResponseList);
        responseContent.putData("total", bookCheckPage.getTotalElements());
        return JSON.toJSONString(responseContent);
    }


    @Transactional
    @RequestMapping(value = "/api/admin/bookCheck/detailPage", method = RequestMethod.GET)
    public String bookCheckDetailPage(HttpServletRequest request,
                                      HttpServletResponse response, BookCheckQueryRequest bookCheckQueryRequest, ModelMap modelMap) {
        BookCheck bookCheck = bookCheckDao.findOne(bookCheckQueryRequest.getBookCheckId());
        if (bookCheck != null) {
            UserInfo userInfo = userInfoDao.findOne(bookCheck.getUserId());
            BookCheckQueryResponse bookCheckQueryResponse = new BookCheckQueryResponse(bookCheck, userInfo.getNickname());
            modelMap.addAttribute("bookCheckQueryResponse", bookCheckQueryResponse);
        }
        return "/bookCheck/detail";
    }

    @Transactional
    @RequestMapping(value = "/api/admin/bookCheck/detail", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String bookCheckDetail(HttpServletRequest request,
                                  HttpServletResponse response, final BookCheckQueryRequest bookCheckQueryRequest) {
        ResponseContent responseContent = new ResponseContent();
        BookCheck bookCheck = bookCheckDao.findOne(bookCheckQueryRequest.getBookCheckId());
        if (bookCheck != null) {
            UserInfo userInfo = userInfoDao.findOne(bookCheck.getUserId());
            BookCheckQueryResponse bookCheckQueryResponse = new BookCheckQueryResponse(bookCheck, userInfo.getNickname());
            responseContent.putData("bookCheckQueryResponse", bookCheckQueryResponse);
        } else {
            responseContent.update(ResponseCode.BOOKCHECK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/admin/bookCheck/update", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String bookCheckUpdate(HttpServletRequest request,
                                  HttpServletResponse response, final BookCheckQueryRequest bookCheckQueryRequest) {
        ResponseContent responseContent = new ResponseContent();
        BookCheck bookCheck = bookCheckDao.findOne(bookCheckQueryRequest.getBookCheckId());
        if (bookCheck != null) {
            if (bookCheck.getCheckStatus() != CheckStatus.Pass.getStatus()) {
                if (bookCheckQueryRequest.getCheckStatus() == CheckStatus.Pass.getStatus()) {
                    bookCheck.setBookId(BookUtils.bookCheckPass(bookCheck).getBookId());
                    bookCheck.setCheckStatus(bookCheckQueryRequest.getCheckStatus());
                    bookCheck.setCheckInfo(bookCheckQueryRequest.getCheckInfo());
                    bookCheck = bookCheckDao.save(bookCheck);
                } else if (bookCheckQueryRequest.getCheckStatus() == CheckStatus.NotPass.getStatus()) {
                    bookCheck.setCheckStatus(bookCheckQueryRequest.getCheckStatus());
                    bookCheck.setCheckInfo(bookCheckQueryRequest.getCheckInfo());
                    bookCheck = bookCheckDao.save(bookCheck);
                }
                AddUtils.addUserMessage(
                        bookCheck.getUserId(),
                        PropConfig.OFFICER_USERID,
                        null,
                        MessageType.BookCheck.getType(),
                        bookCheck.getBookCheckId(),
                        com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            } else {
                responseContent.update(ResponseCode.ERROR_PARAM);
            }
        } else {
            responseContent.update(ResponseCode.BOOKCHECK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/admin/bookCommunity/merge", method = RequestMethod.GET)
    public String mergePage(HttpServletRequest request,
                            HttpServletResponse response, final BookCheckQueryRequest bookCheckQueryRequest) {
        return "/bookCommunity/merge";
    }

    @Transactional
    @RequestMapping(value = "/api/admin/book/notBookCommunity", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String bookCheckUpdate(HttpServletRequest request,
                                  HttpServletResponse response, @RequestParam(defaultValue = "0") int page) {
        ResponseContent responseContent = new ResponseContent();
        if (page >= 0) {
            Pageable pageable = new PageRequest(page, 10);
            Slice<Book> bookSlice = bookDao.findByCommunityId(0, pageable);
            responseContent.putData("bookList", bookSlice.getContent());
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/admin/book/bookCommunity", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String getBookCommunity(HttpServletRequest request,
                                   HttpServletResponse respons, String title) {
        ResponseContent responseContent = new ResponseContent();
        if (StringUtils.isNotBlank(title)) {
            responseContent.putData("bookCommunityList", bookGroupDao.findByTitle(title));
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/admin/bookCommunity/update", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String getBookCommunity(HttpServletRequest request,
                                   HttpServletResponse respons, long bookCommunityId, String title, String author) {
        ResponseContent responseContent = new ResponseContent();
        BookGroup bookGroup = bookGroupDao.findOne(bookCommunityId);
        if (bookGroup != null) {
            if (StringUtils.isNotBlank(title)) {
                bookGroup.setTitle(title);
            }
            if (StringUtils.isNotBlank(author)) {
                bookGroup.setAuthor(author);
            }
            bookGroup = UpdateUtils.updateBookCommunity(bookGroup);
        } else {
            responseContent.update(ResponseCode.BOOKCOMMUNITY_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/admin/feedback/query", method = RequestMethod.GET)
    public String feedbackPage(HttpServletRequest request,
                               HttpServletResponse response) {
        return "/feedback/view";
    }

    @Transactional
    @RequestMapping(value = "/api/admin/feedback/query", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String feedbackQuery(HttpServletRequest request,
                                HttpServletResponse response, final BookCheckQueryRequest bookCheckQueryRequest) {
        ResponseContent responseContent = new ResponseContent();
        List<FeedbackQueryResponse> feedbackQueryResponseList = new ArrayList<FeedbackQueryResponse>();
        Pageable pageable = new PageRequest(bookCheckQueryRequest.getPage(), 10, new Sort(Sort.Direction.DESC, "createdTime"));
        Page<Feedback> feedbackPage = feedbackDao.findAll(pageable);
        for (Feedback feedback : feedbackPage.getContent()) {
            UserInfo userInfo = userInfoDao.findOne(feedback.getUserId());
            feedbackQueryResponseList.add(new FeedbackQueryResponse(feedback, userInfo));
        }
        responseContent.putData("feedbackQueryResponseList", feedbackQueryResponseList);
        responseContent.putData("total", feedbackPage.getTotalElements());
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/admin/updateConfigFile", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String updateConfigFile(HttpServletRequest request,
                                   HttpServletResponse response, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        FileConfig.updateConfigFile();
        responseContent.putData("address", FileConfig.address);
        responseContent.putData("bookClass", FileConfig.map);
        responseContent.putData("bookTagClass", FileConfig.bookTagList);
        responseContent.putData("booksheetTagClass", FileConfig.bookSheetTagList);
        responseContent.putData("newsKewords", NewsUtils.newskeyword);
        responseContent.putData("titleIncludeWords", NewsUtils.titleIncludeWords);
        responseContent.putData("sourceExcludeKewords", NewsUtils.srcExcludeKeyword);
        responseContent.putData("updatedTime", TimeUtils.getDate(FileConfig.updatedTime).toString());
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/admin/updateNews", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String updateNews(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        NewsUtils.updateNews();
        logger.info("[" + TimeUtils.getCurrentDate() + "] update news end");
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/admin/addNews", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String addNews(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        String newsJsons = backendRequest.getNewsList();
        if (StringUtils.isNotBlank(newsJsons)) {
            JSONObject jsonObject = JSON.parseObject(newsJsons);
            JSONArray jsonArray = jsonObject.getJSONArray("contentlist");
            for (Object newsJson : jsonArray) {
                String title = ((JSONObject) newsJson).getString("title").trim();
                String ukey = Utils.MD5(title);//md5 title for ukey
                if (DaoUtils.tempNewsDao.findByUniqueKey(ukey) == null) { // db not have
                    TempNews tempNews = new TempNews();
                    tempNews.setUniqueKey(ukey);
                    tempNews.setNewsJson(JSON.toJSONString(newsJson));
                    tempNews = DaoUtils.tempNewsDao.save(tempNews);
                }
            }
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "api/admin/delUser", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delUser(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        ResponseContent responseContent = new ResponseContent();
        long userId = backendRequest.getUserId();
        if (userId != -1) {
            DeleteUtils.deleteUser(userId);
        }
        return JSON.toJSONString(responseContent);
    }

    //app二维码下载
    @Transactional
    @RequestMapping(value = "/p/qr", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    public String qr(HttpServletRequest request,
                     HttpServletResponse response, ModelMap modelMap, BackendRequest backendRequest) {
        String  agent = request.getHeader("User-Agent");
//        logger.info(agent);
        if(agent.toLowerCase().indexOf("mac os") != -1) {
            return "redirect:https://itunes.apple.com/cn/app/id1170983858";
        } else {
            return "redirect:/down/apk/wenya.apk";
        }
    }
}
