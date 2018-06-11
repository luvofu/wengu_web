package com.wg.common.utils.dbutils;

import com.alibaba.fastjson.JSON;
import com.wg.book.domain.Book;
import com.wg.book.domain.BookKeyword;
import com.wg.book.utils.BookUtils;
import com.wg.bookcircle.domain.BookCircleDynamic;
import com.wg.bookcircle.domain.BookCircleReply;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.bookgroup.domain.GroupReply;
import com.wg.bookgroup.utils.BookGroupUtils;
import com.wg.bookorder.domain.BookOrder;
import com.wg.bookorder.domain.OrderEvaluate;
import com.wg.bookorder.utils.BookOrderUtils;
import com.wg.booksheet.domain.BookSheet;
import com.wg.booksheet.domain.BookSheetBook;
import com.wg.common.Constant;
import com.wg.common.Enum.bookgroup.ReplyType;
import com.wg.common.Enum.bookorder.FinishType;
import com.wg.common.Enum.bookorder.OrderStatus;
import com.wg.common.Enum.bookorder.OrderType;
import com.wg.common.Enum.common.CollectType;
import com.wg.common.Enum.common.GoodType;
import com.wg.common.Enum.common.Permission;
import com.wg.common.Enum.community.MemberType;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.Enum.message.SMsgTemplate;
import com.wg.common.Enum.solr.SolrType;
import com.wg.common.Enum.useraccount.BillType;
import com.wg.common.Enum.useraccount.DealStatus;
import com.wg.common.Enum.useraccount.PayType;
import com.wg.common.Enum.userbook.EntryType;
import com.wg.common.PropConfig;
import com.wg.common.domain.Home;
import com.wg.common.utils.DecimalUtils;
import com.wg.common.utils.ImageUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.community.domain.CommBook;
import com.wg.community.domain.CommMember;
import com.wg.community.domain.Community;
import com.wg.message.domain.UserMessage;
import com.wg.message.utils.EmailUtils;
import com.wg.message.utils.IMUtils;
import com.wg.message.utils.SMsgUtils;
import com.wg.message.utils.UserMsgUtils;
import com.wg.news.domain.News;
import com.wg.notebook.domain.Note;
import com.wg.notebook.domain.Notebook;
import com.wg.notebook.domain.Storyline;
import com.wg.picword.domain.Picword;
import com.wg.solr.domain.TempSolrIndex;
import com.wg.user.domain.*;
import com.wg.user.utils.ValidUtils;
import com.wg.useraccount.domain.ThirdPay;
import com.wg.useraccount.domain.UserAccount;
import com.wg.useraccount.domain.UserBill;
import com.wg.useraccount.utils.BillSignUtils;
import com.wg.useraccount.utils.UserAccUtils;
import com.wg.userbook.domain.BookCheck;
import com.wg.userbook.domain.UserBook;
import com.wg.userbook.domain.UserBookmark;
import com.wg.userbook.domain.UserCategory;
import com.wg.userbook.utils.UserBookUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/11/3 0003.
 */

/**
 * 新增工具
 */
public class AddUtils {
    //新增书籍
    public static Book addBook(Book book) {
        String title = book.getTitle();
        String origin_title = book.getOriginTitle();
        String subTitle = book.getSubTitle();
        String author = book.getAuthor();
        float thirdRating = book.getThirdRating();
        String isbn10 = book.getIsbn10();
        String isbn13 = book.getIsbn13();

        if (StringUtils.isBlank(title) || title.length() >= 255) {
            return null;
        }
        if (StringUtils.isNotBlank(subTitle) && (subTitle.equals(title) || subTitle.length() >= 255)) {
            book.setSubTitle(null);
        }
        if (origin_title != null && origin_title.length() >= 255) {
            book.setOriginTitle(null);
        }
        if (StringUtils.isBlank(author) || author.length() >= 255) {
            return null;
        }
        if (StringUtils.isBlank(isbn10) && StringUtils.isBlank(isbn13)) {
            return null;
        }
        if (StringUtils.isNotBlank(isbn10) && bookDao.findByisbn10(isbn10) != null) {
            return null;
        }
        if (StringUtils.isNotBlank(isbn13) && (!BookUtils.isIsbn(isbn13) || bookDao.findByIsbn13(isbn13) != null)) {
            return null;
        }
        if (thirdRating > 0) {
            book.setRating(thirdRating);
            book.setRatingAllNum(BookUtils.DOUBAN_RATING_NUM);
        }
        book = UpdateUtils.updateBook(book);

        BookUtils.newTempBookCover(book);
        BookUtils.setBookGroup(book);
        BookGroupUtils.updateCs(book);

        return book;
    }

    //新增社区
    public static BookGroup addBookCommunity(Book book) {
        BookGroup bookGroup = null;
        if (book != null) {
            bookGroup = new BookGroup();
            bookGroup.setAuthor(book.getAuthor());
            bookGroup.setTitle(BookUtils.titleFilter(book.getTitle()));
            bookGroup.setSubTitle(book.getSubTitle());
            bookGroup = UpdateUtils.updateBookCommunity(bookGroup);
        }
        return bookGroup;
    }

    //新增社区评论
    public static GroupComment addCommunityComment(BookGroup bookGroup, long userId, String content) {
        GroupComment groupComment = null;
        if (bookGroup != null && StringUtils.isNotBlank(content)) {
            groupComment = new GroupComment();
            groupComment.setUserId(userId);
            groupComment.setCommunityId(bookGroup.getCommunityId());
            groupComment.setContent(content);
            groupComment.setGoodNum(0);
            groupComment.setReplyNum(0);
            groupComment = groupCommentDao.save(groupComment);

            bookGroup.setCommentNum(bookGroup.getCommentNum() + 1);
            bookGroup = UpdateUtils.updateBookCommunity(bookGroup);
        }
        return groupComment;
    }

    //新增社区评论回复
    public static GroupReply addCommunityReply(
            GroupComment groupComment,
            long userId,
            String content,
            int replyType,
            long replyObjId) {
        GroupReply groupReply = null;
        long objUserId = groupComment.getUserId();
        if (replyType == ReplyType.Reply.getType()) {
            objUserId = groupReplyDao.findOne(replyObjId).getUserId();
        }
        if (groupComment != null && StringUtils.isNotBlank(content)) {
            new GroupReply();
            groupReply.setUserId(userId);
            groupReply.setCommentId(groupComment.getCommentId());
            groupReply.setReplyType(replyType);
            groupReply.setReplyObjId(replyObjId);
            groupReply.setContent(content);
            groupReply = groupReplyDao.save(groupReply);

            //set comment replynum
            groupComment.setReplyNum(groupComment.getReplyNum() + 1);
            groupComment = groupCommentDao.save(groupComment);

            //消息
            AddUtils.addUserMessage(
                    objUserId,
                    userId,
                    null,
                    MessageType.CommunityReply.getType(),
                    groupReply.getReplyId(),
                    com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
        return groupReply;
    }

    //新增动态
    public static BookCircleDynamic addBookCircleDynamic(long userId,
                                                         String content,
                                                         long linkId,
                                                         int linkType,
                                                         String location,
                                                         MultipartFile file,
                                                         int permission,
                                                         int dynamicType) {
        BookCircleDynamic bookCircleDynamic = new BookCircleDynamic();
        bookCircleDynamic.setUserId(userId);
        bookCircleDynamic.setContent(content);
        bookCircleDynamic.setLinkId(linkId);
        bookCircleDynamic.setLinkType(linkType);
        bookCircleDynamic.setLocation(location);
        bookCircleDynamic.setPermission(permission);
        bookCircleDynamic.setDynamicType(dynamicType);
        bookCircleDynamic = bookCircleDynamicDao.save(bookCircleDynamic);
        if (file != null) {
            String path = ImageUtils.saveImage(Constant.DYNAMIC_CONTENT_FOLDER, bookCircleDynamic.getDynamicId(), file);
            if (path != null) {
                bookCircleDynamic.setImage(path);
                bookCircleDynamic = bookCircleDynamicDao.save(bookCircleDynamic);
            }
        }
        return bookCircleDynamic;
    }

    //新增动态回复
    public static BookCircleReply addBookCircleReply(BookCircleDynamic bookCircleDynamic,
                                                     long userId,
                                                     String content,
                                                     int replyType,
                                                     long replyObjId) {
        BookCircleReply bookCircleReply = null;
        if (bookCircleDynamic != null && StringUtils.isNotBlank(content)) {
            long objUserId = bookCircleDynamic.getUserId();
            if (replyType == com.wg.common.Enum.bookcircle.ReplyType.Reply.getType()) {
                objUserId = bookCircleReplyDao.findOne(replyObjId).getUserId();
            }
            bookCircleReply = new BookCircleReply();
            bookCircleReply.setUserId(userId);
            bookCircleReply.setDynamicId(bookCircleDynamic.getDynamicId());
            bookCircleReply.setReplyType(replyType);
            bookCircleReply.setReplyObjId(replyObjId);
            bookCircleReply.setContent(content);
            bookCircleReply = bookCircleReplyDao.save(bookCircleReply);

            //set replynum
            bookCircleDynamic.setReplyNum(bookCircleDynamic.getReplyNum() + 1);
            bookCircleDynamic = bookCircleDynamicDao.save(bookCircleDynamic);

            //create message
            AddUtils.addUserMessage(
                    objUserId,
                    userId,
                    null,
                    MessageType.BookCircleReply.getType(),
                    bookCircleReply.getReplyId(),
                    com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
        return bookCircleReply;
    }

    //新增藏书
    public static UserBook addUserBook(long userId, Book book, String category, int entryType) {
        UserBook userBook = null;
        if (userId > 0 && book != null && (entryType == EntryType.SearchInput.getType() || entryType == EntryType.ManualInput.getType())) {
            userBook = new UserBook();
            userBook.setUserId(userId);
            userBook.setBookId(book.getBookId());
            userBook.setTitle(book.getTitle());
            userBook.setAuthor(book.getAuthor());
            userBook.setCategory(book.getCategory());
            userBook.setEnterType(entryType);
            userBook.setLease(false);
            userBook.setSale(false);
            userBook.setEvaluation(UserBookUtils.getEvaluation(book));
            userBook.setDayRentGold(UserBookUtils.DEFAULT_DAY_RENT_GOLD);

            UserBookUtils.setUserBookCategory(userBook, category);

            //update user info book num
            UserInfo userInfo = userInfoDao.findOne(userBook.getUserId());
            userInfo.setBookNum(userInfo.getBookNum() + 1);
            userInfo = UpdateUtils.updateUserInfo(userInfo);

            //update book
            book.setEntryNum(book.getEntryNum() + 1);
            book = UpdateUtils.updateBook(book);
        }
        return userBook;
    }

    //新增 用户分类
    public static UserCategory addUserCategory(long userId, String category) {
        UserCategory userCategory = null;
        if (userCategoryDao.findByUserIdAndCategory(userId, category) == null) {
            userCategory = new UserCategory();
            userCategory.setUserId(userId);
            userCategory.setCategory(category);
            userCategory.setBookNum(0);
            userCategory = userCategoryDao.save(userCategory);
        }
        return userCategory;
    }

    //新增书签
    public static UserBookmark addUserBookmark(UserBook userBook, int pages, int totalPage) {
        UserBookmark userBookmark = null;
        if (0 < pages && pages <= totalPage) {
            userBookmark = new UserBookmark();
            userBookmark.setUserId(userBook.getUserId());
            userBookmark.setUserBookId(userBook.getUserBookId());
            userBookmark.setName(bookDao.findOne(userBook.getBookId()).getTitle());
            userBookmark.setPages(pages);
            userBookmark.setTotalPage(totalPage);
            userBookmark.setStatus(com.wg.common.Enum.userbook.ReadStatus.Reading.getType());
            userBookmark = userBookmarkDao.save(userBookmark);

            //set userbook read status reading
            userBook.setReadStatus(com.wg.common.Enum.userbook.ReadStatus.Reading.getType());
            userBook = UpdateUtils.updateUserBook(userBook);
        }
        return userBookmark;
    }

    //新增笔记本
    public static Notebook addNotebook(long userId, Book book, String name) {
        Notebook notebook = null;
        if (book != null && userId > 0) {
            notebook = new Notebook();
            notebook.setBookId(book.getBookId());
            notebook.setName(name);
            notebook.setUserId(userId);
            notebook.setPermission(Permission.Open.getType());
            notebook = notebookDao.save(notebook);

            //set notebook num
            UserInfo userInfo = userInfoDao.findOne(notebook.getUserId());
            userInfo.setNotebookNum(userInfo.getNotebookNum() + 1);
            userInfo = UpdateUtils.updateUserInfo(userInfo);
        }
        return notebook;
    }

    //新增笔记
    public static Note addNote(Notebook notebook,
                               String content, String originText, String chapter, int pages,
                               String otherLocation, double sort, MultipartFile file) {
        Note note = null;
        if (notebook != null && (StringUtils.isNotBlank(content) || file != null)) {
            note = new Note();
            note.setContent(content);
            note.setOriginText(originText);
            note.setChapter(chapter);
            note.setPages(pages);
            note.setOtherLocation(otherLocation);
            note.setSort(sort);
            note.setNotebookId(notebook.getNotebookId());
            note = noteDao.save(note);
            if (file != null) {
                String path = ImageUtils.saveImage(Constant.NOTEBOOK_CONTENT_FOLDER, note.getNoteId(), file);
                if (path != null) {
                    note.setImage(path);
                    note = noteDao.save(note);
                }
            }
            //set notebook notenum
            notebook.setNoteNum(notebook.getNoteNum() + 1);
            notebook = notebookDao.save(notebook);
        }
        return note;
    }

    //新增情节
    public static Storyline addStoryline(Notebook notebook, String node, String story, String characters, String places, double sort) {
        Storyline storyline = new Storyline();
        storyline.setNotebookId(notebook.getNotebookId());
        storyline.setNode(node);
        storyline.setStory(story);
        storyline.setCharacters(characters);
        storyline.setPlaces(places);
        storyline.setSort(sort);
        storyline = storylineDao.save(storyline);
        //update notebook story num
        notebook.setStorylineNum(notebook.getStorylineNum() + 1);
        notebook = notebookDao.save(notebook);
        return storyline;
    }

    //新增书单
    public static BookSheet addBookSheet(long userId, String name, String description, MultipartFile file) {
        BookSheet bookSheet = null;
        if (StringUtils.isNotBlank(name) && userId > 0) {
            bookSheet = new BookSheet();
            bookSheet.setUserId(userId);
            bookSheet.setName(name);
            bookSheet.setDescription(description);
            bookSheet = UpdateUtils.updateBookSheet(bookSheet);
            String path = ImageUtils.saveImage(Constant.BOOK_SHEET_COVER_FOLDER, bookSheet.getSheetId(), file);
            bookSheet.setCover(path == null ? Constant.BOOK_SHEET_COVER_DEFAULT : path);
            bookSheet = UpdateUtils.updateBookSheet(bookSheet);

            //set booksheet num
            UserInfo userInfo = userInfoDao.findOne(bookSheet.getUserId());
            userInfo.setBookSheetNum(userInfo.getBookSheetNum() + 1);
            userInfo = UpdateUtils.updateUserInfo(userInfo);
        }
        return bookSheet;
    }

    //新增书单书籍
    public static BookSheetBook addBookSheetBook(BookSheet bookSheet, Book book) {
        BookSheetBook bookSheetBook = null;
        if (bookSheet != null && book != null) {
            bookSheetBook = bookSheetBookDao.findBySheetIdAndBookId(bookSheet.getSheetId(), book.getBookId());
            if (bookSheetBook == null) {
                bookSheetBook = new BookSheetBook();
                bookSheetBook.setSheetId(bookSheet.getSheetId());
                bookSheetBook.setBookId(book.getBookId());
                bookSheetBook = bookSheetBookDao.save(bookSheetBook);
                bookSheet.setBookNum(bookSheet.getBookNum() + 1);
                bookSheet = UpdateUtils.updateBookSheet(bookSheet);
            }
        }
        return bookSheetBook;
    }

    //书籍搜索关键字
    public static BookKeyword addBookKeyword(String keyword) {
        BookKeyword bookKeyword = null;
        if (StringUtils.isNotBlank(keyword)) {
            bookKeyword = new BookKeyword();
            bookKeyword.setKeyword(keyword);
            bookKeyword.setSearchNum(1);
            bookKeyword = bookKeywordDao.save(bookKeyword);
        }
        return bookKeyword;
    }

    //创建新消息
    public static void addUserMessage(long acceptUserId, long sendUserId, String content,
                                      int messageType, long messageObjId, int dealStatus, int readStatus) {
        try {
            if (acceptUserId != sendUserId) {
                UserMessage userMessage = new UserMessage();
                userMessage.setAcceptUserId(acceptUserId);
                userMessage.setSendUserId(sendUserId);
                userMessage.setContent(content);
                userMessage.setMessageType(messageType);
                userMessage.setMessageObjId(messageObjId);
                userMessage.setDealStatus(dealStatus);
                userMessage.setReadStatus(readStatus);
                userMessage = userMessageDao.save(userMessage);

                //push message
                if (userMessage.getReadStatus() == ReadStatus.NotRead.getStatus()) {
                    UserMsgUtils.pushMessage(userMessage);
                }
            }
        } catch (Exception e) {
            //抓取添加消息推送消息异常，避免消息异常影响重要业务逻辑
            e.printStackTrace();
        }
    }

    //新增点赞
    public static UserGood addUserGood(long userId, int goodType, long goodObjId) {
        UserGood userGood = new UserGood();
        userGood.setGoodType(goodType);
        userGood.setGoodObjId(goodObjId);
        userGood.setUserId(userId);
        userGood = userGoodDao.save(userGood);

        if (goodType == GoodType.Comment.getType()) {
            GroupComment groupComment = groupCommentDao.findOne(userGood.getGoodObjId());
            groupComment.setGoodNum(groupComment.getGoodNum() + 1);
            groupComment = groupCommentDao.save(groupComment);
            AddUtils.addUserMessage(
                    groupComment.getUserId(),
                    userId,
                    null,
                    MessageType.Good_Comment.getType(),
                    userGood.getGoodId(),
                    com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        } else if (goodType == GoodType.Dynamic.getType()) {
            BookCircleDynamic bookCircleDynamic = bookCircleDynamicDao.findOne(userGood.getGoodObjId());
            bookCircleDynamic.setGoodNum(bookCircleDynamic.getGoodNum() + 1);
            bookCircleDynamic = bookCircleDynamicDao.save(bookCircleDynamic);
            AddUtils.addUserMessage(
                    bookCircleDynamic.getUserId(),
                    userId,
                    null,
                    MessageType.Good_Dynamic.getType(),
                    userGood.getGoodId(),
                    com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        } else if (goodType == GoodType.BookSheet.getType()) {
            BookSheet bookSheet = bookSheetDao.findOne(userGood.getGoodObjId());
            bookSheet.setGoodNum(bookSheet.getGoodNum() + 1);
            bookSheet = UpdateUtils.updateBookSheet(bookSheet);
            AddUtils.addUserMessage(
                    bookSheet.getUserId(),
                    userId,
                    null,
                    MessageType.Good_BookSheet.getType(),
                    userGood.getGoodId(),
                    com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        } else if (goodType == GoodType.Picword.getType()) {
            Picword picword = picwordDao.findOne(userGood.getGoodObjId());
            picword.setGoodNum(picword.getGoodNum() + 1);
            picword = picwordDao.save(picword);
            AddUtils.addUserMessage(
                    picword.getUserId(),
                    userId,
                    null,
                    MessageType.Good_Picword.getType(),
                    userGood.getGoodId(),
                    com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
        return userGood;
    }

    //新增点赞
    public static UserCollection addUserCollection(long userId, int collectType, long collectObjId) {
        UserCollection userCollection = userCollectionDao.findByUserIdAndCollectObjIdAndCollectType(userId, collectObjId, collectType);
        if (userCollection == null && (collectType == CollectType.Book.getType()
                || collectType == CollectType.BookSheet.getType()
                || collectType == CollectType.Picword.getType())) {
            userCollection = new UserCollection();
            userCollection.setUserId(userId);
            userCollection.setCollectType(collectType);
            userCollection.setCollectObjId(collectObjId);
            userCollection = userCollectionDao.save(userCollection);

            if (collectType == CollectType.Book.getType()) {
                Book book = bookDao.findOne(collectObjId);
                book.setCollectionNum(book.getCollectionNum() + 1);
                book = UpdateUtils.updateBook(book);
            } else if (collectType == CollectType.BookSheet.getType()) {
                BookSheet bookSheet = bookSheetDao.findOne(collectObjId);
                bookSheet.setCollectionNum(bookSheet.getCollectionNum() + 1);
                bookSheet = UpdateUtils.updateBookSheet(bookSheet);
                AddUtils.addUserMessage(
                        bookSheet.getUserId(),
                        userId,
                        null,
                        MessageType.Collect_BookSheet.getType(),
                        userCollection.getCollectionId(),
                        com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            } else if (collectType == CollectType.Picword.getType()) {
                Picword picword = picwordDao.findOne(collectObjId);
                picword.setCollectionNum(picword.getCollectionNum() + 1);
                picword = picwordDao.save(picword);
                AddUtils.addUserMessage(
                        picword.getUserId(),
                        userId,
                        null,
                        MessageType.Collect_Picword.getType(),
                        userCollection.getCollectionId(),
                        com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            }
        }
        return userCollection;
    }

    //新增验证码

    public static Valid addValid(String deviceToken, String regMobile, int useCondition) {
        Valid valid = new Valid();
        valid.setDeviceToken(deviceToken);
        valid.setRegMobile(regMobile);
        valid.setUseCondition(useCondition);
        valid.setRestSendNum(ValidUtils.VALID_REQUEST_NUM);
        valid = validDao.save(valid);
        return valid;
    }

    //新增新闻
    public static News addNews(String ukey, String title, String images, String link, String source, Date pubDate, String path, int getType) {
        News news = new News();
        news.setUniqueKey(ukey);
        news.setTitle(title);
        news.setImages(images);
        news.setLink(link);
        news.setSource(source);
        news.setPubDate(pubDate);
        news.setHtml(path);
        news.setGetType(getType);
        news = newsDao.save(news);
        return news;
    }

    //新增图文
    public static Picword addPicword(long userId, long bookId, int permisson, MultipartFile file) {
        Picword picword = null;
        if (userId > 0 && file != null) {
            picword = new Picword();
            picword.setUserId(userId);
            picword.setBookId(bookId);
            picword.setGoodNum(0);
            picword.setCollectionNum(0);
            picword.setPermission(permisson);
            picword = picwordDao.save(picword);
            picword.setImage(ImageUtils.saveImage(Constant.PICWORD_CONTENT_FOLDER, picword.getPicwordId(), file));
            picword = picwordDao.save(picword);
        }
        return picword;
    }

    //添加好友关注
    public static UserFriend addUserFriend(long userId, long friendId) {
        UserFriend userFriend = null;
        if (userId != friendId) {
            userFriend = new UserFriend();
            userFriend.setUserId(userId);
            userFriend.setFriendId(friendId);
            userFriend = userFriendDao.save(userFriend);

            //update concern num & fan num
            UserInfo user = userInfoDao.findOne(userId);
            user.setConcernNum(user.getConcernNum() + 1);
            user = UpdateUtils.updateUserInfo(user);

            UserInfo friend = userInfoDao.findOne(friendId);
            friend.setFanNum(friend.getFanNum() + 1);
            friend = UpdateUtils.updateUserInfo(friend);

            AddUtils.addUserMessage(
                    friendId,
                    userId,
                    null,
                    MessageType.FriendConcern.getType(),
                    userFriend.getUserFriendId(),
                    com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
        return userFriend;
    }

    //新建home
    public static Home addHome(int objType, long objId, double heat) {
        Home home = new Home();
        home.setObjType(objType);
        home.setObjId(objId);
        home.setHeat(heat);
        home = homeDao.save(home);
        return home;
    }

    public static BookCheck addBookCheck(long userId,
                                         String title, String subTitle, String originTitle,
                                         String author, String translator, String price,
                                         String publisher, String pubDate, String binding,
                                         String pages, String summary, String authorInfo,
                                         String isbn10, String isbn13, MultipartFile file) {
        BookCheck bookCheck = new BookCheck();
        bookCheck.setUserId(userId);
        bookCheck.setTitle(title);
        bookCheck.setSubTitle(subTitle);
        bookCheck.setOriginTitle(originTitle);
        bookCheck.setAuthor(author);
        bookCheck.setTranslator(translator);
        bookCheck.setPrice(price);
        bookCheck.setPublisher(publisher);
        bookCheck.setPubDate(pubDate);
        bookCheck.setBinding(binding);
        bookCheck.setPages(pages);
        bookCheck.setSummary(summary);
        bookCheck.setAuthorInfo(authorInfo);
        bookCheck.setIsbn10(isbn10);
        bookCheck.setIsbn13(isbn13);
        bookCheck = bookCheckDao.save(bookCheck);
        String path = ImageUtils.saveImage(Constant.BOOKCHECK_COVER_FOLDER, bookCheck.getBookCheckId(), file);
        bookCheck.setCover(path != null ? path : Constant.BOOK_COVER_DEFAULT);
        bookCheck = bookCheckDao.save(bookCheck);
        String msg = "新书需要审核:《" + bookCheck.getTitle() + "》";
        EmailUtils.sendEmailBookCheck("新书审核", msg, PropConfig.OFFICER_EMAIL);
        return bookCheck;
    }

    //新增书籍订单
    public static BookOrder addBookOrder(UserToken userToken, UserBook userBook, int leaseDay, int orderType, BigDecimal payGold) {
        BookOrder bookOrder = null;
        if (orderType == OrderType.Lease.getType() && leaseDay > 0 || orderType == OrderType.Sale.getType()) {
            bookOrder = new BookOrder();
            bookOrder.setOrderNumber(BookOrderUtils.generOrderNumber(userToken, orderType));
            bookOrder.setUserId(userToken.getUserId());//申请人
            UserInfo owner = userInfoDao.findOne(userBook.getUserId());
            bookOrder.setOwnerId(owner.getUserId());//拥有人
            bookOrder.setOrderType(orderType);//订单类型
//            bookOrder.setOrderStatus(OrderStatus.Deal.getType());//订单状态
//            bookOrder.setFinishType(FinishType.Ongoing.getType());//订单完成类型
            bookOrder.setBookId(userBook.getBookId());//藏书id
            bookOrder.setEvaluation(DecimalUtils.newDecimal(userBook.getEvaluation()));//估价
            bookOrder.setDayRentGold(DecimalUtils.newDecimal(userBook.getDayRentGold()));//日租金
            bookOrder.setOrderPayGold(payGold);//订单支付金
            bookOrder.setTotalRentGold(BigDecimal.ZERO);//总租金
            bookOrder.setBrokenGold(BigDecimal.ZERO);//折损金
            bookOrder.setBreakGold(BigDecimal.ZERO);//违约金
            bookOrder.setLeaseDay(leaseDay);//租借天数
            bookOrder.setDelayDay(0);//延期天数
            bookOrder.setComplaint(false);
            bookOrder.setDelete(false);//删除标志
            bookOrder.setOwnerDelete(false);//拥有人删除标志
            bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Deal.getStatus(), FinishType.Ongoing.getType(), TimeUtils.getCurrentDate());
            //smsg
            SMsgUtils.sendMsg(null, SMsgTemplate.NewOrder, bookOrder);
            //notify owner deal new order
            addUserMessage(
                    bookOrder.getOwnerId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    bookOrder.getOrderType() == OrderType.Lease.getType() ?
                            MessageType.Lo_Or_NewOrder.getType() : MessageType.So_Or_NewOrder.getType(),
                    bookOrder.getOrderId(),
                    com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
        return bookOrder;
    }

    //新增订单评价
    public static OrderEvaluate addOrderEvaluate(UserToken userToken, BookOrder bookOrder, String content, double rating) {
        OrderEvaluate orderEvaluate = null;
        if (content != null) {
            orderEvaluate = new OrderEvaluate();
            orderEvaluate.setUserId(userToken.getUserId());
            orderEvaluate.setOwnerId(bookOrder.getOwnerId());
            orderEvaluate.setBookId(bookOrder.getBookId());
            orderEvaluate.setOrderId(bookOrder.getOrderId());
            orderEvaluate.setContent(content);
            orderEvaluate.setRating(rating);
            orderEvaluate = orderEvaluateDao.save(orderEvaluate);
        }
        return orderEvaluate;
    }

    //添加用户第三方账户
    public static ThirdPay addThirdPay(long userId, int payType, String account, String name, String nickname) {
        ThirdPay thirdPay = thirdPayDao.findByUserIdAndPayType(userId, payType);
        if (thirdPay == null && (payType == PayType.Alipay.getType() || payType == PayType.Weixin.getType())
                && StringUtils.isNotBlank(account) && StringUtils.isNotBlank(name)) {
            thirdPay = new ThirdPay();
            thirdPay.setUserId(userId);
            thirdPay.setPayType(payType);
            thirdPay.setAccount(account);
            thirdPay.setName(name);
            thirdPay.setNickname(nickname);
            thirdPay = thirdPayDao.save(thirdPay);
        }
        return thirdPay;
    }

    //新增账单：充值
    public static UserBill addUserBill(UserToken userToken, int billType, int payType, BigDecimal billGold) {
        UserBill userBill = null;
        if (billType == BillType.Charge.getType() && DecimalUtils.greaterThanZero(billGold)) {
            userBill = new UserBill();
            userBill.setUserId(userToken.getUserId());
            userBill.setBillType(billType);
            userBill.setBillGold(billGold);
            userBill.setTradeNumber(UserAccUtils.tradeNumber(userToken.getPlatform(), billType, payType));
            userBill.setPayType(payType);
            userBill.setDealStatus(DealStatus.InDeal.getStatus());
            userBill.setAccGold(userAccountDao.findByUserId(userToken.getUserId()).getTotalGold());
            userBill.setShow(false);
            userBill = userBillDao.save(userBill);
            userBill.setExtendInfo(BillSignUtils.signInfo(userBill));
            userBill = userBillDao.save(userBill);
        }
        return userBill;
    }

    //新增账单：提现
    public static UserBill addUserBill(UserToken userToken, int billType, ThirdPay thirdPay, BigDecimal billGold) {
        UserBill userBill = null;
        if (billType == BillType.Withdraw.getType() && thirdPay != null && DecimalUtils.greaterThanZero(billGold)) {
            userBill = new UserBill();
            userBill.setUserId(userToken.getUserId());
            userBill.setBillType(billType);
            userBill.setBillGold(billGold);
            userBill.setTradeNumber(UserAccUtils.tradeNumber(userToken.getPlatform(), billType, thirdPay.getPayType()));
            userBill.setPayType(thirdPay.getPayType());
            userBill.setAccount(thirdPay.getAccount());
            userBill.setName(thirdPay.getName());
            userBill.setNickname(thirdPay.getNickname());
            userBill.setDealStatus(DealStatus.InDeal.getStatus());
            userBill.setAccGold(userAccountDao.findByUserId(userToken.getUserId()).getTotalGold());
            userBill.settTime(TimeUtils.getCurrentDate());
            userBill = userBillDao.save(userBill);
            userBill.setExtendInfo(BillSignUtils.signInfo(userBill));
            userBill = userBillDao.save(userBill);
        }
        return userBill;
    }

    //新增账单：订单收益、支出
    public static UserBill addBill(long userId, int billType, String orderNumber, BigDecimal transferGold) {
        UserBill userBill = null;
        if (!DecimalUtils.lessThanZero(transferGold)) {
            userBill = new UserBill();
            userBill.setUserId(userId);
            userBill.setBillType(billType);
            userBill.setBillGold(transferGold);
            userBill.setOrderNumber(orderNumber);
            userBill.setDealStatus(DealStatus.Success.getStatus());
            userBill.setDealTime(TimeUtils.getCurrentDate());
            userBill.setAccGold(userAccountDao.findByUserId(userId).getTotalGold());
            userBill = userBillDao.save(userBill);
            userBill.setExtendInfo(BillSignUtils.signInfo(userBill));
            userBill = userBillDao.save(userBill);
        }
        return userBill;
    }

    //添加用户钱包
    public static UserAccount addUserAccount(long userId) {
        UserAccount userAccount = userAccountDao.findByUserId(userId);
        if (userAccount == null) {
            userAccount = new UserAccount();
            userAccount.setUserId(userId);
            userAccount.setTotalGold(BigDecimal.ZERO);
            userAccount.setFrozenGold(BigDecimal.ZERO);
            userAccount = userAccountDao.save(userAccount);
        }
        return userAccount;
    }

    //新增社区
    public static Community addCommunity(long userId, String name,
                                         String address, double longitude, double latitude,
                                         String tag, int joinStatus, String commDes, MultipartFile file) {
        Community community = null;
        if (StringUtils.isNotBlank(name) && userId > 0) {
            community = new Community();
            community.setName(name);
            community.setAddress(address);
            community.setLongitude(longitude);
            community.setLatitude(latitude);
            community.setTag(tag);
            community.setJoinStatus(joinStatus);
            community.setCommDes(commDes);
            Map<String, String> map = new HashMap<String, String>();
            map.put("userId", String.valueOf(userId));
            community.setExtendInfo(JSON.toJSONString(map));
            community = communityDao.save(community);

            String path = ImageUtils.saveImage(Constant.COMMUNITY_THEMEPIC_FOLDER, community.getCommunityId(), file);
            community.setThemePic(path == null ? Constant.COMMUNITY_THEMEPIC_DEFAULT : path);
            community = communityDao.save(community);

            //im tribe
            IMUtils.createTribe(community, userId);

            //add community member
            addCommMember(community, userId, MemberType.Owner.getType());
        }
        return community;
    }

    //新增社区成员
    public static CommMember addCommMember(Community community, long userId, int memberType) {
        CommMember commMember = commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userId);
        if (commMember == null && community != null && userId > 0 && memberType > 0) {
            commMember = new CommMember();
            commMember.setCommunityId(community.getCommunityId());
            commMember.setUserId(userId);
            commMember.setMemberType(memberType);
            commMember = commMemberDao.save(commMember);

            //update member num
            community.setMemberNum(community.getMemberNum() + 1);
            community = communityDao.save(community);

            //im tribe if new add
            if (memberType != MemberType.Owner.getType()) {
                IMUtils.joinTribe(community, commMember.getUserId());
            }
        }
        return commMember;
    }

    //新增社区书籍
    public static CommBook addCommBook(Community community, UserToken userToken, UserBook userBook) {
        CommBook commBook = commBookDao.findByCommunityIdAndUserIdAndBookId(
                community.getCommunityId(), userToken.getUserId(), userBook.getBookId());
        if (commBook == null) {
            commBook = new CommBook();
            commBook.setCommunityId(community.getCommunityId());
            commBook.setUserId(userToken.getUserId());
            commBook.setBookId(userBook.getBookId());
            commBook.setTitle(userBook.getTitle());
            commBook.setAuthor(userBook.getAuthor());
            commBook.setCategory(userBook.getCategory());
            commBook = commBookDao.save(commBook);

            //update community book num
            community.setBookNum(community.getBookNum() + 1);
            community = communityDao.save(community);
        }
        return commBook;
    }

    //新增社区书籍
    public static void addCommBook(Community community, UserToken userToken, List<UserBook> userBookList) {
        for (UserBook userBook : userBookList) {
            CommBook commBook = commBookDao.findByCommunityIdAndUserIdAndBookId(
                    community.getCommunityId(), userToken.getUserId(), userBook.getBookId());
            if (commBook == null) {
                commBook = new CommBook();
                commBook.setCommunityId(community.getCommunityId());
                commBook.setUserId(userToken.getUserId());
                commBook.setBookId(userBook.getBookId());
                commBook.setTitle(userBook.getTitle());
                commBook.setAuthor(userBook.getAuthor());
                commBook.setCategory(userBook.getCategory());
                commBook = commBookDao.save(commBook);
            }
        }
        //update community book num
        community.setBookNum(commBookDao.countByCommunityId(community.getCommunityId()));
        community = communityDao.save(community);
    }

    //add solr delete temp index
    public static void addTempSolrIndex(SolrType solrType, long ObjId) {
        TempSolrIndex tempSolrIndex = new TempSolrIndex();
        tempSolrIndex.setObjId(ObjId);
        tempSolrIndex.setType(solrType.getType());
        tempSolrIndexDao.save(tempSolrIndex);
    }
}
