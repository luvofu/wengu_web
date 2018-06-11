package com.wg.bookgroup.utils;

import com.alibaba.fastjson.JSON;
import com.wg.book.domain.Book;
import com.wg.book.utils.BookUtils;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.common.Constant;
import com.wg.common.FileConfig;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.userbook.domain.UserBook;
import com.wg.userbook.model.CategoryStatis;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/9/20.
 */
public class BookGroupUtils {
    //tag to csMap
    public static HashMap<String, Long> getTagCsMap(String newTag) {
        HashMap<String, Long> csMap = new HashMap<String, Long>();
        if (StringUtils.isNotBlank(newTag)) {
            for (String tag : newTag.split(Constant.SPLIT_CHAR)) {
                for (String secClass : FileConfig.secClassList) {
                    if (tag.contains(secClass)) {
                        String topClass = FileConfig.map.get(secClass);
                        if (csMap.containsKey(topClass)) {
                            csMap.put(topClass, csMap.get(topClass) + 1);
                        } else {
                            csMap.put(topClass, 1L);
                        }
                    }
                }
            }
        }
        return csMap;
    }

    //通过分类统计获得最大类别
    public static String getMaxCategory(HashMap<String, Long> csMap) {
        String maxKey = BookUtils.NULL_CATEGORY;
        if (csMap != null && csMap.size() > 0) {
            long maxValue = 0;
            for (String key : csMap.keySet()) {
                long value = csMap.get(key);
                if (value > maxValue) {
                    maxKey = key;
                    maxValue = value;
                }
            }
        }
        return maxKey;
    }

    //通过标签更新分类
    public static String updateCs(BookGroup bookGroup, String newTag) {
        HashMap<String, Long> csMap = getBookGroupCsMap(bookGroup);
        HashMap<String, Long> newMap = getTagCsMap(newTag);
        String oldCategory = getMaxCategory(csMap);
        for (String newkey : newMap.keySet()) {
            if (csMap.containsKey(newkey)) {
                csMap.put(newkey, csMap.get(newkey) + newMap.get(newkey));
            } else {
                csMap.put(newkey, newMap.get(newkey));
            }
        }
        String category = getMaxCategory(csMap);
        if (bookGroup != null) {
            bookGroup.setCategoryStatis(getCategoryStatis(csMap));
            bookGroup = UpdateUtils.updateBookCommunity(bookGroup);
            if (!category.equals(oldCategory)) {
                List<Book> bookList = bookDao.findByCommunityId(bookGroup.getCommunityId());
                updateAllCategory(bookList, category);
            }
        }
        return category;
    }

    //通过新书标签，更新分类
    public static void updateCs(Book book) {
        if (book != null) {
            BookGroup bookGroup = bookGroupDao.findOne(book.getCommunityId());
            String newTag = book.getTag();
            String category = updateCs(bookGroup, newTag);
            book.setCategory(category);
            book = UpdateUtils.updateBook(book);
        }
    }

    //通过藏书标签,更新分类
    public static void updateCs(UserBook userBook) {
        Book book = bookDao.findOne(userBook.getBookId());
        if (book != null) {
            BookGroup bookGroup = bookGroupDao.findOne(book.getCommunityId());
            String newTag = userBook.getTag();
            //update if comm exist
            if (bookGroup != null) {
                updateCs(bookGroup, newTag);
            }
        }
    }

    //通过社区统计,更新分类
    public static void updateCs(BookGroup bookGroup) {
        List<Book> bookList = bookDao.findByCommunityId(bookGroup.getCommunityId());
        HashMap<String, Long> csMap = new HashMap<String, Long>();
        for (Book book : bookList) {
            HashMap<String, Long> newMap = getTagCsMap(book.getTag());
            for (String key : newMap.keySet()) {
                if (csMap.containsKey(key)) {
                    csMap.put(key, csMap.get(key) + newMap.get(key));
                } else {
                    csMap.put(key, newMap.get(key));
                }
            }
        }
        String maxCategory = getMaxCategory(csMap);
        updateAllCategory(bookList, maxCategory);
        bookGroup.setCategoryStatis(getCategoryStatis(csMap));
        UpdateUtils.updateBookCommunity(bookGroup);
    }

    //book group csMap
    public static HashMap<String, Long> getBookGroupCsMap(BookGroup bookGroup) {
        HashMap<String, Long> csMap = new HashMap<String, Long>();
        try {
            if (bookGroup != null) {
                List<CategoryStatis> categoryStatisList = JSON.parseArray(bookGroup.getCategoryStatis(), CategoryStatis.class);
                for (CategoryStatis categoryStatis : categoryStatisList) {
                    csMap.put(categoryStatis.getCategory(), categoryStatis.getStatis());
                }
            }
        } catch (Exception e) {
        }
        return csMap;
    }

    //csMap to csList
    public static String getCategoryStatis(HashMap<String, Long> csMap) {
        List<CategoryStatis> categoryStatisList = new ArrayList<CategoryStatis>();
        if (csMap != null)
            for (String key : csMap.keySet()) {
                categoryStatisList.add(new CategoryStatis(key, csMap.get(key)));
            }
        return JSON.toJSONString(categoryStatisList);
    }

    //更新所有相关category
    public static void updateAllCategory(List<Book> bookList, String maxCategory) {
        if (bookList != null) {
            for (Book book : bookList) {
                book.setCategory(maxCategory);
                book = UpdateUtils.updateBook(book);
                //update userbook commbook category
                userBookDao.updateCategory(book.getCategory(), book.getBookId());
                commBookDao.updateCategory(book.getCategory(), book.getBookId());
            }
        }
    }

    //书籍社区重定向
    public static boolean redirectBookCommunity(Book book, BookGroup bookGroup) {
        if (book != null) {
            try {
                BookGroup fromBookGroup = bookGroupDao.findOne(book.getCommunityId());
                if (bookGroup == null) {
                    bookGroup = AddUtils.addBookCommunity(book);
                }

                book.setCommunityId(bookGroup.getCommunityId());
                book = UpdateUtils.updateBook(book);

                //update bookcomm categorystatis
                BookGroupUtils.updateCs(bookGroup);

                if (fromBookGroup != null) {
                    //redirect comment
                    List<GroupComment> groupCommentList = groupCommentDao.findByCommunityId(fromBookGroup.getCommunityId());
                    for (GroupComment groupComment : groupCommentList) {
                        groupComment.setCommunityId(bookGroup.getCommunityId());
                        groupComment = groupCommentDao.save(groupComment);
                    }
                    // delete oldbookcommunity if book count==0
                    if (bookDao.countByCommunityId(fromBookGroup.getCommunityId()) == 0) {
                        DeleteUtils.deleteBookCommunity(fromBookGroup);
                    } else {
                        //update old bookcomm category statis
                        BookGroupUtils.updateCs(fromBookGroup);
                    }
                }
                return true;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return false;
    }

    //书籍社区合并
    public static boolean combinBookCommunity(BookGroup fromBookGroup, BookGroup bookGroup) {
        if (fromBookGroup != null && bookGroup != null) {
            try {
                //redirect book comm
                List<Book> bookList = bookDao.findByCommunityId(fromBookGroup.getCommunityId());
                for (Book book : bookList) {
                    book.setCommunityId(bookGroup.getCommunityId());
                    book = UpdateUtils.updateBook(book);

                }
                //update bookcomm categorystatis
                BookGroupUtils.updateCs(bookGroup);
                //redirect comment
                List<GroupComment> groupCommentList = groupCommentDao.findByCommunityId(fromBookGroup.getCommunityId());
                for (GroupComment groupComment : groupCommentList) {
                    groupComment.setCommunityId(bookGroup.getCommunityId());
                    groupComment = groupCommentDao.save(groupComment);
                }
                // delete oldbookcommunity
                DeleteUtils.deleteBookCommunity(fromBookGroup);
                return true;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return false;
    }
}
