package com.wg.book.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wg.book.domain.Book;
import com.wg.book.domain.BookKeyword;
import com.wg.book.domain.TempBookCover;
import com.wg.bookcircle.utils.BookCircleUtils;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.common.Constant;
import com.wg.common.Enum.book.DownStatus;
import com.wg.common.Enum.book.KeywordSearchType;
import com.wg.common.Enum.book.SourceType;
import com.wg.common.Enum.bookcircle.DynamicType;
import com.wg.common.Enum.bookcircle.LinkType;
import com.wg.common.Enum.common.Permission;
import com.wg.common.Enum.userbook.CheckStatus;
import com.wg.common.Enum.userbook.EntryType;
import com.wg.common.PropConfig;
import com.wg.common.utils.HttpUtils;
import com.wg.common.utils.ImageUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.solr.modle.QueryRes;
import com.wg.solr.utils.BookSolr;
import com.wg.task.SearchThirdTask;
import com.wg.userbook.domain.BookCheck;
import com.wg.userbook.domain.UserBook;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by wzhonggo on 8/26/2016.
 */
public class BookUtils {
    public static final String DOUBAN_NAME = "https://api.douban.com/v2/book/search?";
    public static final int DOUBAN_RATING_NUM = 10;
    public static final String BOOK_AUTHOR_SPLIT_CHAR = "[ .,．•，·。、*~`；;:|《》<>【】{}［］（）〔〕\\(\\)\\[\\]\\s/\\\\\\'‘\"\"“”]";
    public static final int THIRD_GET_NUM = 100;
    public static final long COMMUNITY_NOTEXIST_SIGN = 0;
    public static final String NULL_CATEGORY = "未知";

    //title过滤
    public static String titleFilter(String title) {
        if (title != null) {
            title = title.replaceAll("[\\(\\{\\[\\（\\【].*?[\\)\\}\\]\\）\\】]", "");
            title = title.replaceAll("[\\(\\{\\[\\（\\【][^\\)\\}\\]\\）\\】]*?$", "");
            title = title.trim();
        }
        return title;
    }

    //isbn10、13格式判断
    public static boolean isIsbn(String isbn) {
        boolean isIsbn = false;
        if (isbn != null) {
            isIsbn = isbn.matches("^\\d{10}$") || isbn.matches("^\\d{13}$");
        }
        return isIsbn;
    }

    //isbn unique
    public static boolean uniqueIsbn(String isbn) {
        return bookDao.findByisbn10(isbn) == null && bookDao.findByIsbn13(isbn) == null;
    }

    //设置书籍社区,归入或新建
    public static void setBookGroup(Book book) {
        boolean create = true;
        long communityId = COMMUNITY_NOTEXIST_SIGN;
        //try find one bookcomm
        for (BookGroup bookGroup : bookGroupDao.findByTitle(titleFilter(book.getTitle()))) {
            int flag = compareAuthor(book.getAuthor(), bookGroup.getAuthor());
            if (flag == 1) {//find one,sign false and break to create
                create = false;
                communityId = bookGroup.getCommunityId();
                break;
            } else if (flag == 2) {//not sure,sign false and continue try next
                create = false;
            }
        }
        if (create) {
            BookGroup bookGroup = AddUtils.addBookCommunity(book);
            communityId = bookGroup.getCommunityId();
        }
        book.setCommunityId(communityId);
        book = UpdateUtils.updateBook(book);
    }

    //0 not one author、1 one author、2 not sure
    public static int compareAuthor(String srcAuthor, String communityAuthor) {
        //init sign no one
        int flag = 0;
        String[] srcArr = srcAuthor.split(BOOK_AUTHOR_SPLIT_CHAR);
        String[] communityArr = communityAuthor.split(BOOK_AUTHOR_SPLIT_CHAR);
        for (int index = 0; index < srcArr.length; index++) {
            for (int jndex = 0; jndex < communityArr.length; jndex++) {
                if (srcArr[index].length() > 1 && communityArr[jndex].length() > 1) {
                    if (srcArr[index].contains(communityArr[jndex]) || communityArr[jndex].contains(srcArr[index])) {
                        //contains sign one break and return
                        flag = 1;
                        break;
                    }
                }
            }
        }
        // not contains use similar degree
        if (flag == 0) {
            double degree = Utils.getSimilarDegree(srcAuthor, communityAuthor);
            if (degree < 0.34) {
                //degree less than 0.34 sign not one
                flag = 0;
            } else if (degree > 0.67) {
                // degree big than 0.67 sign one
                flag = 1;
            } else {
                //sign not sure
                flag = 2;
            }
        }
        return flag;
    }

    //书籍搜索(book、userbook、booksheet),keyword、isbn
    public static QueryRes<Book> bookSearch(String keyword, int page) {
        List<Book> bookList = new ArrayList<Book>();
        if (isIsbn(keyword)) {
            Book book = getBookByIsbn(keyword);
            if (book != null) {
                bookList.add(book);
            }
            return new QueryRes<Book>(bookList, 1);
        } else {
            updateBookSearchKeyword(keyword, page, KeywordSearchType.User.getType());
            return BookSolr.findByKeywordContains(keyword, page);
        }
    }

    //首次查询,keyword记录到task set,后端查询立即执行查询,非首次首页查询记录次数
    public static void updateBookSearchKeyword(String keyword, int page, int type) {
        if (StringUtils.isNotBlank(keyword)) {
            BookKeyword oldKeyword = bookKeywordDao.findByKeyword(keyword);
            if (oldKeyword == null) {
                if (type == KeywordSearchType.Backend.getType()) {
                    //backend use exe third search immediately
                    searchThirdBook(keyword);
                } else {
                    //user use record to set for task search,return immediately
                    SearchThirdTask.keywordSet.add(keyword);
                }
            } else if (page == 0) {
                //update search num if keyword has been search in history and page is zero
                oldKeyword.setSearchNum(oldKeyword.getSearchNum() + 1);
                oldKeyword = bookKeywordDao.save(oldKeyword);
            }
        }
    }

    //isbn查询db,不存在则查询第三方录入,然后再次查询db,并返回book
    public static Book getBookByIsbn(String isbn) {
        Book book = null;
        if (isIsbn(isbn)) {
            //search db by isbn
            book = isbn.length() == 13 ? bookDao.findByIsbn13(isbn) : bookDao.findByisbn10(isbn);
            if (book == null) {
                //search third by isbn
                searchThirdBook(isbn);
                //again search db by isbn
                book = isbn.length() == 13 ? bookDao.findByIsbn13(isbn) : bookDao.findByisbn10(isbn);
            }
        }
        return book;
    }

    //执行一次第三方查询,更新keyword或isbn
    public static boolean searchThirdBook(String keyword) {
        logger.info("[" + TimeUtils.getCurrentDate() + "] search book by third,keyword:" + keyword);
        if (StringUtils.isNotBlank(keyword)) {
            if (!isIsbn(keyword)) {
                if (bookKeywordDao.findByKeyword(keyword) == null) {
                    if (getBookFromDouban(keyword, 0, THIRD_GET_NUM)) {
                        return AddUtils.addBookKeyword(keyword) != null;
                    }
                }
            } else {
                if (bookDao.findByIsbn13(keyword) == null && bookDao.findByisbn10(keyword) == null) {
                    if (getBookFromDouban(keyword, 0, 1)) {
                        return bookDao.findByIsbn13(keyword) != null || bookDao.findByisbn10(keyword) != null;
                    }
                }
            }
        }
        return false;
    }

    //向豆瓣发出获取书籍请求,通过关键字、isbn
    public static boolean getBookFromDouban(String keyword, int star, int number) {
        if (StringUtils.isNotBlank(keyword) && number > 0) {
            String url = DOUBAN_NAME + "q=" + Utils.encode(keyword) + "&start=" + star + "&count=" + number;
            String content = HttpUtils.requestString(new HttpGet(url));
            if (content != null) {
                JSONObject result = JSON.parseObject(content);
                JSONArray books = result.getJSONArray("books");
                if (books != null && books.size() > 0) {
                    for (int i = 0; i < books.size(); i++) {
                        JSONObject bookJson = books.getJSONObject(i);
                        try {
                            saveBookFromJson(bookJson);
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                    //搜索且有书籍录入
                    return true;
                }
            }
        }
        return false;
    }

    //从豆瓣json中保存书籍信息
    public static void saveBookFromJson(JSONObject bookJson) throws Exception {
        Book book = new Book();
        book.setTitle(bookJson.getString("title"));
        book.setSubTitle(bookJson.getString("subtitle"));
        book.setOriginTitle(bookJson.getString("origin_title"));
        book.setAuthor(getNameFromJson(bookJson.getJSONArray("author")));
        book.setThirdRating(bookJson.getJSONObject("rating").getFloat("average"));
        book.setRating(0);
        book.setRatingAllNum(0);
        book.setTag(getTagFromJson(bookJson.getJSONArray("tags")));
        book.setSummary(bookJson.getString("summary"));
        book.setPrice(bookJson.getString("price"));
        book.setCover(getCoverFromJson(bookJson.getJSONObject("images")));
        book.setCatalog(bookJson.getString("catalog"));
        book.setPages(bookJson.getString("pages"));
        book.setAuthorInfo(bookJson.getString("author_intro"));
        book.setTranslator(getNameFromJson(bookJson.getJSONArray("translator")));
        book.setPublisher(bookJson.getString("publisher"));
        book.setPubDate(bookJson.getString("pubdate"));
        book.setIsbn10(bookJson.getString("isbn10"));
        book.setIsbn13(bookJson.getString("isbn13"));
        book.setBinding(bookJson.getString("binding"));
        book.setSource(SourceType.DouBan.getType());
        // add book
        AddUtils.addBook(book);
    }

    //获取标签自定义计算长度
    public static int getTagLenth(String tag) {
        float len = 0;
        for (char charValue : tag.toCharArray())
            if ((charValue >= 'a' && charValue <= 'z')
                    || (charValue >= 'A' && charValue <= 'Z')
                    || (charValue >= '0' && charValue <= '9')) {
                len += 0.25;
            } else {
                len += 1;
            }
        return (int) Math.rint(len);
    }

    //从豆瓣json中获取标签
    public static String getTagFromJson(JSONArray tagArray) {
        HashSet<String> tagSet = new HashSet<String>();
        for (Object object : tagArray) {
            String tag = ((JSONObject) object).getString("name");
            if (tag != null) {
                tag = Utils.moveSign(tag);
                if (tag.length() > 1 && getTagLenth(tag) < 9) {
                    tagSet.add(tag);
                }
            }
        }
        return StringUtils.join(tagSet.toArray(), Constant.JOIN_CHAR);
    }

    //从json中获取豆瓣书籍作者、译者
    public static String getNameFromJson(JSONArray jsonArray) {
        String[] nameStrs = jsonArray.toArray(new String[jsonArray.size()]);
        String[] acceptStrs;
        int nameStrsLen = nameStrs.length;
        int acceptStrsLen = nameStrsLen <= 3 ? nameStrsLen : 3;
        acceptStrs = new String[acceptStrsLen];
        for (int index = 0; index < acceptStrsLen; index++) {
            if (nameStrs[index].length() >= 255) {
                return null;
            } else {
                acceptStrs[index] = nameStrs[index];
            }
        }
        if (acceptStrsLen != nameStrsLen) {
            acceptStrs[acceptStrsLen - 1] = acceptStrs[acceptStrsLen - 1] + " 等";
        }
        return StringUtils.join(acceptStrs, Constant.JOIN_CHAR);
    }

    //从json中获取豆瓣书籍封面url
    public static String getCoverFromJson(JSONObject images) {
        String cover = Constant.BOOK_COVER_DEFAULT;
        if (images.containsKey("large")) {
            if (!images.getString("large").contains("default")) {
                cover = images.getString("large");
            }
        } else if (images.containsKey("medium")) {
            if (!images.getString("medium").contains("default")) {
                cover = images.getString("medium");
            }
        } else if (images.containsKey("small")) {
            if (!images.getString("small").contains("default")) {
                cover = images.getString("small");
            }
        }
        return cover;
    }

    //新建书籍封面下载记录
    public static void newTempBookCover(Book book) {
        if (book != null) {
            String cover = book.getCover();
            if (cover != null && cover.contains("http")) {
                //url image
                TempBookCover tempBookCover = new TempBookCover();
                tempBookCover.setBookId(book.getBookId());
                tempBookCover.setDownStatus(DownStatus.NotDown.getStatus());
                tempBookCover = tempBookCoverDao.save(tempBookCover);
            }
        }
    }

    //删除下载完后的书籍列表记录
    public static void deleteTempBookCover(List<TempBookCover> tempBookCoverList) {
        for (TempBookCover tempBookCover : tempBookCoverList) {
            if (tempBookCover.getDownStatus() == DownStatus.DownSuccess.getStatus()) {
                tempBookCoverDao.delete(tempBookCover);
            }
        }
    }

    //用户藏书打分,更新书籍评分,打分、修改
    public static void updateBookRating(UserBook userbook, float rating) {
        Book book = bookDao.findOne(userbook.getBookId());
        float oldRating = userbook.getRating();
        long ratingAllNum = book.getRatingAllNum();
        float ratingSum = book.getRating() * ratingAllNum + rating;
        if (oldRating > 0) {
            ratingSum -= oldRating;
        } else {
            ratingAllNum += 1;
        }
        float ratingAver = 0;
        if (ratingAllNum > 0) {
            ratingAver = ratingSum / ratingAllNum;
        }
        book.setRating(ratingAver);
        book.setRatingAllNum(ratingAllNum);
        book = UpdateUtils.updateBook(book);
    }

    //审核书籍通过
    public static Book bookCheckPass(BookCheck bookCheck) {
        Book book = null;
        if (bookCheck.getCheckStatus() != CheckStatus.Pass.getStatus()) {
            if (bookCheck.getIsbn13() != null) {
                book = bookDao.findByIsbn13(bookCheck.getIsbn13());
            }
            if (book == null) {
                book = new Book();
                book.setTitle(bookCheck.getTitle());
                book.setSubTitle(bookCheck.getSubTitle());
                book.setOriginTitle(bookCheck.getOriginTitle());
                book.setAuthor(bookCheck.getAuthor());
                book.setTranslator(bookCheck.getTranslator());
                book.setPrice(bookCheck.getPrice());
                book.setPublisher(bookCheck.getPublisher());
                book.setPubDate(bookCheck.getPubDate());
                book.setBinding(bookCheck.getBinding());
                book.setPages(bookCheck.getPages());
                book.setSummary(bookCheck.getSummary());
                book.setAuthorInfo(bookCheck.getAuthorInfo());
                book.setIsbn10(bookCheck.getIsbn10());
                book.setIsbn13(bookCheck.getIsbn13());
                book.setCover(bookCheck.getCover());
                book.setLocalCover(true);
                book.setSource(SourceType.User.getType());
                //add book
                book = AddUtils.addBook(book);
                //cp cover if exsit
                book = moveCoverFromCheck(bookCheck, book);
            }
        }
        UserBook userBook = AddUtils.addUserBook(bookCheck.getUserId(), book, null, EntryType.ManualInput.getType());
        AddUtils.addBookCircleDynamic(
                userBook.getUserId(), BookCircleUtils.getBookEntryDynamicContent(book.getTitle()),
                0, LinkType.Common.getType(), null, null, Permission.Open.getType(), DynamicType.System.getType());
        return book;
    }

    //拷贝审核图片到书籍图片目录
    public static Book moveCoverFromCheck(BookCheck bookCheck, Book book) {
        String cover = bookCheck.getCover();
        if (cover != null && !cover.equals(Constant.BOOK_COVER_DEFAULT)) {
            cover = ImageUtils.saveImage(Constant.BOOK_COVER_FOLDER, book.getBookId(), new File(PropConfig.UPLOAD_PATH + cover));
        }
        book.setCover(cover != null ? cover : Constant.BOOK_COVER_DEFAULT);
        book = UpdateUtils.updateBook(book);
        return book;
    }
}
