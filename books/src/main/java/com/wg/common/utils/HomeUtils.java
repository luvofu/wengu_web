package com.wg.common.utils;

import com.wg.book.domain.Book;
import com.wg.book.utils.BookUtils;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.booksheet.domain.BookSheet;
import com.wg.common.Constant;
import com.wg.common.Enum.common.HomeType;
import com.wg.common.Enum.common.Permission;
import com.wg.common.Enum.common.SourceType;
import com.wg.common.domain.Home;
import com.wg.common.domain.HotBook;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.picword.domain.Picword;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.*;

import static com.wg.common.utils.dbutils.DaoUtils.*;


/**
 * Created by Administrator on 2016/9/29.
 */
public class HomeUtils {
    //更新home
    public static void updateHomeData() {
        Date startDate = TimeUtils.getModifyDate(TimeUtils.getCurrentDate(), -1, null, null, null);
        //book
        findNewHomeBook(startDate);
        //booksheet
        findNewHomeBookSheet(startDate);
        //comment
        findNewHomeCommunityComment(startDate);
        //picword
        findNewHomePicword(startDate);
    }

    //删除旧home数据
    public static void deleteHomeData(Date startDate) {
        homeDao.deleteOldData(startDate);
    }

    //按藏书数,查询新书插入home
    public static void findNewHomeBook(Date startDate) {
        Slice<Book> bookSlice = bookDao.findAllByUpdatedTimeGreaterThanOrderByEntryNumDesc(startDate, new PageRequest(0, Constant.HOME_NUM));
        for (Book book : bookSlice.getContent()) {
            AddUtils.addHome(HomeType.Book.getType(), book.getBookId(), getBookHeat(book));
        }
        int newNum = bookSlice.getContent().size();
        if (newNum < Constant.HOME_NUM) {
            int moreNum = Constant.HOME_NUM - newNum;
            Slice<Book> moreBookSlice = bookDao.findAllByUpdatedTimeLessThanOrderByEntryNumDesc(startDate, new PageRequest(0, moreNum));
            List<Book> moreBookList = moreBookSlice.getContent();
            for (Book book : moreBookList) {
                AddUtils.addHome(HomeType.Book.getType(), book.getBookId(), getBookHeat(book));
            }
        }
    }

    //按收藏数,查询新单单插入home
    public static void findNewHomeBookSheet(Date startDate) {
        Slice<BookSheet> bookSheetSlice = bookSheetDao.findAllByUpdatedTimeGreaterThanAndDescriptionIsNotNullOrderByCollectionNumDesc(
                startDate, new PageRequest(0, Constant.HOME_NUM));
        for (BookSheet bookSheet : bookSheetSlice.getContent()) {
            AddUtils.addHome(HomeType.BookSheet.getType(), bookSheet.getSheetId(), getBookSheetHeat(bookSheet));
        }
        int newNum = bookSheetSlice.getContent().size();
        if (newNum < Constant.HOME_NUM) {
            Slice<BookSheet> moreBookSheetSlice = bookSheetDao.findAllByUpdatedTimeLessThanAndDescriptionIsNotNullOrderByCollectionNumDesc(
                    startDate, new PageRequest(0, Constant.HOME_NUM - newNum));
            List<BookSheet> moreBookSheetList = moreBookSheetSlice.getContent();
            for (BookSheet bookSheet : moreBookSheetList) {
                AddUtils.addHome(HomeType.BookSheet.getType(), bookSheet.getSheetId(), getBookSheetHeat(bookSheet));
            }
        }
    }

    //按回复数,查询新评论插入home
    public static void findNewHomeCommunityComment(Date startDate) {
        Slice<GroupComment> communityCommentSlice = groupCommentDao.findAllByUpdatedTimeGreaterThanOrderByReplyNumDesc(startDate, new PageRequest(0, Constant.HOME_NUM));
        for (GroupComment groupComment : communityCommentSlice.getContent()) {
            AddUtils.addHome(HomeType.CommunityComment.getType(), groupComment.getCommentId(), getCommunityCommentHeat(groupComment));
        }
        int newNum = communityCommentSlice.getContent().size();
        if (newNum < Constant.HOME_NUM) {
            Slice<GroupComment> moreCommunityCommentSlice = groupCommentDao.findAllByUpdatedTimeLessThanOrderByReplyNumDesc(startDate, new PageRequest(0, Constant.HOME_NUM - newNum));
            for (GroupComment groupComment : moreCommunityCommentSlice.getContent()) {
                AddUtils.addHome(HomeType.CommunityComment.getType(), groupComment.getCommentId(), getCommunityCommentHeat(groupComment));
            }
        }
    }

    //按点赞数,查询新图文插入home
    private static void findNewHomePicword(Date startDate) {
        Slice<Picword> picwordSlice = picwordDao.findByCreatedTimeGreaterThanAndPermissionOrderByGoodNumDesc(
                startDate, Permission.Open.getType(), new PageRequest(0, Constant.HOME_NUM));
        for (Picword picword : picwordSlice.getContent()) {
            AddUtils.addHome(HomeType.Picword.getType(), picword.getPicwordId(), getPicwordHeat(picword));
        }
        int newNum = picwordSlice.getContent().size();
        if (newNum < Constant.HOME_NUM) {
            Slice<Picword> morePicwordSlice = picwordDao.findByCreatedTimeLessThanAndPermissionOrderByGoodNumDesc(
                    startDate, Permission.Open.getType(), new PageRequest(0, Constant.HOME_NUM - newNum));
            for (Picword picword : morePicwordSlice.getContent()) {
                AddUtils.addHome(HomeType.Picword.getType(), picword.getPicwordId(), getPicwordHeat(picword));
            }
        }
    }

    public static double getBookHeat(Book book) {
        return 0.4 * book.getRating() + 0.6 * book.getEntryNum();
    }

    public static double getBookSheetHeat(BookSheet bookSheet) {
        return bookSheet.getCollectionNum() / getDeltaHour(bookSheet.getCreatedTime());
    }

    public static double getCommunityCommentHeat(GroupComment groupComment) {
        return (groupComment.getGoodNum() * 0.4 + groupComment.getReplyNum() * 0.6) / getDeltaHour(groupComment.getCreatedTime());
    }

    public static double getPicwordHeat(Picword picword) {
        return (picword.getGoodNum() * 0.6 + picword.getCollectionNum() * 0.4) / getDeltaHour(picword.getCreatedTime());
    }

    //根据创建时间获取时间差,单位小时
    public static double getDeltaHour(Date createdDate) {
        double deltaHour = (TimeUtils.getCurrTimestamp() - createdDate.getTime()) / 3600000.0;
        if (deltaHour < 1) {
            deltaHour = 1.0;
        }
        return deltaHour;
    }

    //更新热书到hotbook
    public static void updateHotBookByHome() {
        Slice<Home> homeSlice = homeDao.findByObjTypeOrderByHeatDesc(
                HomeType.Book.getType(), new PageRequest(0, Constant.HOME_GET_NUM));
        List<Book> bookList = new ArrayList<Book>();
        for (Home home : homeSlice.getContent()) {
            Book book = bookDao.findOne(home.getObjId());
            if (book != null) {
                bookList.add(book);
            }
            if (bookList.size() >= Constant.PAGE_NUM_MEDIUM) {
                break;
            }
        }
        //create new hotbook from home
        for (Book book : bookList) {
            HotBook hotBook = hotBookDao.findByBookId(book.getBookId());
            if (hotBook == null) {
                hotBook = new HotBook();
                hotBook.setBookId(book.getBookId());
                hotBook.setSource(SourceType.Home.getType());
                hotBook = hotBookDao.save(hotBook);
            } else if (hotBook.getSource() == SourceType.Home.getType()) {
                hotBook = hotBookDao.save(hotBook);
            }
        }
    }

    //从豆瓣isbn更新书籍到hotbook
    public static void updateHotBookByDouBan(String isbns) {
        if (isbns != null && StringUtils.isNotBlank(isbns)) {
            //try store isbn book
            List<Book> bookList = new ArrayList<Book>();
            String[] isbnArr = isbns.split(Constant.SPLIT_CHAR);
            for (String isbn : isbnArr) {
                Book book = BookUtils.getBookByIsbn(isbn);
                if (book != null) {
                    bookList.add(book);
                }
            }
            //update new hotbook from douban
            for (Book book : bookList) {
                HotBook hotBook = hotBookDao.findByBookId(book.getBookId());
                if (hotBook == null) {
                    hotBook = new HotBook();
                    hotBook.setBookId(book.getBookId());
                }
                hotBook.setSource(SourceType.DouBan.getType());
                hotBook.setUpdatedTime(TimeUtils.getCurrentDate());
                hotBook = hotBookDao.save(hotBook);
            }
        }
    }

    //删除旧热门书籍
    public static void deleteOldHotBook(int sourceType, Date date) {
        List<HotBook> hotBookList = hotBookDao.findBySourceAndUpdatedTimeLessThan(sourceType, date);
        if (hotBookList != null && hotBookList.size() > 0) {
            for (HotBook hotBook : hotBookList) {
                hotBookDao.delete(hotBook);
            }
        }
    }

    //更新热门书籍hotbook
    public static void updateHotBookGet() {
        List<HotBook> hotBookList = hotBookDao.findAll();
        int size = hotBookList.size();
        Set<Integer> set = new HashSet<Integer>();
        Random random = new Random();
        int getNum = size < Constant.HOT_BOOK_NUM ? size : Constant.HOT_BOOK_NUM;
        while (set.size() < getNum) {
            set.add(random.nextInt(size));
        }
        for (int index = 0; index < size; index++) {
            HotBook hotBook = hotBookList.get(index);
            if (set.contains(index)) {
                hotBook.setGet(true);
            } else {
                hotBook.setGet(false);
            }
        }
        hotBookDao.save(hotBookList);
    }
}
